package com.ull.algorithm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * <p><b>Current implementation:</b> 
 * Phase 1: Clusterization by distance - each container is assigned to the nearest facility.
 * Phase 2: Greedy routing - for each facility and vehicle, a greedy algorithm visits
 * containers in distance order for each day of the planning horizon, respecting vehicle capacity.
 * Phase 3: Container state monitoring - tracks the filling level of each container per day.
 */
public class Algorithm {

  private final DeliveryPlanningProblem problem;
  /** Map to track container states: key = "containerId_day", value = current liters */
  private final Map<String, Double> containerDailyState;
  /** Set to track which containers have been collected on each day */
  private final Set<String> collectedContainersPerDay;

  public Algorithm(DeliveryPlanningProblem problem) {
    if (problem == null) {
      throw new IllegalArgumentException("Problem is not defined");
    }
    this.problem = problem;
    this.containerDailyState = new HashMap<>();
    this.collectedContainersPerDay = new HashSet<>();
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

    // Nothing to plan if there are no facilities or no containers
    if (facilitiesWithVehicles == null || facilitiesWithVehicles.isEmpty() 
        || containers == null || containers.isEmpty()) {
      solution.updateStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
      return solution;
    }

    try {
      // -----------------------------------------------------------------------
      // Phase 1 – Clustering: assign each container to the nearest facility
      // -----------------------------------------------------------------------
      List<FacilityCluster> clusters = clusterizeByDistance(facilitiesWithVehicles, containers);
      
      // If no clusters were created or all clusters are empty, mark as infeasible
      if (clusters == null || clusters.isEmpty()) {
        solution.updateStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
        return solution;
      }
      
      for (FacilityCluster cluster : clusters) {
        if (cluster != null) {
          solution.addCluster(cluster);
        }
      }

      // Initialize container states (all start at 0 liters)
      for (Container container : containers) {
        if (container != null) {
          container.emptyCurrentLiters();
        }
      }

      // -----------------------------------------------------------------------
      // Phase 2 – Greedy routing: for each facility, process each vehicle
      //            and create daily plans for each day in the planning horizon
      // -----------------------------------------------------------------------
      int numberOfDays = problem.getNumberOfDays();
      if (numberOfDays < 1) {
        solution.updateStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
        return solution;
      }
      
      LocalDate startDate = LocalDate.now().plusDays(1);

      for (FacilityCluster cluster : clusters) {
        if (cluster == null) {
          continue;
        }
        
        Facility facility = cluster.getFacility();
        if (facility == null || facility.getId() == null) {
          continue;
        }
        
        List<Container> clusterContainers = new ArrayList<>(cluster.getAssignedContainers());
        
        // Find the corresponding FacilityWithVehicles for this facility
        FacilityWithVehicles facilityWithVehicles = facilitiesWithVehicles.stream()
            .filter(fw -> fw != null && fw.getFacility() != null 
                && facility.getId().equals(fw.getFacility().getId()))
            .findFirst()
            .orElse(null);

        if (facilityWithVehicles == null || facilityWithVehicles.getVehicles() == null) {
          continue; // Skip if facility not found or has no vehicles
        }

        List<Vehicle> vehicles = facilityWithVehicles.getVehicles();
        if (vehicles.isEmpty()) {
          continue; // Skip if no vehicles assigned
        }

        // For each day, track which containers have been collected
        for (int day = 1; day <= numberOfDays; day++) {
          
          LocalDate serviceDate = startDate.plusDays(day - 1);
          
          // Add daily demand to all containers for this day
          for (Container container : clusterContainers) {
            if (container != null) {
              double currentLiters = container.getCurrentLiters();
              double newLiters = currentLiters + container.getDailyDemandLitersPerDay();
              container.updateCurrentLiters(Math.min(newLiters, container.getCapacityLiters()));
            }
          }
          
          // Only create daily plans for the first day (day 1). Subsequent days are used
          // to update container fill levels so that uncollected containers may overflow.
          if (day == 1) {
            for (Vehicle vehicle : vehicles) {
              // Reset collected set before each vehicle so each vehicle gets its own plan
              collectedContainersPerDay.clear();
              if (vehicle == null) {
                continue;
              }

              try {
                DailyPlan dailyPlan = new DailyPlan(day, serviceDate, facility, vehicle);

                // Apply greedy algorithm to assign containers to this vehicle's route
                buildGreedyRoute(dailyPlan, clusterContainers, day);

                solution.addDailyPlan(dailyPlan);
              } catch (Exception e) {
                // Skip this vehicle if plan creation fails
                continue;
              }
            }
          }
        }
      }

      // -----------------------------------------------------------------------
      // Phase 3 – Container state monitoring
      // -----------------------------------------------------------------------
      buildContainerStateMonitoring(solution, containers, numberOfDays);

      // Set status based on whether we have daily plans
      if (solution.getDailyPlans() != null && !solution.getDailyPlans().isEmpty()) {
        solution.updateStatus(DeliveryPlanningSolution.Status.SUBOPTIMAL);
      } else {
        solution.updateStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
      }
      
    } catch (Exception e) {
      // Catch any unexpected errors and return infeasible
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
    
    // Create a map of facility to cluster
    Map<String, FacilityCluster> clusterMap = new HashMap<>();
    for (FacilityWithVehicles fw : facilitiesWithVehicles) {
      if (fw == null || fw.getFacility() == null) {
        continue; // Skip invalid facilities
      }
      clusterMap.put(fw.getFacility().getId(), new FacilityCluster(fw.getFacility()));
    }

    // Assign each container to the nearest facility
    for (Container container : containers) {
      if (container == null || container.getLocation() == null) {
        continue; // Skip invalid containers
      }
      
      Facility nearestFacility = null;
      double minDistance = Double.MAX_VALUE;

      for (FacilityWithVehicles fw : facilitiesWithVehicles) {
        if (fw == null || fw.getFacility() == null) {
          continue; // Skip invalid facilities
        }
        
        Facility facility = fw.getFacility();
        if (facility.getLocation() == null) {
          continue; // Skip facilities without location
        }
        
        double distance = container.calculateDistanceTo(facility);
        
        if (distance < minDistance) {
          minDistance = distance;
          nearestFacility = facility;
        }
      }

      // Assign to nearest facility if found and exists in cluster map
      if (nearestFacility != null && clusterMap.containsKey(nearestFacility.getId())) {
        clusterMap.get(nearestFacility.getId()).addContainer(container);
      }
    }

    return new ArrayList<>(clusterMap.values());
  }

  /**
   * Builds a greedy route for a daily plan by visiting containers in order of proximity,
   * respecting the vehicle's capacity.
   *
   * <p>The algorithm:
   * 1. Starts at the facility
   * 2. Greedily selects the nearest unvisited container at each step
   * 3. Collects waste if the vehicle has capacity
   * 4. Generates an alert and stops if the vehicle is full
   * 5. Marks containers as collected so other vehicles skip them
   *
   * @param dailyPlan the daily plan to populate with stops
   * @param availableContainers the containers that can be visited
   * @param planDay the current day in the planning horizon
   */
  private void buildGreedyRoute(DailyPlan dailyPlan, List<Container> availableContainers, int planDay) {
    if (availableContainers == null || availableContainers.isEmpty()) {
      return;
    }

    Facility facility = dailyPlan.getOriginFacility();
    Vehicle vehicle = dailyPlan.getVehicle();
    
    if (facility == null || facility.getLocation() == null || vehicle == null) {
      return;
    }

    double vehicleCapacity = vehicle.getCapacityLiters();
    double vehicleCurrentLoad = vehicle.getCurrentLoadLiters();
    
    List<Container> unvisited = new ArrayList<>(availableContainers);
    
    // Current location starts at the facility
    Object currentLocation = facility;

    // Greedy: repeatedly select the nearest unvisited container that hasn't been collected yet
    while (!unvisited.isEmpty()) {
      Container nearest = null;
      double minDistance = Double.MAX_VALUE;
      int nearestIndex = -1;

      // Find the nearest unvisited container that hasn't been collected today
      for (int i = 0; i < unvisited.size(); i++) {
        Container container = unvisited.get(i);
        if (container == null || container.getLocation() == null) {
          continue; // Skip invalid containers
        }

        // Skip if already collected today
        if (collectedContainersPerDay.contains(container.getId())) {
          continue;
        }
        
        double distance;
        try {
          if (currentLocation instanceof Facility) {
            distance = ((Facility) currentLocation).calculateDistanceTo(container);
          } else {
            distance = ((Container) currentLocation).calculateDistanceTo(container);
          }
        } catch (Exception e) {
          continue; // Skip this container if distance calculation fails
        }

        if (distance < minDistance) {
          minDistance = distance;
          nearest = container;
          nearestIndex = i;
        }
      }

      if (nearest != null && nearestIndex >= 0) {
        double containerCurrentLiters = nearest.getCurrentLiters();

        // Remaining capacity in vehicle
        double remainingCapacity = vehicleCapacity - vehicleCurrentLoad;
        if (remainingCapacity <= 0) {
          // Vehicle already full: return to the facility, unload, and continue collecting.
          returnToFacilityAndUnload(vehicle, facility);
          vehicleCurrentLoad = 0.0;
          currentLocation = facility;
          continue;
        }

        // Amount to collect (partial if necessary)
        double toCollect = Math.min(containerCurrentLiters, remainingCapacity);

        if (toCollect > 0) {
          try {
            List<Alert> stopAlerts = new ArrayList<>();

            // Add stop with collected liters (kilograms not modeled, use 0.0)
            dailyPlan.addStop(
                nearest,
                0.0,
                toCollect,
                containerCurrentLiters, // actual liters before collection
                stopAlerts
            );

            // Update vehicle load (route planning only)
            vehicleCurrentLoad += toCollect;
            vehicle.updateCurrentLoadLiters(vehicleCurrentLoad);

            // Do NOT modify the real container state here; keep planning read-only
            // Remove the container from the local unvisited list (one visit per vehicle)
            currentLocation = nearest;
            unvisited.remove(nearestIndex);

            if (vehicleCurrentLoad >= vehicleCapacity) {
              returnToFacilityAndUnload(vehicle, facility);
              vehicleCurrentLoad = 0.0;
              currentLocation = facility;
            }
          } catch (Exception e) {
            // If adding stop fails, skip this container
            unvisited.remove(nearestIndex);
          }
        } else {
          // Nothing to collect from this container, skip it
          unvisited.remove(nearestIndex);
        }
      } else {
        // No valid container found, break the loop
        break;
      }
    }

    if (vehicleCurrentLoad > 0.0) {
      returnToFacilityAndUnload(vehicle, facility);
    }
  }

  private void returnToFacilityAndUnload(Vehicle vehicle, Facility facility) {
    if (vehicle == null || facility == null) {
      return;
    }

    vehicle.emptyLoad();
  }

  /**
   * Builds the container state monitoring data for the solution.
   * 
   * <p>This tracks the filling level of each container for each day.
   *
   * @param solution the solution to populate
   * @param containers all containers in the problem
   * @param numberOfDays number of days in the planning horizon
   */
  private void buildContainerStateMonitoring(
      DeliveryPlanningSolution solution,
      List<Container> containers,
      int numberOfDays) {
    
    for (Container container : containers) {
      if (container == null) {
        continue;
      }
      
      for (int day = 1; day <= numberOfDays; day++) {
        // Calculate the real container filling for this day.
        double dailyFillingBeforeCap = container.getDailyDemandLitersPerDay() * day;
        double capacity = container.getCapacityLiters();

        // Determine status from the real filling.
        ContainerStatus status = dailyFillingBeforeCap > capacity ? ContainerStatus.OVERFLOWED : ContainerStatus.CORRECT;
        
        // Create container daily state
        ContainerDailyState state = new ContainerDailyState(
            container.getId(),
            day,
          dailyFillingBeforeCap,
            capacity,
            container.getDailyDemandLitersPerDay(),
            status);
        
        solution.addContainerDailyState(state);
      }
    }
  }
}
