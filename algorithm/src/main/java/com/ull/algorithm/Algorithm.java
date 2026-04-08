package com.ull.algorithm;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Clase que representa el algoritmo en sí. Recibe un problema en el constructor y
 * devuelve una solución a través del run().
 */
public class Algorithm {

  private final DeliveryPlanningProblem problem;
  private static final double AVERAGE_SPEED_KMH = 60.0;
  private static final int MINUTES_PER_HOUR = 60;
  private static final int MIN_TRAVEL_TIME = 15;

  public Algorithm(DeliveryPlanningProblem problem) {
    this.problem = problem;
  }

  public DeliveryPlanningSolution run() {
    DeliveryPlanningSolution solution = new DeliveryPlanningSolution();

    JSONArray orders = problem.getOrders();
    JSONArray trucks = problem.getTrucks();

    if (orders.length() == 0 || trucks.length() == 0) {
      solution.setStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
      return solution;
    }

    JSONObject laundry = orders.getJSONObject(0).getJSONObject("laundry");
    JSONObject laundryPos = laundry.getJSONObject("position");

    List<JSONObject> sortedOrders = new ArrayList<>();
    for (int i = 0; i < orders.length(); i++) {
      sortedOrders.add(orders.getJSONObject(i));
    }

    sortedOrders.sort((a, b) -> {
      LocalDateTime timeA = LocalDateTime.parse(a.getJSONObject("timeWindow").getString("start"));
      LocalDateTime timeB = LocalDateTime.parse(b.getJSONObject("timeWindow").getString("start"));
      return timeA.compareTo(timeB);
    });

    double maxTruckCapacity = 0;
    for (int i = 0; i < trucks.length(); i++) {
      double capacity = trucks.getJSONObject(i).getDouble("capacity");
      maxTruckCapacity = Math.max(maxTruckCapacity, capacity);
    }
    double maxHalfCapacity = maxTruckCapacity / 2.0;

    Map<String, List<JSONObject>> truckAssignments = new HashMap<>();
    for (int i = 0; i < trucks.length(); i++) {
      truckAssignments.put(trucks.getJSONObject(i).getString("id"), new ArrayList<>());
    }

    Set<String> closedTrucks = new HashSet<>();

    for (JSONObject order : sortedOrders) {
      double deliveryVol = order.optDouble("deliveryVolume", 0.0);
      double pickupVol = order.optDouble("pickupVolume", 0.0);

      if (deliveryVol > maxHalfCapacity || pickupVol > maxHalfCapacity) {
        solution.setStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
        return solution;
      }

      String type = order.getJSONObject("customer").getString("type");
      boolean assigned = false;

      for (int i = 0; i < trucks.length(); i++) {
        JSONObject truck = trucks.getJSONObject(i);
        String truckId = truck.getString("id");
        double capacity = truck.getDouble("capacity");

        if (closedTrucks.contains(truckId)) {
          continue;
        }

        List<JSONObject> assignedOrders = truckAssignments.get(truckId);

        if (!assignedOrders.isEmpty() &&
            !type.equals(assignedOrders.get(0).getJSONObject("customer").getString("type")))
          continue;

        boolean inserted = tryInsertOrder(assignedOrders, order, capacity, laundryPos);

        if (inserted) {
          assigned = true;

          boolean canContinue = validateRouteCanStillPickup(assignedOrders, capacity, laundryPos);
          if (!canContinue) {
            closedTrucks.add(truckId);
          }

          break;
        }
      }

      if (!assigned) {
        solution.setStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
        return solution;
      }
    }

    for (int i = 0; i < trucks.length(); i++) {
      JSONObject truck = trucks.getJSONObject(i);
      String truckId = truck.getString("id");
      double capacity = truck.getDouble("capacity");

      List<JSONObject> route = truckAssignments.get(truckId);
      if (route.isEmpty())
        continue;

      double totalDelivery = route.stream().mapToDouble(o -> o.optDouble("deliveryVolume", 0.0)).sum();
      double clean = totalDelivery;
      double dirty = 0.0;

      LocalDateTime time = getInitialTruckTime(route, laundryPos);
      JSONObject lastPos = laundryPos;

      for (JSONObject order : route) {
        double deliveryVol = order.optDouble("deliveryVolume", 0.0);
        double pickupVol = order.optDouble("pickupVolume", 0.0);
        JSONObject timeWindow = order.getJSONObject("timeWindow");
        LocalDateTime start = LocalDateTime.parse(timeWindow.getString("start"));
        LocalDateTime end = LocalDateTime.parse(timeWindow.getString("end"));
        int serviceTime = order.getInt("serviceTime");
        JSONObject pos = order.getJSONObject("customer").getJSONObject("position");

        double distance = calculateDistance(lastPos, pos);
        int travelTime = Math.max((int) (distance / AVERAGE_SPEED_KMH * MINUTES_PER_HOUR), MIN_TRAVEL_TIME);

        time = time.plusMinutes(travelTime);
        if (time.isBefore(start))
          time = start;
        LocalDateTime departure = time.plusMinutes(serviceTime);

        clean -= deliveryVol;
        dirty += pickupVol;

        if (clean + dirty > capacity || departure.isAfter(end)) {
          System.out.println("FINAL INFEASIBLE → Order " + order.getString("id") +
              " truckTime=" + time +
              " departure=" + departure +
              " windowEnd=" + end +
              " clean=" + clean +
              " dirty=" + dirty +
              " capacity=" + capacity);
          solution.setStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
          return solution;
        }

        double remainingClean = Math.max(clean, 0);
        double dirtySpace = capacity - (remainingClean + dirty);

        solution.addRouteStop(
            truckId,
            order.getString("id"),
            time.format(DateTimeFormatter.ISO_DATE_TIME),
            departure.format(DateTimeFormatter.ISO_DATE_TIME),
            remainingClean,
            dirtySpace,
            distance,
            pickupVol > 0,
            deliveryVol > 0,
            pickupVol,
            deliveryVol);

        time = departure;
        lastPos = pos;
      }
    }

    solution.setStatus(DeliveryPlanningSolution.Status.OPTIMAL);
    return solution;
  }

  private boolean tryInsertOrder(List<JSONObject> assignedList, JSONObject order, double capacity,
      JSONObject laundryPos) {
    return tryInsertOrderRecursive(assignedList, order, capacity, laundryPos, 0, 0);
  }

  private boolean tryInsertOrderRecursive(List<JSONObject> assignedList, JSONObject order, double capacity,
      JSONObject laundryPos, int level, int maxLevel) {
    for (int pos = 0; pos <= assignedList.size(); pos++) {
      List<JSONObject> trialList = new ArrayList<>(assignedList);
      trialList.add(pos, order);

      if (validateRoute(trialList, capacity, laundryPos)) {
        assignedList.clear();
        assignedList.addAll(trialList);
        return true;
      }
    }
    return false;
  }

  private boolean validateRoute(List<JSONObject> route, double capacity, JSONObject laundryPos) {
    double clean = route.stream().mapToDouble(o -> o.optDouble("deliveryVolume", 0.0)).sum();
    double dirty = 0.0;

    LocalDateTime time = getInitialTruckTime(route, laundryPos);
    JSONObject lastPos = laundryPos;

    for (JSONObject order : route) {
      double deliveryVol = order.optDouble("deliveryVolume", 0.0);
      double pickupVol = order.optDouble("pickupVolume", 0.0);
      JSONObject timeWindow = order.getJSONObject("timeWindow");
      LocalDateTime start = LocalDateTime.parse(timeWindow.getString("start"));
      LocalDateTime end = LocalDateTime.parse(timeWindow.getString("end"));
      int serviceTime = order.getInt("serviceTime");
      JSONObject pos = order.getJSONObject("customer").getJSONObject("position");

      double distance = calculateDistance(lastPos, pos);
      int travelTime = Math.max((int) (distance / AVERAGE_SPEED_KMH * MINUTES_PER_HOUR), MIN_TRAVEL_TIME);

      time = time.plusMinutes(travelTime);
      if (time.isBefore(start))
        time = start;
      LocalDateTime departure = time.plusMinutes(serviceTime);

      clean -= deliveryVol;
      dirty += pickupVol;

      if (clean + dirty > capacity || departure.isAfter(end)) {
        System.out.println("INFEASIBLE → Order " + order.getString("id") +
            " truckTime=" + time +
            " departure=" + departure +
            " windowEnd=" + end +
            " clean=" + clean +
            " dirty=" + dirty +
            " capacity=" + capacity);
        return false;
      }

      time = departure;
      lastPos = pos;
    }

    return true;
  }

  private boolean validateRouteCanStillPickup(List<JSONObject> route, double capacity, JSONObject laundryPos) {
    double clean = route.stream().mapToDouble(o -> o.optDouble("deliveryVolume", 0.0)).sum();
    double dirty = 0.0;

    LocalDateTime time = getInitialTruckTime(route, laundryPos);
    JSONObject lastPos = laundryPos;

    for (JSONObject order : route) {
      double deliveryVol = order.optDouble("deliveryVolume", 0.0);
      double pickupVol = order.optDouble("pickupVolume", 0.0);
      JSONObject timeWindow = order.getJSONObject("timeWindow");
      LocalDateTime start = LocalDateTime.parse(timeWindow.getString("start"));
      LocalDateTime end = LocalDateTime.parse(timeWindow.getString("end"));
      int serviceTime = order.getInt("serviceTime");
      JSONObject pos = order.getJSONObject("customer").getJSONObject("position");

      double distance = calculateDistance(lastPos, pos);
      int travelTime = Math.max((int) (distance / AVERAGE_SPEED_KMH * MINUTES_PER_HOUR), MIN_TRAVEL_TIME);

      time = time.plusMinutes(travelTime);
      if (time.isBefore(start))
        time = start;
      LocalDateTime departure = time.plusMinutes(serviceTime);

      clean -= deliveryVol;
      dirty += pickupVol;

      if (clean + dirty > capacity || departure.isAfter(end)) {
        return false;
      }

      time = departure;
      lastPos = pos;
    }

    double remainingClean = Math.max(clean, 0);
    double freeSpace = capacity - (remainingClean + dirty);

    return freeSpace > 0;
  }

  private double calculateDistance(JSONObject pos1, JSONObject pos2) {
    final double R = 6371;
    double lat1 = Math.toRadians(pos1.getDouble("latitude"));
    double lon1 = Math.toRadians(pos1.getDouble("longitude"));
    double lat2 = Math.toRadians(pos2.getDouble("latitude"));
    double lon2 = Math.toRadians(pos2.getDouble("longitude"));

    double dlat = lat2 - lat1;
    double dlon = lon2 - lon1;

    double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
        Math.cos(lat1) * Math.cos(lat2) *
            Math.sin(dlon / 2) * Math.sin(dlon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return Math.max(R * c, 0.1);
  }

  private LocalDateTime getInitialTruckTime(List<JSONObject> route, JSONObject laundryPos) {
    if (route.isEmpty()) {
      return LocalDateTime.now().withHour(5).withMinute(0).withSecond(0).withNano(0);
    } else {
      JSONObject firstOrder = route.get(0);
      String startStr = firstOrder.getJSONObject("timeWindow").getString("start");
      LocalDateTime start = LocalDateTime.parse(startStr);
      return start.minusMinutes(30);
    }
  }
}
