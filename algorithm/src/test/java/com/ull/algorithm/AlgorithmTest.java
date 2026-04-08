package com.ull.algorithm;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

class AlgorithmTest {

  private JSONObject createLaundry(String id, String name, double lat, double lon) {
    JSONObject laundry = new JSONObject();
    laundry.put("id", id);
    laundry.put("name", name);
    JSONObject pos = new JSONObject();
    pos.put("latitude", lat);
    pos.put("longitude", lon);
    laundry.put("position", pos);
    return laundry;
  }

  private JSONObject createCustomer(String id, String name, String type, double lat, double lon) {
    JSONObject customer = new JSONObject();
    customer.put("id", id);
    customer.put("name", name);
    customer.put("type", type);
    JSONObject pos = new JSONObject();
    pos.put("latitude", lat);
    pos.put("longitude", lon);
    customer.put("position", pos);
    return customer;
  }

  private JSONObject createTruck(String id, String name, double capacity) {
    JSONObject truck = new JSONObject();
    truck.put("id", id);
    truck.put("name", name);
    truck.put("capacity", capacity);
    return truck;
  }

  private double calculateExpectedDistance(JSONObject point1, JSONObject point2) {
    final double R = 6371; // Radio de la Tierra en kilómetros
    double lat1 = Math.toRadians(point1.getJSONObject("position").getDouble("latitude"));
    double lon1 = Math.toRadians(point1.getJSONObject("position").getDouble("longitude"));
    double lat2 = Math.toRadians(point2.getJSONObject("position").getDouble("latitude"));
    double lon2 = Math.toRadians(point2.getJSONObject("position").getDouble("longitude"));

    double dlon = lon2 - lon1;
    double dlat = lat2 - lat1;

    double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
        Math.cos(lat1) * Math.cos(lat2) *
            Math.sin(dlon / 2) * Math.sin(dlon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }

  @Test
  void shouldCreateFeasibleSolutionForSimpleProblem() {
    // Create a simple problem with one truck, one laundry, and two customers
    JSONObject problemData = new JSONObject();

    JSONArray trucks = new JSONArray();
    trucks.put(createTruck("truck1", "Truck 1", 1000.0));

    JSONObject laundry = createLaundry("laundryOne", "Central Laundry", 28.4721, -16.2981);

    JSONObject customerOne = createCustomer("customerOne", "Hotel ABC", "HOTEL", 28.4698, -16.2574);
    JSONObject customerTwo = createCustomer("customerTwo", "Hotel XYZ", "HOTEL", 28.4827, -16.3112);

    JSONArray orders = new JSONArray();

    // First order: delivery only
    JSONObject order1 = new JSONObject();
    order1.put("id", "order1");
    order1.put("customer", customerOne);
    order1.put("laundry", laundry);
    order1.put("deliveryVolume", 200.0);
    JSONObject timeWindow1 = new JSONObject();
    LocalDateTime now = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
    timeWindow1.put("start", now.format(DateTimeFormatter.ISO_DATE_TIME));
    timeWindow1.put("end", now.plusHours(2).format(DateTimeFormatter.ISO_DATE_TIME));
    order1.put("timeWindow", timeWindow1);
    order1.put("serviceTime", 30);
    orders.put(order1);

    // Second order: delivery and pickup
    JSONObject order2 = new JSONObject();
    order2.put("id", "order2");
    order2.put("customer", customerTwo);
    order2.put("laundry", laundry);
    order2.put("deliveryVolume", 150.0);
    order2.put("pickupVolume", 100.0);
    JSONObject timeWindow2 = new JSONObject();
    timeWindow2.put("start", now.plusHours(1).format(DateTimeFormatter.ISO_DATE_TIME));
    timeWindow2.put("end", now.plusHours(3).format(DateTimeFormatter.ISO_DATE_TIME));
    order2.put("timeWindow", timeWindow2);
    order2.put("serviceTime", 45);
    orders.put(order2);

    problemData.put("trucks", trucks);
    problemData.put("orders", orders);

    // Create the problem and run the algorithm
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem();
    problem.setData(problemData);

    Algorithm algorithm = new Algorithm(problem);
    DeliveryPlanningSolution solution = algorithm.run();

    // Basic assertions
    assertEquals(DeliveryPlanningSolution.Status.OPTIMAL, solution.getStatus());
    JSONArray routes = solution.getRoutes();
    assertEquals(1, routes.length());
    JSONObject route = routes.getJSONObject(0);
    assertEquals("truck1", route.getString("truck"));

    // Check the stops in the route
    JSONArray stops = route.getJSONArray("stops");
    assertEquals(2, stops.length());

    // Check the first stop
    JSONObject stop1 = stops.getJSONObject(0);

    assertEquals("order1", stop1.getString("order"));
    assertTrue(stop1.getBoolean("hasDelivery"));
    assertFalse(stop1.getBoolean("hasPickup"));
    assertEquals(200.0, stop1.getDouble("deliveryVolume"));
    assertEquals(150.0, stop1.getDouble("remainingCleanLoad")); // After delivering 200, 150 clean laundry remains
    assertEquals(850.0, stop1.getDouble("availableDirtySpace")); // Space available for dirty laundry (1000 - 150 clean)

    // Check the second stop
    JSONObject stop2 = stops.getJSONObject(1);

    assertEquals("order2", stop2.getString("order"));
    assertTrue(stop2.getBoolean("hasDelivery"));
    assertTrue(stop2.getBoolean("hasPickup"));
    assertEquals(150.0, stop2.getDouble("deliveryVolume"));
    assertEquals(100.0, stop2.getDouble("pickupVolume"));
    assertEquals(0.0, stop2.getDouble("remainingCleanLoad")); // All clean laundry delivered
    assertEquals(900.0, stop2.getDouble("availableDirtySpace")); // Space available (1000 - 100 dirty)

    // Check the time windows
    LocalDateTime stop1Arrival = LocalDateTime.parse(stop1.getString("arrivalTime"), DateTimeFormatter.ISO_DATE_TIME);
    LocalDateTime stop1Departure = LocalDateTime.parse(stop1.getString("departureTime"),
        DateTimeFormatter.ISO_DATE_TIME);
    LocalDateTime stop2Arrival = LocalDateTime.parse(stop2.getString("arrivalTime"), DateTimeFormatter.ISO_DATE_TIME);
    LocalDateTime stop2Departure = LocalDateTime.parse(stop2.getString("departureTime"),
        DateTimeFormatter.ISO_DATE_TIME);

    assertTrue(stop1Arrival.isBefore(stop1Departure));
    assertTrue(stop1Departure.isBefore(stop2Arrival));
    assertTrue(stop2Arrival.isBefore(stop2Departure));

    // Verificar ventanas de tiempo
    assertTrue(stop1Arrival.isAfter(now) || stop1Arrival.equals(now));
    assertTrue(stop1Departure.isBefore(now.plusHours(2)));
    assertTrue(stop2Arrival.isAfter(now.plusHours(1)) || stop2Arrival.equals(now.plusHours(1)));
    assertTrue(stop2Departure.isBefore(now.plusHours(3)));

    // Check distances
    double expectedDistance1 = calculateExpectedDistance(laundry, customerOne);
    double expectedDistance2 = calculateExpectedDistance(customerOne, customerTwo);
    assertEquals(expectedDistance1, stop1.getDouble("distanceFromPrevious"), 0.1);
    assertEquals(expectedDistance2, stop2.getDouble("distanceFromPrevious"), 0.1);
  }

  @Test
  void shouldReturnInfeasibleForImpossibleTimeWindows() {
    JSONObject problemData = new JSONObject();

    JSONArray trucks = new JSONArray();
    trucks.put(createTruck("truck1", "Truck 1", 1000.0));

    JSONObject laundry = createLaundry("laundryOne", "Central Laundry", 28.4721, -16.2981);

    // A customer with a time window that is impossible to meet
    JSONObject customer = createCustomer("customerOne", "Hotel ABC", "HOTEL", 28.4698, -16.2574);

    JSONArray orders = new JSONArray();
    JSONObject order = new JSONObject();
    order.put("id", "order1");
    order.put("customer", customer);
    order.put("laundry", laundry);
    order.put("deliveryVolume", 200.0);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime past = now.minusHours(2);

    JSONObject timeWindow = new JSONObject();
    timeWindow.put("start", past.format(DateTimeFormatter.ISO_DATE_TIME));
    timeWindow.put("end", past.plusHours(0).format(DateTimeFormatter.ISO_DATE_TIME));
    order.put("timeWindow", timeWindow);
    order.put("serviceTime", 30);
    orders.put(order);

    problemData.put("trucks", trucks);
    problemData.put("orders", orders);

    DeliveryPlanningProblem problem = new DeliveryPlanningProblem();
    problem.setData(problemData);

    Algorithm algorithm = new Algorithm(problem);
    DeliveryPlanningSolution solution = algorithm.run();

    assertEquals(DeliveryPlanningSolution.Status.INFEASIBLE, solution.getStatus());
  }

  @Test
  void shouldReturnInfeasibleForExceededCapacity() {
    JSONObject problemData = new JSONObject();

    // Truck with limited capacity
    JSONArray trucks = new JSONArray();
    trucks.put(createTruck("truck1", "Small Truck", 100.0));

    JSONObject laundry = createLaundry("laundryOne", "Central Laundry", 28.4721, -16.2981);

    // Customer with a delivery volume that exceeds the truck's capacity
    JSONObject customer = createCustomer("customerOne", "Hotel ABC", "HOTEL", 28.4698, -16.2574);

    JSONArray orders = new JSONArray();
    JSONObject order = new JSONObject();
    order.put("id", "order1");
    order.put("customer", customer);
    order.put("laundry", laundry);
    order.put("deliveryVolume", 150.0); // Exceeds truck capacity

    LocalDateTime now = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
    JSONObject timeWindow = new JSONObject();
    timeWindow.put("start", now.format(DateTimeFormatter.ISO_DATE_TIME));
    timeWindow.put("end", now.plusHours(2).format(DateTimeFormatter.ISO_DATE_TIME));
    order.put("timeWindow", timeWindow);
    order.put("serviceTime", 30);
    orders.put(order);

    problemData.put("trucks", trucks);
    problemData.put("orders", orders);

    DeliveryPlanningProblem problem = new DeliveryPlanningProblem();
    problem.setData(problemData);

    Algorithm algorithm = new Algorithm(problem);
    DeliveryPlanningSolution solution = algorithm.run();

    assertEquals(DeliveryPlanningSolution.Status.INFEASIBLE, solution.getStatus());
  }

  @Test
  void shouldHandleMultipleTrucks() {
    JSONObject problemData = new JSONObject();

    JSONArray trucks = new JSONArray();
    trucks.put(createTruck("truck1", "Truck 1", 1000.0));
    trucks.put(createTruck("truck2", "Truck 2", 700.0));

    JSONObject laundry = createLaundry("laundryOne", "Central Laundry", 28.4721, -16.2981);

    // Three customers that require at least 3 different trucks
    JSONObject customerOne = createCustomer("customerOne", "Hotel ABC", "HOTEL", 28.4698, -16.2574);
    JSONObject customerTwo = createCustomer("customerTwo", "Hotel XYZ", "HOTEL", 28.4827, -16.3112);
    JSONObject customerThree = createCustomer("customerThree", "Hotel DEF", "HOTEL", 28.4756, -16.2845);

    JSONArray orders = new JSONArray();
    LocalDateTime now = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);

    // First order: Big delivery
    JSONObject order1 = new JSONObject();
    order1.put("id", "order1");
    order1.put("customer", customerOne);
    order1.put("laundry", laundry);
    order1.put("deliveryVolume", 400.0);
    JSONObject timeWindow1 = new JSONObject();
    timeWindow1.put("start", now.format(DateTimeFormatter.ISO_DATE_TIME));
    timeWindow1.put("end", now.plusHours(2).format(DateTimeFormatter.ISO_DATE_TIME));
    order1.put("timeWindow", timeWindow1);
    order1.put("serviceTime", 30);
    orders.put(order1);

    // Second order: delivery and pickup
    JSONObject order2 = new JSONObject();
    order2.put("id", "order2");
    order2.put("customer", customerTwo);
    order2.put("laundry", laundry);
    order2.put("deliveryVolume", 300.0);
    order2.put("pickupVolume", 200.0);
    JSONObject timeWindow2 = new JSONObject();
    timeWindow2.put("start", now.plusMinutes(30).format(DateTimeFormatter.ISO_DATE_TIME));
    timeWindow2.put("end", now.plusHours(3).format(DateTimeFormatter.ISO_DATE_TIME));
    order2.put("timeWindow", timeWindow2);
    order2.put("serviceTime", 45);
    orders.put(order2);

    // Third order: delivery only
    JSONObject order3 = new JSONObject();
    order3.put("id", "order3");
    order3.put("customer", customerThree);
    order3.put("laundry", laundry);
    order3.put("pickupVolume", 100.0);
    JSONObject timeWindow3 = new JSONObject();
    timeWindow3.put("start", now.plusHours(1).format(DateTimeFormatter.ISO_DATE_TIME));
    timeWindow3.put("end", now.plusHours(4).format(DateTimeFormatter.ISO_DATE_TIME));
    order3.put("timeWindow", timeWindow3);
    order3.put("serviceTime", 30);
    orders.put(order3);

    problemData.put("trucks", trucks);
    problemData.put("orders", orders);

    DeliveryPlanningProblem problem = new DeliveryPlanningProblem();
    problem.setData(problemData);

    Algorithm algorithm = new Algorithm(problem);
    DeliveryPlanningSolution solution = algorithm.run();

    // Verify that the solution is optimal
    assertEquals(DeliveryPlanningSolution.Status.OPTIMAL, solution.getStatus());

    // Verify that there is one route
    JSONArray routes = solution.getRoutes();
    assertEquals(1, routes.length());

    // Verify that all orders are assigned
    Set<String> assignedOrders = new HashSet<>();
    for (int i = 0; i < routes.length(); i++) {
      JSONObject route = routes.getJSONObject(i);
      JSONArray stops = route.getJSONArray("stops");
      for (int j = 0; j < stops.length(); j++) {
        assignedOrders.add(stops.getJSONObject(j).getString("order"));
      }
    }
    assertEquals(3, assignedOrders.size());
    assertTrue(assignedOrders.contains("order1"));
    assertTrue(assignedOrders.contains("order2"));
    assertTrue(assignedOrders.contains("order3"));

    // Verify that the remaining capacities are valid
    for (int i = 0; i < routes.length(); i++) {
      JSONObject route = routes.getJSONObject(i);
      JSONArray stops = route.getJSONArray("stops");
      for (int j = 0; j < stops.length(); j++) {
        JSONObject stop = stops.getJSONObject(j);
        assertTrue(stop.getDouble("remainingCleanLoad") >= 0);
        assertTrue(stop.getDouble("availableDirtySpace") >= 0);
      }
    }
  }
}