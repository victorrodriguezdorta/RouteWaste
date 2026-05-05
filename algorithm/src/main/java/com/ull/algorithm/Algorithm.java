package com.ull.algorithm;

import java.time.LocalDate;
import java.util.List;

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
 * <p><b>Current implementation:</b> stub that assigns every container to the
 * first available facility and creates one {@link DailyPlan} per
 * facility-vehicle pair for day 1, visiting all assigned containers in their
 * natural order. No optimisation is performed; this is intended only to
 * verify that the full input → algorithm → output pipeline works end-to-end.
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
   * @return the computed (or stub) solution
   */
  public DeliveryPlanningSolution run() {
    DeliveryPlanningSolution solution = new DeliveryPlanningSolution();
    solution.updateMaxBudget(problem.getMaxBudget());

    List<FacilityWithVehicles> facilitiesWithVehicles = problem.getFacilitiesWithVehicles();
    List<Container> containers = problem.getContainers();

    // Nothing to plan if there are no facilities or no containers
    if (facilitiesWithVehicles.isEmpty() || containers.isEmpty()) {
      solution.updateStatus(DeliveryPlanningSolution.Status.INFEASIBLE);
      return solution;
    }

    // -----------------------------------------------------------------------
    // Phase 1 – Stub clustering: assign every container to the first facility
    // -----------------------------------------------------------------------
    FacilityWithVehicles firstFacilityGroup = facilitiesWithVehicles.get(0);
    Facility firstFacility = firstFacilityGroup.getFacility();

    FacilityCluster cluster = new FacilityCluster(firstFacility);
    for (Container container : containers) {
      cluster.addContainer(container);
    }
    solution.addCluster(cluster);

    // -----------------------------------------------------------------------
    // Phase 2 – Stub routing: one DailyPlan per vehicle for day 1,
    //            visiting all containers in sequence
    // -----------------------------------------------------------------------
    LocalDate serviceDate = LocalDate.now().plusDays(1);
    List<Vehicle> vehicles = firstFacilityGroup.getVehicles();

    for (Vehicle vehicle : vehicles) {
      DailyPlan plan = new DailyPlan(1, serviceDate, firstFacility, vehicle);

      for (Container container : containers) {
        // Stub: collect 0 kg / 0 liters — replace with real values once algorithm is ready
        plan.addStop(container, 0.0, 0.0);
      }

      solution.addDailyPlan(plan);
    }

    solution.updateStatus(DeliveryPlanningSolution.Status.SUBOPTIMAL);
    return solution;
  }
}
