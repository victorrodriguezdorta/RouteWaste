package com.ull.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import com.ull.domain.DeliveryPlanningStatus;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.ContainerDailyState;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.DailyPlanStop;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.entity.FacilityWithVehicles;
import com.ull.domain.entity.Vehicle;
import com.ull.domain.enumerate.StopType;
import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.cost.MaximumBudget;
import com.ull.domain.valueobject.location.Location;

class AlgorithmTest {

  private static final double DELTA = 0.000001;

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
    return vehicle(id, type, 1000.0, 1000.0);
  }

  private Vehicle vehicle(String id, String type, double capacityKilograms, double capacityLiters) {
    return new Vehicle(id, type, capacityKilograms, capacityLiters, 0.5);
  }

  private Container container(String id, double lat, double lon) {
    return container(id, lat, lon, 500.0, 50.0);
  }

  private Container container(
      String id,
      double lat,
      double lon,
      double capacityLiters,
      double dailyDemandLitersPerDay) {
    return new Container(
        id,
        location(lat, lon),
        WasteType.ORGANIC,
        capacityLiters,
        dailyDemandLitersPerDay,
        "DISTRICT");
  }

  private String firstContainerStopId(DailyPlan plan) {
    return plan.getStops().stream()
        .filter(stop -> stop.getType() == StopType.CONTAINER)
        .findFirst()
        .map(stop -> stop.getContainer().getId())
        .orElseThrow();
  }

  @Test
  void shouldPrioritizeHigherFillPercentageOverCloserContainer() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container nearLowFillContainer = container(
        "container-near-low-fill",
        28.4698,
        -16.2498,
        200.0,
        10.0);
    Container farHighFillContainer = container(
        "container-far-high-fill",
        28.460,
        -16.260,
        100.0,
        90.0);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        1,
        List.of(facilityWithVehicles),
        List.of(nearLowFillContainer, farHighFillContainer),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    DailyPlan dailyPlan = solution.getDailyPlans().get(0);
    assertEquals("container-far-high-fill", firstContainerStopId(dailyPlan));
  }

  @Test
  void shouldPrioritizeOverflowedContainerOverCloserLowFillContainer() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container nearLowFillContainer = container(
        "container-near-low-fill",
        28.4698,
        -16.2498,
        500.0,
        30.0);
    Container farOverflowedContainer = container(
        "container-far-overflowed",
        28.460,
        -16.260,
        50.0,
        65.0);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        1,
        List.of(facilityWithVehicles),
        List.of(nearLowFillContainer, farOverflowedContainer),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    DailyPlan dailyPlan = solution.getDailyPlans().get(0);
    assertEquals("container-far-overflowed", firstContainerStopId(dailyPlan));
    DailyPlanStop firstStop = dailyPlan.getStops().stream()
        .filter(stop -> stop.getType() == StopType.CONTAINER)
        .findFirst()
        .orElseThrow();
    assertTrue(firstStop.getContainerActualLiters() > farOverflowedContainer.getCapacityLiters());
  }

  @Test
  void shouldPreferCloserContainerWhenFillLevelsAreEqual() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container nearContainer = container(
        "container-near",
        28.469,
        -16.252,
        100.0,
        50.0);
    Container farContainer = container(
        "container-far",
        28.460,
        -16.260,
        100.0,
        50.0);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        1,
        List.of(facilityWithVehicles),
        List.of(nearContainer, farContainer),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    DailyPlan dailyPlan = solution.getDailyPlans().get(0);
    assertEquals("container-near", firstContainerStopId(dailyPlan));
  }

  @Test
  void shouldRecordFillLevelBeforeAndAfterCollectionInMonitoring() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container container = container("container-1", 28.465, -16.263, 100.0, 80.0);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        1,
        List.of(facilityWithVehicles),
        List.of(container),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    ContainerDailyState state = solution.getContainerStateMonitoring().get(0);
    assertEquals(0.0, state.getDailyFillingLiters(), DELTA);
    assertEquals(80.0, state.getDailyFillingLitersBeforeCollection(), DELTA);
  }

  @Test
  void shouldReturnSuboptimalSolutionForSimpleProblem() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container firstContainer = container("container-1", 28.465, -16.263);
    Container secondContainer = container("container-2", 28.462, -16.264);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        7,
        List.of(facilityWithVehicles),
        List.of(firstContainer, secondContainer),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    assertEquals(DeliveryPlanningStatus.SUBOPTIMAL, solution.getStatus());
    assertNotNull(solution.getExecutedAt());
    assertEquals(new MaximumBudget(5000.0, "EUR"), solution.getMaxBudget());
  }

  @Test
  void shouldCreateOneClusterPerAlgorithmCall() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container firstContainer = container("container-1", 28.465, -16.263);
    Container secondContainer = container("container-2", 28.462, -16.264);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        7,
        List.of(facilityWithVehicles),
        List.of(firstContainer, secondContainer),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    List<FacilityCluster> clusters = solution.getClusters();
    assertEquals(1, clusters.size());
    assertEquals("facility-1", clusters.get(0).getFacility().getId());
    assertEquals(2, clusters.get(0).getAssignedContainers().size());
  }

  @Test
  void shouldCreateOneDailyPlanPerVehicleAndDay() {
    Facility facility = facility("facility-1");
    Vehicle firstVehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Vehicle secondVehicle = vehicle("vehicle-2", "SUPPORT_VEHICLE");
    Container container = container("container-1", 28.465, -16.263);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(
        facility,
        List.of(firstVehicle, secondVehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        3,
        List.of(facilityWithVehicles),
        List.of(container),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    List<DailyPlan> dailyPlans = solution.getDailyPlans();
    assertEquals(6, dailyPlans.size());
    assertEquals(2, dailyPlans.stream().filter(plan -> plan.getPlanDay() == 1).count());
    assertEquals(2, dailyPlans.stream().filter(plan -> plan.getPlanDay() == 2).count());
    assertEquals(2, dailyPlans.stream().filter(plan -> plan.getPlanDay() == 3).count());
    assertEquals(150.0, solution.getTotalCollectedLiters(), DELTA);
  }

  @Test
  void shouldCreatePlansForAllRequestedDays() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "SUPPORT_VEHICLE", 88.0, 88.0);
    Container firstContainer = container("container-1", 28.465837, -16.263835, 76.0, 67.0);
    Container secondContainer = container("container-2", 28.464925, -16.268236, 100.0, 50.0);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        2,
        List.of(facilityWithVehicles),
        List.of(firstContainer, secondContainer),
        new MaximumBudget(1.0E11, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    assertEquals(2, solution.getDailyPlans().size());
    assertEquals(1, solution.getDailyPlans().get(0).getPlanDay());
    assertEquals(2, solution.getDailyPlans().get(1).getPlanDay());
    assertEquals(117.0, solution.getDailyPlans().get(0).getTotalCollectedLiters(), DELTA);
    assertEquals(117.0, solution.getDailyPlans().get(1).getTotalCollectedLiters(), DELTA);
    assertEquals(234.0, solution.getTotalCollectedLiters(), DELTA);

    List<ContainerDailyState> monitoring = solution.getContainerStateMonitoring();
    assertEquals(4, monitoring.size());
    assertTrue(monitoring.stream().allMatch(state -> Math.abs(state.getDailyFillingLiters()) <= DELTA));
  }

  @Test
  void shouldRecordCollectedKilogramsFromWasteTypeDensity() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK", 1000.0, 1000.0);
    Container container = container("container-1", 28.465, -16.263, 500.0, 80.0);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        1,
        List.of(facilityWithVehicles),
        List.of(container),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    DailyPlan dailyPlan = solution.getDailyPlans().get(0);
    DailyPlanStop containerStop = dailyPlan.getStops().stream()
        .filter(stop -> stop.getType() == StopType.CONTAINER)
        .findFirst()
        .orElseThrow();

    assertEquals(80.0, containerStop.getCollectedLiters(), DELTA);
    assertEquals(40.0, containerStop.getCollectedKilograms(), DELTA);
    assertEquals(40.0, dailyPlan.getTotalCollectedKilograms(), DELTA);
  }

  @Test
  void shouldUnloadWhenKilogramCapacityIsReachedBeforeLiterCapacity() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK", 30.0, 1000.0);
    Container container = container("container-1", 28.465, -16.263, 500.0, 100.0);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        1,
        List.of(facilityWithVehicles),
        List.of(container),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    DailyPlan dailyPlan = solution.getDailyPlans().get(0);
    List<DailyPlanStop> containerStops = dailyPlan.getStops().stream()
        .filter(stop -> stop.getType() == StopType.CONTAINER)
        .toList();

    assertEquals(2, containerStops.size());
    assertEquals(60.0, containerStops.get(0).getCollectedLiters(), DELTA);
    assertEquals(30.0, containerStops.get(0).getCollectedKilograms(), DELTA);
    assertEquals(40.0, containerStops.get(1).getCollectedLiters(), DELTA);
    assertEquals(20.0, containerStops.get(1).getCollectedKilograms(), DELTA);
    assertEquals(100.0, dailyPlan.getTotalCollectedLiters(), DELTA);
    assertEquals(50.0, dailyPlan.getTotalCollectedKilograms(), DELTA);
  }

  @Test
  void shouldRevisitContainerAfterUnloadUntilItIsEmpty() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK", 60.0, 60.0);
    Container container = container("container-1", 28.465, -16.263, 500.0, 100.0);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        1,
        List.of(facilityWithVehicles),
        List.of(container),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

      DailyPlan dailyPlan = solution.getDailyPlans().get(0);
      List<DailyPlanStop> containerStops = dailyPlan.getStops().stream()
        .filter(s -> s.getType() == StopType.CONTAINER)
        .toList();
      assertEquals(2, containerStops.size());
      assertEquals(60.0, containerStops.get(0).getCollectedLiters(), DELTA);
      assertEquals(40.0, containerStops.get(1).getCollectedLiters(), DELTA);
    assertEquals(100.0, dailyPlan.getTotalCollectedLiters(), DELTA);
    assertEquals(0.0, solution.getContainerStateMonitoring().get(0).getDailyFillingLiters(), DELTA);
  }

  @Test
  void shouldNotDuplicateCollectionAcrossVehicles() {
    Facility facility = facility("facility-1");
    Vehicle firstVehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Vehicle secondVehicle = vehicle("vehicle-2", "SUPPORT_VEHICLE");
    Container container = container("container-1", 28.465, -16.263, 500.0, 50.0);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(
        facility,
        List.of(firstVehicle, secondVehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        1,
        List.of(facilityWithVehicles),
        List.of(container),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

      long stopCount = solution.getDailyPlans().stream()
        .flatMap(plan -> plan.getStops().stream())
        .filter(s -> s.getType() == StopType.CONTAINER)
        .count();

    assertEquals(2, solution.getDailyPlans().size());
    assertEquals(1, stopCount);
    assertEquals(50.0, solution.getTotalCollectedLiters(), DELTA);
  }

  @Test
  void shouldLimitFacilityVisitsToTwoPerVehicle() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK", 1000.0, 50.0);
    Container firstContainer = container("container-1", 28.465, -16.263, 500.0, 40.0);
    Container secondContainer = container("container-2", 28.462, -16.264, 500.0, 40.0);
    Container thirdContainer = container("container-3", 28.460, -16.260, 500.0, 40.0);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        1,
        List.of(facilityWithVehicles),
        List.of(firstContainer, secondContainer, thirdContainer),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    assertEquals(1, solution.getDailyPlans().size());
    long facilityStopCount = solution.getDailyPlans().get(0).getStops().stream()
        .filter(s -> s.getType() == StopType.FACILITY)
        .count();

    assertEquals(2, facilityStopCount);
    assertTrue(solution.getTotalCollectedLiters() <= 120.0 + DELTA);
  }

  @Test
  void shouldReturnInfeasibleWhenNoFacilities() {
    Container container = container("container-1", 28.465, -16.263);
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        7,
        List.of(),
        List.of(container),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    assertEquals(DeliveryPlanningStatus.INFEASIBLE, solution.getStatus());
    assertTrue(solution.getClusters().isEmpty());
    assertTrue(solution.getDailyPlans().isEmpty());
  }

  @Test
  void shouldReturnInfeasibleWhenNoContainers() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        7,
        List.of(facilityWithVehicles),
        List.of(),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    assertEquals(DeliveryPlanningStatus.INFEASIBLE, solution.getStatus());
    assertTrue(solution.getClusters().isEmpty());
    assertTrue(solution.getDailyPlans().isEmpty());
  }

  @Test
  void shouldAssignAllContainersToTheCluster() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container firstContainer = container("container-1", 28.465, -16.263);
    Container secondContainer = container("container-2", 28.462, -16.264);
    Container thirdContainer = container("container-3", 28.460, -16.260);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        7,
        List.of(facilityWithVehicles),
        List.of(firstContainer, secondContainer, thirdContainer),
        new MaximumBudget(5000.0, "EUR"));

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
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container firstContainer = container("container-1", 28.465, -16.263);
    Container secondContainer = container("container-2", 28.462, -16.264);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        7,
        List.of(facilityWithVehicles),
        List.of(firstContainer, secondContainer),
        new MaximumBudget(5000.0, "EUR"));

    DeliveryPlanningSolution solution = new Algorithm(problem).run();

    assertFalse(solution.getDailyPlans().isEmpty());
    DailyPlan plan = solution.getDailyPlans().get(0);
    long containerStopCount = plan.getStops().stream()
      .filter(s -> s.getType() == StopType.CONTAINER)
      .count();
    assertEquals(2, containerStopCount);
    assertTrue(plan.getTotalDistanceMeters() > 0);
  }

  @Test
  void vehicleShouldBeEmptyAfterFinishingTheRoute() {
    Facility facility = facility("facility-1");
    Vehicle vehicle = vehicle("vehicle-1", "COLLECTION_TRUCK");
    Container firstContainer = container("container-1", 28.465, -16.263);
    Container secondContainer = container("container-2", 28.462, -16.264);
    Container thirdContainer = container("container-3", 28.460, -16.260);

    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        7,
        List.of(facilityWithVehicles),
        List.of(firstContainer, secondContainer, thirdContainer),
        new MaximumBudget(5000.0, "EUR"));

    new Algorithm(problem).run();

    assertEquals(0.0, vehicle.getCurrentLoadLiters(), DELTA);
    assertEquals(0.0, vehicle.getCurrentLoadKilograms(), DELTA);
  }
}
