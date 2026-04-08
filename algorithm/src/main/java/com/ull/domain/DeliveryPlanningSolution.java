package com.ull.domain;

import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Clase que contiene los datos de la solución al problema.
public class DeliveryPlanningSolution {
  public enum Status {
    OPTIMAL,
    INFEASIBLE,
    SUBOPTIMAL
  }

  private Status status;
  private JSONArray routes;
  private double totalDistance;

  public DeliveryPlanningSolution() {
    this.routes = new JSONArray();
    this.totalDistance = 0.0;
    this.status = Status.INFEASIBLE;
  }

  public void addRoute(String truckId, LocalDateTime startTime, LocalDateTime endTime,
      double[] capacities, String[] orderIds) {
    JSONObject route = new JSONObject();
    route.put("truck", truckId);
    route.put("starting_time", startTime.format(DateTimeFormatter.ISO_DATE_TIME));
    route.put("ending_time", endTime.format(DateTimeFormatter.ISO_DATE_TIME));

    JSONArray capacitiesArray = new JSONArray();
    for (double capacity : capacities) {
      capacitiesArray.put(capacity);
    }
    route.put("capacities", capacitiesArray);

    JSONArray pathArray = new JSONArray();
    for (String orderId : orderIds) {
      pathArray.put(orderId);
    }
    route.put("path", pathArray);

    route.put("metrics", new JSONArray());

    routes.put(route);
  }

  public void addRouteStop(String truckId, String orderId,
      String arrivalTime, String departureTime,
      double remainingCapacityClean, double remainingCapacityDirty,
      double distanceFromPrevious, boolean hasPickup, boolean hasDelivery,
      double pickupVolume, double deliveryVolume) {
    // Buscar la ruta del camión o crear una nueva
    JSONObject route = null;
    for (int i = 0; i < routes.length(); i++) {
      JSONObject r = routes.getJSONObject(i);
      if (r.getString("truck").equals(truckId)) {
        route = r;
        break;
      }
    }

    if (route == null) {
      route = new JSONObject();
      route.put("truck", truckId);
      route.put("stops", new JSONArray());
      routes.put(route);
    }

    // Crear la nueva parada
    JSONObject stop = new JSONObject();
    stop.put("order", orderId);
    stop.put("arrivalTime", arrivalTime);
    stop.put("departureTime", departureTime);
    stop.put("remainingCleanLoad", remainingCapacityClean);
    stop.put("availableDirtySpace", remainingCapacityDirty);
    stop.put("distanceFromPrevious", distanceFromPrevious);
    stop.put("hasPickup", hasPickup);
    stop.put("hasDelivery", hasDelivery);
    stop.put("pickupVolume", pickupVolume);
    stop.put("deliveryVolume", deliveryVolume);

    // Agregar la parada a la ruta
    route.getJSONArray("stops").put(stop);

    // Actualizar la distancia total
    this.totalDistance += distanceFromPrevious;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public JSONArray getRoutes() {
    return routes;
  }

  public double getTotalDistance() {
    return totalDistance;
  }

  public void setTotalDistance(double totalDistance) {
    this.totalDistance = totalDistance;
  }

  public JSONArray toJson() {
    return routes;
  }
}