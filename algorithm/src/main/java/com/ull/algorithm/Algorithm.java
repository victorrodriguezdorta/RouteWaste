package com.ull.algorithm;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import com.ull.domain.DeliveryPlanningStatus;
import com.ull.domain.entity.Alert;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.ContainerDailyState;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.entity.FacilityWithVehicles;
import com.ull.domain.entity.Vehicle;
import com.ull.domain.enumerate.ContainerStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point for the planning algorithm.
 *
 * <p>Receives a fully typed {@link DeliveryPlanningProblem} and returns a
 * {@link DeliveryPlanningSolution}.
 *
 * <p>Current implementation:
 * Phase 1: Clusterization by distance - each container is assigned to the nearest facility.
 * Phase 2: Day-by-day greedy routing - each vehicle serves the nearest container with pending
 * liters, can unload at the facility, and may continue collecting on the same day.
 * Phase 3: Container state monitoring - tracks the remaining liters after each simulated day.
 */
public class Algorithm {

  public static final String PROBLEM_NOT_DEFINED = "Problem is not defined";

  private static final double EPSILON = 0.000001;
  private static final int MAX_FACILITY_VISITS = 3;
  private static final int MIN_NUMBER_OF_DAYS = 1;

  private final DeliveryPlanningProblem problem;
  /**
   * Mixed-purpose state map used during the simulation:
   * - key = "containerId" stores the pending liters that still need collection
   * - key = "containerId_day" stores the end-of-day pending liters for monitoring/debugging
   */
  private final Map<String, Double> containerDailyState;

  /**
   * Creates an algorithm runner for a delivery planning problem.
   *
   * @param problem the problem to solve
   */
  public Algorithm(DeliveryPlanningProblem problem) {
    if (problem == null) {
      throw new IllegalArgumentException(PROBLEM_NOT_DEFINED);
    }
    this.problem = problem;
    this.containerDailyState = new HashMap<>();
  }

  /**
   * Runs the algorithm and returns the planning solution.
   *
   * @return the computed solution
   */
  public DeliveryPlanningSolution run() {
    DeliveryPlanningSolution solution = new DeliveryPlanningSolution();
    solution.updateMaxBudget(problem.getMaxBudget());
    List<FacilityWithVehicles> facilitiesWithVehicles = problem.getFacilitiesWithVehicles();
    List<Container> containers = problem.getContainers();
    if (facilitiesWithVehicles == null || facilitiesWithVehicles.isEmpty()
        || containers == null || containers.isEmpty()) {
      solution.updateStatus(DeliveryPlanningStatus.INFEASIBLE);
      return solution;
    }
    try {
      List<FacilityCluster> clusters = clusterizeByDistance(facilitiesWithVehicles, containers);
      if (clusters == null || clusters.isEmpty()) {
        solution.updateStatus(DeliveryPlanningStatus.INFEASIBLE);
        return solution;
      }
      for (FacilityCluster cluster : clusters) {
        if (cluster != null) {
          solution.addCluster(cluster);
        }
      }
      initializePendingLiters(containers);
      int numberOfDays = problem.getNumberOfDays();
      if (numberOfDays < MIN_NUMBER_OF_DAYS) {
        solution.updateStatus(DeliveryPlanningStatus.INFEASIBLE);
        return solution;
      }
      LocalDate startDate = LocalDate.now().plusDays(MIN_NUMBER_OF_DAYS);
      for (int day = MIN_NUMBER_OF_DAYS; day <= numberOfDays; day++) {
        LocalDate serviceDate = startDate.plusDays(day - MIN_NUMBER_OF_DAYS);
        for (FacilityCluster cluster : clusters) {
          if (cluster == null) {
            continue;
          }
          Facility facility = cluster.getFacility();
          if (facility == null || facility.getId() == null) {
            continue;
          }
          List<Container> clusterContainers = new ArrayList<>(cluster.getAssignedContainers());
          addDailyDemand(clusterContainers);
          FacilityWithVehicles facilityWithVehicles = findFacilityWithVehicles(
              facilitiesWithVehicles,
              facility.getId());
          if (facilityWithVehicles != null && facilityWithVehicles.getVehicles() != null) {
            for (Vehicle vehicle : facilityWithVehicles.getVehicles()) {
              if (vehicle == null) {
                continue;
              }
              try {
                vehicle.emptyLoad();
                DailyPlan dailyPlan = new DailyPlan(day, serviceDate, facility, vehicle);
                buildGreedyRoute(dailyPlan, clusterContainers);
                solution.addDailyPlan(dailyPlan);
              } catch (Exception e) {
                continue;
              }
            }
          }
          recordContainerStates(solution, clusterContainers, day);
        }
      }
      if (solution.getDailyPlans() != null && !solution.getDailyPlans().isEmpty()) {
        solution.updateStatus(DeliveryPlanningStatus.SUBOPTIMAL);
      } else {
        solution.updateStatus(DeliveryPlanningStatus.INFEASIBLE);
      }
    } catch (Exception e) {
      solution.updateStatus(DeliveryPlanningStatus.INFEASIBLE);
    }
    return solution;
  }

  /**
   * Clusters containers by assigning each to its nearest facility.
   *
   * @param facilitiesWithVehicles list of facilities
   * @param containers list of containers to cluster
   * @return list of facility clusters with assigned containers
   */
  private List<FacilityCluster> clusterizeByDistance(
      List<FacilityWithVehicles> facilitiesWithVehicles,
      List<Container> containers) {
    Map<String, FacilityCluster> clusterMap = new HashMap<>();
    for (FacilityWithVehicles facilityWithVehicles : facilitiesWithVehicles) {
      if (facilityWithVehicles == null || facilityWithVehicles.getFacility() == null) {
        continue;
      }
      clusterMap.put(
          facilityWithVehicles.getFacility().getId(),
          new FacilityCluster(facilityWithVehicles.getFacility()));
    }
    for (Container container : containers) {
      if (container == null || container.getLocation() == null) {
        continue;
      }
      Facility nearestFacility = null;
      double minDistance = Double.MAX_VALUE;
      for (FacilityWithVehicles facilityWithVehicles : facilitiesWithVehicles) {
        if (facilityWithVehicles == null || facilityWithVehicles.getFacility() == null) {
          continue;
        }
        Facility facility = facilityWithVehicles.getFacility();
        if (facility.getLocation() == null) {
          continue;
        }
        double distance = container.calculateDistanceTo(facility);
        if (distance < minDistance) {
          minDistance = distance;
          nearestFacility = facility;
        }
      }
      if (nearestFacility != null && clusterMap.containsKey(nearestFacility.getId())) {
        clusterMap.get(nearestFacility.getId()).addContainer(container);
      }
    }
    return new ArrayList<>(clusterMap.values());
  }

  /**
   * Builds a greedy route for a daily plan by repeatedly visiting the nearest container with
   * pending liters. When the vehicle becomes full, it returns to the facility, unloads, and
   * continues serving pending work on the same day.
   *
   * @param dailyPlan the daily plan to populate
   * @param availableContainers the containers assigned to the plan facility
   */
  private void buildGreedyRoute(DailyPlan dailyPlan, List<Container> availableContainers) {
    if (availableContainers == null || availableContainers.isEmpty()) {
      return;
    }
    Facility facility = dailyPlan.getOriginFacility();
    Vehicle vehicle = dailyPlan.getVehicle();
    if (facility == null || facility.getLocation() == null || vehicle == null) {
      return;
    }
    double vehicleCapacity = vehicle.getCapacityLiters();
    if (vehicleCapacity <= EPSILON) {
      return;
    }
    double vehicleCurrentLoad = 0.0;
    Object currentLocation = facility;
    int facilityVisits = MIN_NUMBER_OF_DAYS;
    while (hasPendingLiters(availableContainers)) {
      double remainingCapacity = vehicleCapacity - vehicleCurrentLoad;
      if (remainingCapacity <= EPSILON) {
        if (facilityVisits >= MAX_FACILITY_VISITS) {
          break;
        }
        returnToFacilityAndUnload(dailyPlan, vehicle, currentLocation, facility);
        vehicleCurrentLoad = 0.0;
        currentLocation = facility;
        facilityVisits++;
        continue;
      }
      if (facilityVisits >= MAX_FACILITY_VISITS && vehicleCurrentLoad > EPSILON) {
        break;
      }
      Container nearest = findNearestCollectableContainer(currentLocation, availableContainers);
      if (nearest == null) {
        break;
      }
      double pendingLiters = getPendingLiters(nearest);
      if (pendingLiters <= EPSILON) {
        updatePendingLiters(nearest, 0.0);
        continue;
      }
      double collectedLiters = Math.min(pendingLiters, remainingCapacity);
      double distanceFromCurrentLocation = calculateDistanceToContainer(currentLocation, nearest);
      dailyPlan.addStop(
          nearest,
          0.0,
          collectedLiters,
          pendingLiters,
          distanceFromCurrentLocation,
          createStopAlerts(nearest, pendingLiters, collectedLiters));
      updatePendingLiters(nearest, pendingLiters - collectedLiters);
      vehicleCurrentLoad += collectedLiters;
      vehicle.updateCurrentLoadLiters(vehicleCurrentLoad);
      currentLocation = nearest;
      if (vehicleCurrentLoad >= vehicleCapacity - EPSILON) {
        if (facilityVisits >= MAX_FACILITY_VISITS) {
          break;
        }
        returnToFacilityAndUnload(dailyPlan, vehicle, currentLocation, facility);
        vehicleCurrentLoad = 0.0;
        currentLocation = facility;
        facilityVisits++;
      }
    }
    if (vehicleCurrentLoad > EPSILON && facilityVisits < MAX_FACILITY_VISITS) {
      returnToFacilityAndUnload(dailyPlan, vehicle, currentLocation, facility);
      facilityVisits++;
    }
  }

  /**
   * Initializes the pending liters map for every known container.
   *
   * @param containers containers participating in the simulation
   */
  private void initializePendingLiters(List<Container> containers) {
    this.containerDailyState.clear();
    if (containers == null) {
      return;
    }
    for (Container container : containers) {
      if (container != null && container.getId() != null) {
        updatePendingLiters(container, 0.0);
      }
    }
  }

  /**
   * Adds one day of demand to each container pending amount.
   *
   * @param containers containers whose daily demand should be added
   */
  private void addDailyDemand(List<Container> containers) {
    if (containers == null) {
      return;
    }
    for (Container container : containers) {
      if (container == null || container.getId() == null) {
        continue;
      }
      double pendingLiters = getPendingLiters(container) + container.getDailyDemandLitersPerDay();
      updatePendingLiters(container, pendingLiters);
    }
  }

  /**
   * Finds the vehicle assignment associated with a facility id.
   *
   * @param facilitiesWithVehicles facility-vehicle assignments
   * @param facilityId target facility identifier
   * @return matching assignment, or null when none exists
   */
  private FacilityWithVehicles findFacilityWithVehicles(
      List<FacilityWithVehicles> facilitiesWithVehicles,
      String facilityId) {
    if (facilitiesWithVehicles == null || facilityId == null) {
      return null;
    }
    return facilitiesWithVehicles.stream()
        .filter(facilityWithVehicles -> facilityWithVehicles != null
            && facilityWithVehicles.getFacility() != null
            && facilityId.equals(facilityWithVehicles.getFacility().getId()))
        .findFirst()
        .orElse(null);
  }

  /**
   * Records end-of-day container states in the solution.
   *
   * @param solution solution receiving the monitoring snapshots
   * @param containers containers to monitor
   * @param day simulated planning day
   */
  private void recordContainerStates(
      DeliveryPlanningSolution solution,
      List<Container> containers,
      int day) {
    if (containers == null) {
      return;
    }
    for (Container container : containers) {
      if (container == null || container.getId() == null) {
        continue;
      }
      double pendingLiters = getPendingLiters(container);
      ContainerStatus status = pendingLiters > container.getCapacityLiters()
          ? ContainerStatus.OVERFLOWED
          : ContainerStatus.CORRECT;
      solution.addContainerDailyState(new ContainerDailyState(
          container.getId(),
          day,
          pendingLiters,
          container.getCapacityLiters(),
          container.getDailyDemandLitersPerDay(),
          status));
      this.containerDailyState.put(buildDayKey(container.getId(), day), pendingLiters);
    }
  }

  /**
   * Finds the nearest container that still has pending liters.
   *
   * @param currentLocation current facility or container location anchor
   * @param containers candidate containers
   * @return nearest collectable container, or null when none can be collected
   */
  private Container findNearestCollectableContainer(Object currentLocation, List<Container> containers) {
    Container nearest = null;
    double minDistance = Double.MAX_VALUE;
    for (Container container : containers) {
      if (container == null || container.getLocation() == null) {
        continue;
      }
      if (getPendingLiters(container) <= EPSILON) {
        continue;
      }
      try {
        double distance = calculateDistanceToContainer(currentLocation, container);
        if (distance < minDistance) {
          minDistance = distance;
          nearest = container;
        }
      } catch (Exception e) {
        continue;
      }
    }
    return nearest;
  }

  /**
   * Checks whether any container still has pending liters.
   *
   * @param containers containers to inspect
   * @return true when at least one container has pending liters
   */
  private boolean hasPendingLiters(List<Container> containers) {
    if (containers == null) {
      return false;
    }
    for (Container container : containers) {
      if (container != null && container.getId() != null && getPendingLiters(container) > EPSILON) {
        return true;
      }
    }
    return false;
  }

  /**
   * Creates alerts for a collection stop.
   *
   * @param container container being collected
   * @param pendingLiters liters pending before collection
   * @param collectedLiters liters collected in the stop
   * @return alerts describing overflow or partial collection conditions
   */
  private List<Alert> createStopAlerts(
      Container container,
      double pendingLiters,
      double collectedLiters) {
    List<Alert> alerts = new ArrayList<>();
    if (pendingLiters > container.getCapacityLiters()) {
      alerts.add(new Alert(
          "CONTAINER_OVERFLOWED",
          "Container filling exceeds its nominal capacity before collection.",
          pendingLiters));
    }
    double remainingLiters = pendingLiters - collectedLiters;
    if (remainingLiters > EPSILON) {
      alerts.add(new Alert(
          "PARTIAL_COLLECTION",
          "Collection stopped because the vehicle reached its capacity.",
          remainingLiters));
    }
    return alerts;
  }

  /**
   * Calculates distance from the current location to a target container.
   *
   * @param currentLocation current facility or container
   * @param targetContainer target container
   * @return distance in meters
   */
  private double calculateDistanceToContainer(Object currentLocation, Container targetContainer) {
    if (currentLocation instanceof Facility facility) {
      return facility.calculateDistanceTo(targetContainer);
    }
    return ((Container) currentLocation).calculateDistanceTo(targetContainer);
  }

  /**
   * Calculates distance from the current location to a facility.
   *
   * @param currentLocation current facility or container
   * @param facility target facility
   * @return distance in meters
   */
  private double calculateDistanceToFacility(Object currentLocation, Facility facility) {
    if (currentLocation instanceof Facility currentFacility) {
      return currentFacility.calculateDistanceTo(facility);
    }
    return ((Container) currentLocation).calculateDistanceTo(facility);
  }

  /**
   * Adds a facility stop and empties the vehicle load.
   *
   * @param dailyPlan daily plan receiving the facility stop
   * @param vehicle vehicle to unload
   * @param currentLocation current route location
   * @param facility facility to return to
   */
  private void returnToFacilityAndUnload(
      DailyPlan dailyPlan,
      Vehicle vehicle,
      Object currentLocation,
      Facility facility) {
    if (dailyPlan == null || vehicle == null || currentLocation == null || facility == null) {
      return;
    }
    double distanceToFacility = calculateDistanceToFacility(currentLocation, facility);
    dailyPlan.addFacilityStop(distanceToFacility, new ArrayList<>());
    vehicle.emptyLoad();
  }

  /**
   * Returns pending liters for a container.
   *
   * @param container container whose pending liters are requested
   * @return pending liters, or zero when the container is unknown
   */
  private double getPendingLiters(Container container) {
    return this.containerDailyState.getOrDefault(container.getId(), 0.0);
  }

  /**
   * Stores normalized pending liters for a container.
   *
   * @param container container to update
   * @param pendingLiters pending liters to store
   */
  private void updatePendingLiters(Container container, double pendingLiters) {
    double normalizedPendingLiters = Math.abs(pendingLiters) <= EPSILON ? 0.0 : pendingLiters;
    this.containerDailyState.put(container.getId(), normalizedPendingLiters);
  }

  /**
   * Builds the key used to store a container end-of-day state.
   *
   * @param containerId container identifier
   * @param day planning day
   * @return compound state key
   */
  private String buildDayKey(String containerId, int day) {
    return containerId + "_" + day;
  }
}
