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
import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.algorithm.GreedyWeights;
import com.ull.domain.valueobject.converter.WasteVolumeToMassConverter;

import java.time.LocalDate;
import java.time.LocalTime;
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
 * Phase 2: Day-by-day greedy routing - each vehicle repeatedly selects the next container
 * balancing proximity and fill percentage, can unload at the facility, and may continue
 * collecting on the same day.
 * Phase 3: Container state monitoring - tracks the remaining liters after each simulated day.
 */
public class Algorithm {

  public static final String PROBLEM_NOT_DEFINED = "Problem is not defined";

  private static final double EPSILON = 0.000001;
  private static final int MAX_FACILITY_VISITS = 3;
  private static final int MIN_NUMBER_OF_DAYS = 1;
  private static final double OVERFLOW_SCORE_BONUS = 1.5;
  private static final double FULL_FILL_PERCENTAGE = 100.0;

  private final DeliveryPlanningProblem problem;
  /**
   * Weights applied to the greedy selection score.
   *
   * <p>The values are taken from the problem request or fall back to defaults.
   */
  private final GreedyWeights greedyWeights;
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
    this.greedyWeights = problem.getGreedyWeights() != null
        ? problem.getGreedyWeights()
        : GreedyWeights.defaultWeights();
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
          Map<String, Double> fillingBeforeCollection = snapshotPendingLiters(clusterContainers);
          recordContainerDayStartStates(solution, clusterContainers, day, fillingBeforeCollection);
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
                buildGreedyRoute(solution, dailyPlan, clusterContainers, day);
                solution.addDailyPlan(dailyPlan);
              } catch (Exception e) {
                continue;
              }
            }
          }
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
   * Builds a greedy route for a daily plan by repeatedly visiting the best-scored container
   * with pending liters. Selection balances distance from the current location and the container
   * fill percentage for the day. When the vehicle becomes full, it returns to the facility,
   * unloads, and continues serving pending work on the same day.
   *
   * @param solution the solution receiving per-stop container state snapshots
   * @param dailyPlan the daily plan to populate
   * @param availableContainers the containers assigned to the plan facility
   * @param day the simulated planning day
   */
  private void buildGreedyRoute(
      DeliveryPlanningSolution solution,
      DailyPlan dailyPlan,
      List<Container> availableContainers,
      int day) {
    if (availableContainers == null || availableContainers.isEmpty()) {
      return;
    }
    Facility facility = dailyPlan.getOriginFacility();
    Vehicle vehicle = dailyPlan.getVehicle();
    if (facility == null || facility.getLocation() == null || vehicle == null) {
      return;
    }
    if (!hasRouteCapacityConfigured(vehicle)) {
      return;
    }
    double vehicleCurrentLoadLiters = 0.0;
    double vehicleCurrentLoadKilograms = 0.0;
    Object currentLocation = facility;
    int facilityVisits = MIN_NUMBER_OF_DAYS;
    LocalTime currentTime = problem.getCollectionStartTime();
    int transferMinutes = problem.getAverageTransferTimeMinutes();
    int pickupMinutes = problem.getAveragePickupTimeMinutes();
    while (hasPendingLiters(availableContainers)) {
      if (isVehicleLoadFull(vehicle, vehicleCurrentLoadLiters, vehicleCurrentLoadKilograms)) {
        if (facilityVisits >= MAX_FACILITY_VISITS) {
          break;
        }
        currentTime = returnToFacilityAndUnload(dailyPlan, vehicle, currentLocation, facility, currentTime);
        vehicleCurrentLoadLiters = 0.0;
        vehicleCurrentLoadKilograms = 0.0;
        currentLocation = facility;
        facilityVisits++;
        continue;
      }
      if (facilityVisits >= MAX_FACILITY_VISITS
          && (vehicleCurrentLoadLiters > EPSILON || vehicleCurrentLoadKilograms > EPSILON)) {
        break;
      }
      Container nextContainer = findNextCollectableContainer(currentLocation, availableContainers);
      if (nextContainer == null) {
        break;
      }
      double pendingLiters = getPendingLiters(nextContainer);
      if (pendingLiters <= EPSILON) {
        updatePendingLiters(nextContainer, 0.0);
        continue;
      }
      double remainingLiters = remainingLiterCapacity(vehicle, vehicleCurrentLoadLiters);
      double remainingKilograms = remainingKilogramCapacity(vehicle, vehicleCurrentLoadKilograms);
      double collectedLiters = computeCollectableLiters(
          nextContainer,
          pendingLiters,
          remainingLiters,
          remainingKilograms);
      if (collectedLiters <= EPSILON) {
        if (facilityVisits >= MAX_FACILITY_VISITS) {
          break;
        }
        currentTime = returnToFacilityAndUnload(dailyPlan, vehicle, currentLocation, facility, currentTime);
        vehicleCurrentLoadLiters = 0.0;
        vehicleCurrentLoadKilograms = 0.0;
        currentLocation = facility;
        facilityVisits++;
        continue;
      }
      double collectedKilograms = WasteVolumeToMassConverter.litersToKilograms(
          collectedLiters,
          nextContainer.getWasteType());
      double distanceFromCurrentLocation = calculateDistanceToContainer(currentLocation, nextContainer);
      LocalTime collectedAt = advanceTime(currentTime, transferMinutes);
      dailyPlan.addStop(
          nextContainer,
          collectedKilograms,
          collectedLiters,
          pendingLiters,
          distanceFromCurrentLocation,
          createStopAlerts(nextContainer, pendingLiters, collectedLiters),
          collectedAt);
      double remainingAfterCollection = pendingLiters - collectedLiters;
      updatePendingLiters(nextContainer, remainingAfterCollection);
      recordContainerStopState(
          solution,
          nextContainer,
          day,
          getPendingLiters(nextContainer),
          pendingLiters,
          collectedAt);
      currentTime = advanceTime(collectedAt, pickupMinutes);
      vehicleCurrentLoadLiters += collectedLiters;
      vehicleCurrentLoadKilograms += collectedKilograms;
      vehicle.updateCurrentLoadLiters(vehicleCurrentLoadLiters);
      vehicle.updateCurrentLoadKilograms(vehicleCurrentLoadKilograms);
      currentLocation = nextContainer;
      if (isVehicleLoadFull(vehicle, vehicleCurrentLoadLiters, vehicleCurrentLoadKilograms)) {
        if (facilityVisits >= MAX_FACILITY_VISITS) {
          break;
        }
        currentTime = returnToFacilityAndUnload(dailyPlan, vehicle, currentLocation, facility, currentTime);
        vehicleCurrentLoadLiters = 0.0;
        vehicleCurrentLoadKilograms = 0.0;
        currentLocation = facility;
        facilityVisits++;
      }
    }
    if ((vehicleCurrentLoadLiters > EPSILON || vehicleCurrentLoadKilograms > EPSILON)
        && facilityVisits < MAX_FACILITY_VISITS) {
      returnToFacilityAndUnload(dailyPlan, vehicle, currentLocation, facility, currentTime);
      facilityVisits++;
    }
  }

  /**
   * Adds the given number of minutes to a time of day, tolerating a null clock.
   *
   * @param time base time of day (may be null)
   * @param minutes minutes to add
   * @return the resulting time, or null when no base time is available
   */
  private LocalTime advanceTime(LocalTime time, long minutes) {
    return time != null ? time.plusMinutes(minutes) : null;
  }

  /**
   * Returns whether the vehicle has at least one positive routing capacity configured.
   *
   * @param vehicle assigned vehicle
   * @return true when liter or kilogram capacity is positive
   */
  private boolean hasRouteCapacityConfigured(Vehicle vehicle) {
    return vehicle.getCapacityLiters() > EPSILON || vehicle.getCapacityKilograms() > EPSILON;
  }

  /**
   * Returns remaining liter capacity on the vehicle.
   *
   * @param vehicle assigned vehicle
   * @param currentLoadLiters current liter load
   * @return remaining liters, or unbounded when liter capacity is not configured
   */
  private double remainingLiterCapacity(Vehicle vehicle, double currentLoadLiters) {
    if (vehicle.getCapacityLiters() <= EPSILON) {
      return Double.MAX_VALUE;
    }
    return Math.max(0.0, vehicle.getCapacityLiters() - currentLoadLiters);
  }

  /**
   * Returns remaining kilogram capacity on the vehicle.
   *
   * @param vehicle assigned vehicle
   * @param currentLoadKilograms current kilogram load
   * @return remaining kilograms, or unbounded when kg capacity is not configured
   */
  private double remainingKilogramCapacity(Vehicle vehicle, double currentLoadKilograms) {
    if (vehicle.getCapacityKilograms() <= EPSILON) {
      return Double.MAX_VALUE;
    }
    return Math.max(0.0, vehicle.getCapacityKilograms() - currentLoadKilograms);
  }

  /**
   * Determines whether the vehicle has reached a binding capacity limit.
   *
   * @param vehicle assigned vehicle
   * @param currentLoadLiters current liter load
   * @param currentLoadKilograms current kilogram load
   * @return true when liter or kilogram capacity is exhausted
   */
  private boolean isVehicleLoadFull(
      Vehicle vehicle,
      double currentLoadLiters,
      double currentLoadKilograms) {
    boolean litersFull = vehicle.getCapacityLiters() > EPSILON
        && currentLoadLiters >= vehicle.getCapacityLiters() - EPSILON;
    boolean kilogramsFull = vehicle.getCapacityKilograms() > EPSILON
        && currentLoadKilograms >= vehicle.getCapacityKilograms() - EPSILON;
    return litersFull || kilogramsFull;
  }

  /**
   * Computes how many liters can be collected respecting pending waste, liter capacity,
   * and kilogram capacity (via waste-type density).
   *
   * @param container target container
   * @param pendingLiters liters pending before collection
   * @param remainingLiters remaining liter capacity on the vehicle
   * @param remainingKilograms remaining kilogram capacity on the vehicle
   * @return collectable liters (zero when no capacity remains)
   */
  private double computeCollectableLiters(
      Container container,
      double pendingLiters,
      double remainingLiters,
      double remainingKilograms) {
    if (pendingLiters <= EPSILON) {
      return 0.0;
    }
    double collectedLiters = Math.min(pendingLiters, remainingLiters);
    WasteType wasteType = container.getWasteType();
    if (remainingKilograms < Double.MAX_VALUE) {
      double litersLimitedByKilograms = WasteVolumeToMassConverter.litersFromRemainingKilograms(
          remainingKilograms,
          wasteType);
      collectedLiters = Math.min(collectedLiters, litersLimitedByKilograms);
    }
    return collectedLiters <= EPSILON ? 0.0 : collectedLiters;
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
   * Records the start-of-day container states in the solution.
   *
   * <p>This snapshot captures the filling level at the configured collection start time, right
   * after the daily demand has been added and before any collection happens. It guarantees that
   * every container appears in the monitoring timeline even when it is never collected.
   *
   * @param solution solution receiving the monitoring snapshots
   * @param containers containers to monitor
   * @param day simulated planning day
   * @param fillingBeforeCollection pending liters per container before the day collection starts
   */
  private void recordContainerDayStartStates(
      DeliveryPlanningSolution solution,
      List<Container> containers,
      int day,
      Map<String, Double> fillingBeforeCollection) {
    if (containers == null) {
      return;
    }
    LocalTime dayStartTime = problem.getCollectionStartTime();
    for (Container container : containers) {
      if (container == null || container.getId() == null) {
        continue;
      }
      double beforeCollectionLiters = fillingBeforeCollection != null
          ? fillingBeforeCollection.getOrDefault(container.getId(), getPendingLiters(container))
          : getPendingLiters(container);
      ContainerStatus status = beforeCollectionLiters > container.getCapacityLiters()
          ? ContainerStatus.OVERFLOWED
          : ContainerStatus.CORRECT;
      solution.addContainerDailyState(new ContainerDailyState(
          container.getId(),
          day,
          beforeCollectionLiters,
          beforeCollectionLiters,
          container.getCapacityLiters(),
          container.getDailyDemandLitersPerDay(),
          status,
          dayStartTime));
      this.containerDailyState.put(buildDayKey(container.getId(), day), beforeCollectionLiters);
    }
  }

  /**
   * Records the container state captured right after a collection stop.
   *
   * @param solution solution receiving the monitoring snapshot
   * @param container the collected container
   * @param day simulated planning day
   * @param fillingAfterCollection pending liters left after the collection
   * @param fillingBeforeCollection pending liters before this collection
   * @param time time of day when the collection happened
   */
  private void recordContainerStopState(
      DeliveryPlanningSolution solution,
      Container container,
      int day,
      double fillingAfterCollection,
      double fillingBeforeCollection,
      LocalTime time) {
    if (solution == null || container == null || container.getId() == null) {
      return;
    }
    double normalizedAfter = Math.max(0.0, fillingAfterCollection);
    ContainerStatus status = normalizedAfter > container.getCapacityLiters()
        ? ContainerStatus.OVERFLOWED
        : ContainerStatus.CORRECT;
    solution.addContainerDailyState(new ContainerDailyState(
        container.getId(),
        day,
        normalizedAfter,
        Math.max(0.0, fillingBeforeCollection),
        container.getCapacityLiters(),
        container.getDailyDemandLitersPerDay(),
        status,
        time));
    this.containerDailyState.put(buildDayKey(container.getId(), day), normalizedAfter);
  }

  /**
   * Captures the pending liters for each container at the current simulation point.
   *
   * @param containers containers whose pending liters should be captured
   * @return map keyed by container id
   */
  private Map<String, Double> snapshotPendingLiters(List<Container> containers) {
    Map<String, Double> snapshot = new HashMap<>();
    if (containers == null) {
      return snapshot;
    }
    for (Container container : containers) {
      if (container == null || container.getId() == null) {
        continue;
      }
      snapshot.put(container.getId(), getPendingLiters(container));
    }
    return snapshot;
  }

  /**
   * Finds the next container to collect using a greedy score that balances distance and fill
   * percentage. Lower scores are preferred. Overflowed containers receive additional priority.
   *
   * @param currentLocation current facility or container location anchor
   * @param containers candidate containers
   * @return best-scored collectable container, or null when none can be collected
   */
  private Container findNextCollectableContainer(Object currentLocation, List<Container> containers) {
    Container bestContainer = null;
    double bestScore = Double.MAX_VALUE;
    double bestFillPercentage = -1.0;
    double bestDistance = Double.MAX_VALUE;
    double maxDistance = 0.0;
    double maxFillPercentage = 0.0;
    for (Container container : containers) {
      if (container == null || container.getLocation() == null) {
        continue;
      }
      if (getPendingLiters(container) <= EPSILON) {
        continue;
      }
      try {
        double distance = calculateDistanceToContainer(currentLocation, container);
        double fillPercentage = getFillPercentage(container);
        maxDistance = Math.max(maxDistance, distance);
        maxFillPercentage = Math.max(maxFillPercentage, fillPercentage);
      } catch (Exception e) {
        continue;
      }
    }
    for (Container container : containers) {
      if (container == null || container.getLocation() == null) {
        continue;
      }
      if (getPendingLiters(container) <= EPSILON) {
        continue;
      }
      try {
        double distance = calculateDistanceToContainer(currentLocation, container);
        double fillPercentage = getFillPercentage(container);
        double score = computeContainerSelectionScore(
            distance,
            fillPercentage,
            maxDistance,
            maxFillPercentage);
        if (isBetterContainerCandidate(
            score,
            fillPercentage,
            distance,
            bestScore,
            bestFillPercentage,
            bestDistance)) {
          bestScore = score;
          bestFillPercentage = fillPercentage;
          bestDistance = distance;
          bestContainer = container;
        }
      } catch (Exception e) {
        continue;
      }
    }
    return bestContainer;
  }

  /**
   * Computes the greedy selection score for a container candidate.
   *
   * @param distance distance from the current location in meters
   * @param fillPercentage container fill percentage for the day
   * @param maxDistance maximum distance among current candidates
   * @param maxFillPercentage maximum fill percentage among current candidates
   * @return selection score where lower values are preferred
   */
  private double computeContainerSelectionScore(
      double distance,
      double fillPercentage,
      double maxDistance,
      double maxFillPercentage) {
    double normalizedDistance = maxDistance > EPSILON ? distance / maxDistance : 0.0;
    double normalizedFill = maxFillPercentage > EPSILON ? fillPercentage / maxFillPercentage : 0.0;
    double score = this.greedyWeights.getDistanceWeight() * normalizedDistance
        - this.greedyWeights.getFillWeight() * normalizedFill;
    if (fillPercentage > FULL_FILL_PERCENTAGE) {
      score -= OVERFLOW_SCORE_BONUS;
    }
    return score;
  }

  /**
   * Compares two container candidates for greedy selection.
   *
   * @param score candidate score
   * @param fillPercentage candidate fill percentage
   * @param distance candidate distance
   * @param bestScore current best score
   * @param bestFillPercentage current best fill percentage
   * @param bestDistance current best distance
   * @return true when the candidate should replace the current best
   */
  private boolean isBetterContainerCandidate(
      double score,
      double fillPercentage,
      double distance,
      double bestScore,
      double bestFillPercentage,
      double bestDistance) {
    if (score < bestScore - EPSILON) {
      return true;
    }
    if (Math.abs(score - bestScore) <= EPSILON) {
      if (fillPercentage > bestFillPercentage + EPSILON) {
        return true;
      }
      if (Math.abs(fillPercentage - bestFillPercentage) <= EPSILON && distance < bestDistance - EPSILON) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the fill percentage of a container based on its pending liters for the day.
   *
   * @param container container to inspect
   * @return fill percentage (may exceed 100 when overflowed)
   */
  private double getFillPercentage(Container container) {
    double capacityLiters = container.getCapacityLiters();
    if (capacityLiters <= EPSILON) {
      return 0.0;
    }
    return (getPendingLiters(container) / capacityLiters) * 100.0;
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
   * @param currentTime the vehicle clock before travelling back to the facility
   * @return the vehicle clock after travelling and unloading at the facility
   */ 
  private LocalTime returnToFacilityAndUnload(
      DailyPlan dailyPlan,
      Vehicle vehicle,
      Object currentLocation,
      Facility facility,
      LocalTime currentTime) {
    if (dailyPlan == null || vehicle == null || currentLocation == null || facility == null) {
      return currentTime;
    }
    double distanceToFacility = calculateDistanceToFacility(currentLocation, facility);
    LocalTime arrivalAtFacility = advanceTime(currentTime, problem.getAverageTransferTimeMinutes());
    dailyPlan.addFacilityStop(distanceToFacility, new ArrayList<>(), arrivalAtFacility);
    vehicle.emptyLoad();
    return advanceTime(arrivalAtFacility, (long) facility.getUnloadingTimeMinutes());
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
