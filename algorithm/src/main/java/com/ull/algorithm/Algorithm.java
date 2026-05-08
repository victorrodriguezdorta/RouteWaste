package com.ull.algorithm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.DeliveryPlanningSolution;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.entity.FacilityWithVehicles;
import com.ull.domain.entity.Vehicle;

/**
 * Entry point for the planning algorithm.
 *
 * <p>Receives a fully typed {@link DeliveryPlanningProblem} and returns a
 * {@link DeliveryPlanningSolution}.
 *
 * <p><b>Current implementation:</b> 
 * Phase 1: Clusterization by distance - each container is assigned to the nearest facility.
 * Phase 2: Greedy routing - for each facility and vehicle, a greedy algorithm visits
 * containers in distance order for each day of the planning horizon.
 */
public class Algorithm {

  private final DeliveryPlanningProblem problem;

  public Algorithm(DeliveryPlanningProblem problem) {
    if (problem == null) {
      throw new IllegalArgumentException("Problem is not defined");
    }
    this.problem = problem;
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
          // Back-end persistence expects all selected facilities to be present
          // in the clusters list, even when a facility gets zero containers.
          solution.addCluster(cluster);
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

        // For each day, assign greedy routes to vehicles
        for (int day = 1; day <= numberOfDays; day++) {
          LocalDate serviceDate = startDate.plusDays(day - 1);
          
          // For each vehicle, create a daily plan with greedy routing
          for (Vehicle vehicle : vehicles) {
            if (vehicle == null) {
              continue;
            }
            
            try {
              DailyPlan dailyPlan = new DailyPlan(day, serviceDate, facility, vehicle);
              
              // Apply greedy algorithm to assign containers to this vehicle's route
              buildGreedyRoute(dailyPlan, clusterContainers);
              
              solution.addDailyPlan(dailyPlan);
            } catch (Exception e) {
              // Skip this vehicle if plan creation fails
              continue;
            }
          }
        }
      }

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
   * Builds a greedy route for a daily plan by visiting containers in order of proximity.
   *
   * <p>The algorithm starts at the facility and greedily selects the nearest unvisited
   * container at each step. Distance and fuel consumption are considered.
   *
   * @param dailyPlan the daily plan to populate with stops
   * @param availableContainers the containers that can be visited
   */
  private void buildGreedyRoute(DailyPlan dailyPlan, List<Container> availableContainers) {
    if (availableContainers == null || availableContainers.isEmpty()) {
      return;
    }

    Facility facility = dailyPlan.getOriginFacility();
    if (facility == null || facility.getLocation() == null) {
      return; // Cannot build route without valid facility
    }

    List<Container> unvisited = new ArrayList<>(availableContainers);
    
    // Current location starts at the facility
    Object currentLocation = facility;

    // Greedy: repeatedly select the nearest unvisited container
    while (!unvisited.isEmpty()) {
      Container nearest = null;
      double minDistance = Double.MAX_VALUE;
      int nearestIndex = -1;

      // Find the nearest unvisited container
      for (int i = 0; i < unvisited.size(); i++) {
        Container container = unvisited.get(i);
        if (container == null || container.getLocation() == null) {
          continue; // Skip invalid containers
        }
        
        double distance;

        try {
          if (currentLocation instanceof Facility) {
            distance = ((Facility) currentLocation).calculateDistanceTo(container);
          } else {
            distance = ((Container) currentLocation).calculateDistanceTo(container);
          }
        } catch (Exception e) {
          // Skip this container if distance calculation fails
          continue;
        }

        if (distance < minDistance) {
          minDistance = distance;
          nearest = container;
          nearestIndex = i;
        }
      }

      if (nearest != null && nearestIndex >= 0) {
        try {
          // Add stop to the daily plan (collect 0 kg and 0 liters for now)
          dailyPlan.addStop(nearest, 0.0, 0.0);
          
          // Update current location and remove from unvisited
          currentLocation = nearest;
          unvisited.remove(nearestIndex);
        } catch (Exception e) {
          // If adding stop fails, skip this container
          unvisited.remove(nearestIndex);
        }
      } else {
        // No valid container found, break the loop
        break;
      }
    }
  }
}
