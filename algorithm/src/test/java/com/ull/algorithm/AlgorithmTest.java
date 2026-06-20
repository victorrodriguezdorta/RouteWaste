package com.ull.algorithm;

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
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AlgorithmTest {

  private static final double DELTA = 0.000001;
  private static final String FACILITY_1_ID = "facility-1";
  private static final String VEHICLE_1_ID = "vehicle-1";
  private static final String VEHICLE_2_ID = "vehicle-2";
  private static final String COLLECTION_TRUCK_TYPE = "COLLECTION_TRUCK";
  private static final String SUPPORT_VEHICLE_TYPE = "SUPPORT_VEHICLE";
  private static final String CONTAINER_1_ID = "container-1";
  private static final String CONTAINER_2_ID = "container-2";
  private static final String CONTAINER_3_ID = "container-3";
  private static final String CONTAINER_NEAR_ID = "container-near";
  private static final String CONTAINER_FAR_ID = "container-far";
  private static final String CONTAINER_NEAR_LOW_FILL_ID = "container-near-low-fill";
  private static final String CONTAINER_FAR_HIGH_FILL_ID = "container-far-high-fill";
  private static final String CONTAINER_FAR_OVERFLOWED_ID = "container-far-overflowed";
  private static final int PLAN_DAY_ONE = 1;
  private static final int PLAN_DAY_TWO = 2;
  private static final int PLAN_DAY_THREE = 3;
  private static final double MIN_TOTAL_DISTANCE_METERS = 0.0;

  /**
   * Creates a location for algorithm tests.
   *
   * @param lat latitude.
   * @param lon longitude.
   * @return location instance.
   */
  private Location location(double lat, double lon) {
    return new Location(lat, lon, "Test Address", "GIS-TEST");
  }

  /**
   * Creates a facility for algorithm tests.
   *
   * @param id facility identifier.
   * @return facility instance.
   */
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

  /**
   * Creates a vehicle with default capacity for algorithm tests.
   *
   * @param id vehicle identifier.
   * @param type vehicle type.
   * @return vehicle instance.
   */
  private Vehicle vehicle(String id, String type) {
    return vehicle(id, type, 1000.0, 1000.0);
  }

  /**
   * Creates a vehicle with custom capacity for algorithm tests.
   *
   * @param id vehicle identifier.
   * @param type vehicle type.
   * @param capacityKilograms kilogram capacity.
   * @param capacityLiters liter capacity.
   * @return vehicle instance.
   */
  private Vehicle vehicle(String id, String type, double capacityKilograms, double capacityLiters) {
    return new Vehicle(id, type, capacityKilograms, capacityLiters, 0.5);
  }

  /**
   * Creates a container with default capacity and demand for algorithm tests.
   *
   * @param id container identifier.
   * @param lat latitude.
   * @param lon longitude.
   * @return container instance.
   */
  private Container container(String id, double lat, double lon) {
    return container(id, lat, lon, 500.0, 50.0);
  }

  /**
   * Creates a container with custom capacity and demand for algorithm tests.
   *
   * @param id container identifier.
   * @param lat latitude.
   * @param lon longitude.
   * @param capacityLiters container capacity in liters.
   * @param dailyDemandLitersPerDay daily demand in liters per day.
   * @return container instance.
   */
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

  /**
   * Returns the first container stop identifier in a daily plan.
   *
   * @param plan daily plan.
   * @return first container stop identifier.
   */
  private String firstContainerStopId(DailyPlan plan) {
    return plan.getStops().stream()
        .filter(stop -> stop.getType() == StopType.CONTAINER)
        .findFirst()
        .map(stop -> stop.getContainer().getId())
        .orElseThrow();
  }

  /**
   * Tests that higher fill percentage is prioritized over closer containers.
   */
  @Test
  void shouldPrioritizeHigherFillPercentageOverCloserContainer() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Container nearLowFillContainer = container(
        CONTAINER_NEAR_LOW_FILL_ID,
        28.4698,
        -16.2498,
        200.0,
        10.0);
    Container farHighFillContainer = container(
        CONTAINER_FAR_HIGH_FILL_ID,
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
    assertEquals(CONTAINER_FAR_HIGH_FILL_ID, firstContainerStopId(dailyPlan));
  }

  /**
   * Tests that overflowed containers are prioritized over closer low-fill containers.
   */
  @Test
  void shouldPrioritizeOverflowedContainerOverCloserLowFillContainer() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Container nearLowFillContainer = container(
        CONTAINER_NEAR_LOW_FILL_ID,
        28.4698,
        -16.2498,
        500.0,
        30.0);
    Container farOverflowedContainer = container(
        CONTAINER_FAR_OVERFLOWED_ID,
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
    assertEquals(CONTAINER_FAR_OVERFLOWED_ID, firstContainerStopId(dailyPlan));
    DailyPlanStop firstStop = dailyPlan.getStops().stream()
        .filter(stop -> stop.getType() == StopType.CONTAINER)
        .findFirst()
        .orElseThrow();
    assertTrue(firstStop.getContainerActualLiters() > farOverflowedContainer.getCapacityLiters());
  }

  /**
   * Tests that closer containers are preferred when fill levels are equal.
   */
  @Test
  void shouldPreferCloserContainerWhenFillLevelsAreEqual() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Container nearContainer = container(
        CONTAINER_NEAR_ID,
        28.469,
        -16.252,
        100.0,
        50.0);
    Container farContainer = container(
        CONTAINER_FAR_ID,
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
    assertEquals(CONTAINER_NEAR_ID, firstContainerStopId(dailyPlan));
  }

  /**
   * Tests that fill levels before and after collection are recorded in monitoring.
   */
  @Test
  void shouldRecordFillLevelBeforeAndAfterCollectionInMonitoring() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Container container = container(CONTAINER_1_ID, 28.465, -16.263, 100.0, 80.0);
    FacilityWithVehicles facilityWithVehicles = new FacilityWithVehicles(facility, List.of(vehicle));
    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        15,
        1,
        List.of(facilityWithVehicles),
        List.of(container),
        new MaximumBudget(5000.0, "EUR"));
    DeliveryPlanningSolution solution = new Algorithm(problem).run();
    List<ContainerDailyState> states = solution.getContainerStateMonitoring();
    assertEquals(2, states.size());
    ContainerDailyState dayStartState = states.get(0);
    assertEquals(80.0, dayStartState.getDailyFillingLiters(), DELTA);
    assertNotNull(dayStartState.getTime());
    ContainerDailyState afterCollectionState = states.get(1);
    assertEquals(0.0, afterCollectionState.getDailyFillingLiters(), DELTA);
    assertEquals(80.0, afterCollectionState.getDailyFillingLitersBeforeCollection(), DELTA);
    assertNotNull(afterCollectionState.getTime());
  }

  /**
   * Tests that a simple problem returns a suboptimal solution.
   */
  @Test
  void shouldReturnSuboptimalSolutionForSimpleProblem() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Container firstContainer = container(CONTAINER_1_ID, 28.465, -16.263);
    Container secondContainer = container(CONTAINER_2_ID, 28.462, -16.264);
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

  /**
   * Tests that one cluster is created per algorithm call.
   */
  @Test
  void shouldCreateOneClusterPerAlgorithmCall() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Container firstContainer = container(CONTAINER_1_ID, 28.465, -16.263);
    Container secondContainer = container(CONTAINER_2_ID, 28.462, -16.264);
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
    assertEquals(FACILITY_1_ID, clusters.get(0).getFacility().getId());
    assertEquals(2, clusters.get(0).getAssignedContainers().size());
  }

  /**
   * Tests that one daily plan is created per vehicle and day.
   */
  @Test
  void shouldCreateOneDailyPlanPerVehicleAndDay() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle firstVehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Vehicle secondVehicle = vehicle(VEHICLE_2_ID, SUPPORT_VEHICLE_TYPE);
    Container container = container(CONTAINER_1_ID, 28.465, -16.263);
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
    assertEquals(2, dailyPlans.stream().filter(plan -> plan.getPlanDay() == PLAN_DAY_ONE).count());
    assertEquals(2, dailyPlans.stream().filter(plan -> plan.getPlanDay() == PLAN_DAY_TWO).count());
    assertEquals(2, dailyPlans.stream().filter(plan -> plan.getPlanDay() == PLAN_DAY_THREE).count());
    assertEquals(150.0, solution.getTotalCollectedLiters(), DELTA);
  }

  /**
   * Tests that plans are created for all requested days.
   */
  @Test
  void shouldCreatePlansForAllRequestedDays() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, SUPPORT_VEHICLE_TYPE, 88.0, 88.0);
    Container firstContainer = container(CONTAINER_1_ID, 28.465837, -16.263835, 76.0, 67.0);
    Container secondContainer = container(CONTAINER_2_ID, 28.464925, -16.268236, 100.0, 50.0);
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
    assertTrue(monitoring.stream().anyMatch(state -> CONTAINER_1_ID.equals(state.getContainerId())
        && Math.abs(state.getDailyFillingLiters()) <= DELTA));
    assertTrue(monitoring.stream().anyMatch(state -> CONTAINER_2_ID.equals(state.getContainerId())
        && Math.abs(state.getDailyFillingLiters()) <= DELTA));
    assertTrue(monitoring.stream().allMatch(state -> state.getTime() != null));
  }

  /**
   * Tests that collected kilograms are derived from waste type density.
   */
  @Test
  void shouldRecordCollectedKilogramsFromWasteTypeDensity() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE, 1000.0, 1000.0);
    Container container = container(CONTAINER_1_ID, 28.465, -16.263, 500.0, 80.0);
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

  /**
   * Tests unloading when kilogram capacity is reached before liter capacity.
   */
  @Test
  void shouldUnloadWhenKilogramCapacityIsReachedBeforeLiterCapacity() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE, 30.0, 1000.0);
    Container container = container(CONTAINER_1_ID, 28.465, -16.263, 500.0, 100.0);
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

  /**
   * Tests that containers are revisited after unload until they are empty.
   */
  @Test
  void shouldRevisitContainerAfterUnloadUntilItIsEmpty() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE, 60.0, 60.0);
    Container container = container(CONTAINER_1_ID, 28.465, -16.263, 500.0, 100.0);
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
    assertEquals(
        0.0,
        solution.getContainerStateMonitoring().stream()
            .mapToDouble(ContainerDailyState::getDailyFillingLiters)
            .min()
            .orElse(-1.0),
        DELTA);
  }

  /**
   * Tests that collection is not duplicated across vehicles.
   */
  @Test
  void shouldNotDuplicateCollectionAcrossVehicles() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle firstVehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Vehicle secondVehicle = vehicle(VEHICLE_2_ID, SUPPORT_VEHICLE_TYPE);
    Container container = container(CONTAINER_1_ID, 28.465, -16.263, 500.0, 50.0);
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

  /**
   * Tests that facility visits are limited to two per vehicle.
   */
  @Test
  void shouldLimitFacilityVisitsToTwoPerVehicle() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE, 1000.0, 50.0);
    Container firstContainer = container(CONTAINER_1_ID, 28.465, -16.263, 500.0, 40.0);
    Container secondContainer = container(CONTAINER_2_ID, 28.462, -16.264, 500.0, 40.0);
    Container thirdContainer = container(CONTAINER_3_ID, 28.460, -16.260, 500.0, 40.0);
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

  /**
   * Tests that the algorithm returns infeasible when no facilities are available.
   */
  @Test
  void shouldReturnInfeasibleWhenNoFacilities() {
    Container container = container(CONTAINER_1_ID, 28.465, -16.263);
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

  /**
   * Tests that the algorithm returns infeasible when no containers are available.
   */
  @Test
  void shouldReturnInfeasibleWhenNoContainers() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
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

  /**
   * Tests that all containers are assigned to the cluster.
   */
  @Test
  void shouldAssignAllContainersToTheCluster() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Container firstContainer = container(CONTAINER_1_ID, 28.465, -16.263);
    Container secondContainer = container(CONTAINER_2_ID, 28.462, -16.264);
    Container thirdContainer = container(CONTAINER_3_ID, 28.460, -16.260);
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
    assertTrue(assignedIds.contains(CONTAINER_1_ID));
    assertTrue(assignedIds.contains(CONTAINER_2_ID));
    assertTrue(assignedIds.contains(CONTAINER_3_ID));
  }

  /**
   * Tests that daily plan stops match the container count.
   */
  @Test
  void dailyPlanStopsShouldMatchContainerCount() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Container firstContainer = container(CONTAINER_1_ID, 28.465, -16.263);
    Container secondContainer = container(CONTAINER_2_ID, 28.462, -16.264);
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
    assertTrue(plan.getTotalDistanceMeters() > MIN_TOTAL_DISTANCE_METERS);
  }

  /**
   * Tests that the vehicle is empty after finishing the route.
   */
  @Test
  void vehicleShouldBeEmptyAfterFinishingTheRoute() {
    Facility facility = facility(FACILITY_1_ID);
    Vehicle vehicle = vehicle(VEHICLE_1_ID, COLLECTION_TRUCK_TYPE);
    Container firstContainer = container(CONTAINER_1_ID, 28.465, -16.263);
    Container secondContainer = container(CONTAINER_2_ID, 28.462, -16.264);
    Container thirdContainer = container(CONTAINER_3_ID, 28.460, -16.260);
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
