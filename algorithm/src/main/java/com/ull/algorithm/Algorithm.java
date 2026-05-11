package com.ull.algorithm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import com.ull.domain.entity.Alert;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.ContainerDailyState;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.entity.FacilityWithVehicles;
import com.ull.domain.entity.Vehicle;
import com.ull.domain.enums.ContainerStatus;

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

  private static final double EPSILON = 0.000001;

  private final DeliveryPlanningProblem problem;
  /**
   * Mixed-purpose state map used during the simulation:
   * - key = "containerId" stores the pending liters that still need collection
   * - key = "containerId_day" stores the end-of-day pending liters for monitoring/debugging
   */
  private final Map<String, Double> containerDailyState;

  public Algorithm(DeliveryPlanningProblem problem) {
    if (problem == null) {
      throw new IllegalArgumentException("Problem is not defined");
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
      solution.updateStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
      return solution;
    }

    try {
      List<FacilityCluster> clusters = clusterizeByDistance(facilitiesWithVehicles, containers);
      if (clusters == null || clusters.isEmpty()) {
        solution.updateStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
        return solution;
      }

      for (FacilityCluster cluster : clusters) {
        if (cluster != null) {
          solution.addCluster(cluster);
        }
      }

      initializePendingLiters(containers);

      int numberOfDays = problem.getNumberOfDays();
      if (numberOfDays < 1) {
        solution.updateStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
        return solution;
      }

      LocalDate startDate = LocalDate.now().plusDays(1);

      for (int day = 1; day <= numberOfDays; day++) {
        LocalDate serviceDate = startDate.plusDays(day - 1);

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
        solution.updateStatus(DeliveryPlanningSolution.Status.SUBOPTIMAL);
      } else {
        solution.updateStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
      }

    } catch (Exception e) {
      solution.updateStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
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

    while (hasPendingLiters(availableContainers)) {
      double remainingCapacity = vehicleCapacity - vehicleCurrentLoad;
      if (remainingCapacity <= EPSILON) {
        returnToFacilityAndUnload(dailyPlan, vehicle, currentLocation, facility);
        vehicleCurrentLoad = 0.0;
        currentLocation = facility;
        continue;
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
        returnToFacilityAndUnload(dailyPlan, vehicle, currentLocation, facility);
        vehicleCurrentLoad = 0.0;
        currentLocation = facility;
      }
    }

    if (vehicleCurrentLoad > EPSILON) {
      returnToFacilityAndUnload(dailyPlan, vehicle, currentLocation, facility);
    }
  }

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

  private double calculateDistanceToContainer(Object currentLocation, Container targetContainer) {
    if (currentLocation instanceof Facility facility) {
      return facility.calculateDistanceTo(targetContainer);
    }
    return ((Container) currentLocation).calculateDistanceTo(targetContainer);
  }

  private double calculateDistanceToFacility(Object currentLocation, Facility facility) {
    if (currentLocation instanceof Facility currentFacility) {
      return currentFacility.calculateDistanceTo(facility);
    }
    return ((Container) currentLocation).calculateDistanceTo(facility);
  }

  private void returnToFacilityAndUnload(
      DailyPlan dailyPlan,
      Vehicle vehicle,
      Object currentLocation,
      Facility facility) {
    if (dailyPlan == null || vehicle == null || currentLocation == null || facility == null) {
      return;
    }

    dailyPlan.addTransitDistance(calculateDistanceToFacility(currentLocation, facility));
    vehicle.emptyLoad();
  }

  private double getPendingLiters(Container container) {
    return this.containerDailyState.getOrDefault(container.getId(), 0.0);
  }

  private void updatePendingLiters(Container container, double pendingLiters) {
    double normalizedPendingLiters = Math.abs(pendingLiters) <= EPSILON ? 0.0 : pendingLiters;
    this.containerDailyState.put(container.getId(), normalizedPendingLiters);
  }

  private String buildDayKey(String containerId, int day) {
    return containerId + "_" + day;
  }
}
