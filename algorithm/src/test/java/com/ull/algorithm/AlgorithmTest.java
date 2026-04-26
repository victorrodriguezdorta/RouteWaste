package com.ull.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.entity.FacilityWithVehicles;
import com.ull.domain.entity.Vehicle;
import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.location.Location;
import java.util.List;
import org.junit.jupiter.api.Test;

class AlgorithmTest {

  // -------------------------------------------------------------------------
  // Factories
  // -------------------------------------------------------------------------

  private Location location(double lat, double lon) {
    return new Location(lat, lon, "Test Address", "GIS-TEST");
  }

  private Facility facility(String id) {
    return new Facility(
        id,
        "TRANSFER_STATION",
        location(28.47, -16.25),
        5000.0,
        2000.0,
        30,
        50000.0,
        "OPEN",
        0.0);
  }

  private Vehicle vehicle(String id, String type) {
    return new Vehicle(id, type, 1000.0, 1000.0, 0.5);
  }

  private Container container(String id, double lat, double lon) {
    return new Container(
        id,
        location(lat, lon),
        WasteType.ORGANIC,
        500.0,
        50.0,
        "DISTRICT");
  }

  // -------------------------------------------------------------------------
  // Tests
  // -------------------------------------------------------------------------

  @Test
  void shouldReturnSuboptimalSolutionForSimpleProblem() {
    Facility f = facility("facility-1");
    Vehicle v = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container c1 = container("container-1", 28.465, -16.263);
    Container c2 = container("container-2", 28.462, -16.264);

    FacilityWithVehicles fwv = new FacilityWithVehicles(f, List.of(v));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15, 7, List.of(fwv), List.of(c1, c2));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    // Status should be SUBOPTIMAL (stub algorithm)
    assertEquals(DeliveryPlanningSolution.Status.SUBOPTIMAL, solution.getStatus());
    assertNotNull(solution.getExecutedAt());
  }

  @Test
  void shouldCreateOneClusterPerAlgorithmCall() {
    Facility f = facility("facility-1");
    Vehicle v = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container c1 = container("container-1", 28.465, -16.263);
    Container c2 = container("container-2", 28.462, -16.264);

    FacilityWithVehicles fwv = new FacilityWithVehicles(f, List.of(v));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15, 7, List.of(fwv), List.of(c1, c2));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    List<FacilityCluster> clusters = solution.getClusters();
    assertEquals(1, clusters.size());

    FacilityCluster cluster = clusters.get(0);
    assertEquals("facility-1", cluster.getFacility().getId());
    assertEquals(2, cluster.getAssignedContainers().size());
  }

  @Test
  void shouldCreateOneDailyPlanPerVehicle() {
    Facility f = facility("facility-1");
    Vehicle v1 = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Vehicle v2 = vehicle("vehicle-2", "SUPPORT_VEHICLE");
    Container c = container("container-1", 28.465, -16.263);

    FacilityWithVehicles fwv = new FacilityWithVehicles(f, List.of(v1, v2));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15, 7, List.of(fwv), List.of(c));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    List<DailyPlan> dailyPlans = solution.getDailyPlans();
    assertEquals(2, dailyPlans.size());

    // Each plan should have 1 stop (one container)
    for (DailyPlan plan : dailyPlans) {
      assertEquals(1, plan.getStops().size());
      assertEquals(1, plan.getPlanDay());
    }
  }

  @Test
  void shouldReturnInfeasibleWhenNoFacilities() {
    Container c = container("container-1", 28.465, -16.263);
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15, 7, List.of(), List.of(c));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    assertEquals(DeliveryPlanningSolution.Status.INFEASIBLE, solution.getStatus());
    assertTrue(solution.getClusters().isEmpty());
    assertTrue(solution.getDailyPlans().isEmpty());
  }

  @Test
  void shouldReturnInfeasibleWhenNoContainers() {
    Facility f = facility("facility-1");
    Vehicle v = vehicle("vehicle-1", "COLLECTION_TRUCK");
    FacilityWithVehicles fwv = new FacilityWithVehicles(f, List.of(v));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15, 7, List.of(fwv), List.of());

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    assertEquals(DeliveryPlanningSolution.Status.INFEASIBLE, solution.getStatus());
    assertTrue(solution.getClusters().isEmpty());
    assertTrue(solution.getDailyPlans().isEmpty());
  }

  @Test
  void shouldAssignAllContainersToTheCluster() {
    Facility f = facility("facility-1");
    Vehicle v = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container c1 = container("container-1", 28.465, -16.263);
    Container c2 = container("container-2", 28.462, -16.264);
    Container c3 = container("container-3", 28.460, -16.260);

    FacilityWithVehicles fwv = new FacilityWithVehicles(f, List.of(v));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15, 7, List.of(fwv), List.of(c1, c2, c3));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    FacilityCluster cluster = solution.getClusters().get(0);
    assertEquals(3, cluster.getSize());

    List<String> assignedIds = cluster.getAssignedContainers()
        .stream()
        .map(Container::getId)
        .toList();
    assertTrue(assignedIds.contains("container-1"));
    assertTrue(assignedIds.contains("container-2"));
    assertTrue(assignedIds.contains("container-3"));
  }

  @Test
  void dailyPlanStopsShouldMatchContainerCount() {
    Facility f = facility("facility-1");
    Vehicle v = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container c1 = container("container-1", 28.465, -16.263);
    Container c2 = container("container-2", 28.462, -16.264);

    FacilityWithVehicles fwv = new FacilityWithVehicles(f, List.of(v));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15, 7, List.of(fwv), List.of(c1, c2));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    assertFalse(solution.getDailyPlans().isEmpty());
    DailyPlan plan = solution.getDailyPlans().get(0);
    assertEquals(2, plan.getStops().size());
    assertTrue(plan.getTotalDistanceMeters() > 0);
  }
}