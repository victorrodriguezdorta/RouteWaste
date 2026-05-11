package com.ull.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ull.domain.entity.ContainerDailyState;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.valueobject.cost.MaximumBudget;

/**
 * Represents the output of the algorithm execution.
 *
 * <p>Holds:
 * <ul>
 *   <li>The execution status (OPTIMAL, SUBOPTIMAL, INFEASIBLE).</li>
 *   <li>The clustering result: one {@link FacilityCluster} per facility,
 *       each containing the containers assigned to it.</li>
 *   <li>The routing result: one {@link DailyPlan} per vehicle per day,
 *       each containing the ordered stops for that vehicle on that day.</li>
 * </ul>
 */
public class DeliveryPlanningSolution {

  public static final String CLUSTERS_NOT_DEFINED = "Clusters list is not defined";
  public static final String DAILY_PLANS_NOT_DEFINED = "Daily plans list is not defined";

  /**
   * Possible statuses for the algorithm execution.
   */
  public enum Status {
    /** The solver found a provably optimal solution. */
    OPTIMAL,
    /** The solver found a feasible but not provably optimal solution. */
    SUBOPTIMAL,
    /** The problem has no feasible solution with the given constraints. */
    INFEASIBLE
  }

  private Status status;
  private final Instant executedAt;
  private MaximumBudget maxBudget;
  private final List<FacilityCluster> clusters;
  private final List<DailyPlan> dailyPlans;
  private final List<ContainerDailyState> containerStateMonitoring;

  /**
   * Creates an empty solution stamped with the current instant.
   * Status defaults to {@link Status#INFEASIBLE} until the algorithm updates it.
   */
  public DeliveryPlanningSolution() {
    this.status = Status.INFEASIBLE;
    this.executedAt = Instant.now();
    this.clusters = new ArrayList<>();
    this.dailyPlans = new ArrayList<>();
    this.containerStateMonitoring = new ArrayList<>();
  }

  // -------------------------------------------------------------------------
  // Status
  // -------------------------------------------------------------------------

  public Status getStatus() {
    return this.status;
  }

  public void updateStatus(Status status) {
    if (status == null) {
      throw new IllegalArgumentException("Status is not defined");
    }
    this.status = status;
  }

  // -------------------------------------------------------------------------
  // Execution timestamp
  // -------------------------------------------------------------------------

  public Instant getExecutedAt() {
    return this.executedAt;
  }

  public MaximumBudget getMaxBudget() {
    return this.maxBudget;
  }

  public void updateMaxBudget(MaximumBudget maxBudget) {
    this.maxBudget = maxBudget;
  }

  // -------------------------------------------------------------------------
  // Clusters
  // -------------------------------------------------------------------------

  /**
   * Returns an unmodifiable view of the facility clusters.
   *
   * @return the clustering result
   */
  public List<FacilityCluster> getClusters() {
    return Collections.unmodifiableList(this.clusters);
  }

  /**
   * Adds a facility cluster to the solution.
   *
   * @param cluster the cluster to add
   */
  public void addCluster(FacilityCluster cluster) {
    if (cluster == null) {
      throw new IllegalArgumentException(CLUSTERS_NOT_DEFINED);
    }
    this.clusters.add(cluster);
  }

  // -------------------------------------------------------------------------
  // Daily plans (routes)
  // -------------------------------------------------------------------------

  /**
   * Returns an unmodifiable view of the daily plans.
   *
   * @return the routing result
   */
  public List<DailyPlan> getDailyPlans() {
    return Collections.unmodifiableList(this.dailyPlans);
  }

  /**
   * Adds a daily vehicle route to the solution.
   *
   * @param dailyPlan the daily plan to add
   */
  public void addDailyPlan(DailyPlan dailyPlan) {
    if (dailyPlan == null) {
      throw new IllegalArgumentException(DAILY_PLANS_NOT_DEFINED);
    }
    this.dailyPlans.add(dailyPlan);
  }

  // -------------------------------------------------------------------------
  // Container state monitoring
  // -------------------------------------------------------------------------

  /**
   * Returns an unmodifiable view of the container daily states.
   *
   * @return the container state monitoring list
   */
  public List<ContainerDailyState> getContainerStateMonitoring() {
    return Collections.unmodifiableList(this.containerStateMonitoring);
  }

  /**
   * Adds a container daily state to the monitoring list.
   *
   * @param containerDailyState the container daily state to add
   */
  public void addContainerDailyState(ContainerDailyState containerDailyState) {
    if (containerDailyState == null) {
      throw new IllegalArgumentException("Container daily state is not defined");
    }
    this.containerStateMonitoring.add(containerDailyState);
  }

  // -------------------------------------------------------------------------
  // Derived metrics
  // -------------------------------------------------------------------------

  /**
   * Returns the total route distance across all daily plans, in meters.
   *
   * @return total distance in meters
   */
  public double getTotalDistanceMeters() {
    return this.dailyPlans.stream()
        .mapToDouble(DailyPlan::getTotalDistanceMeters)
        .sum();
  }

  /**
   * Returns the total kilograms collected across all daily plans.
   *
   * @return total collected kilograms
   */
  public double getTotalCollectedKilograms() {
    return this.dailyPlans.stream()
        .mapToDouble(DailyPlan::getTotalCollectedKilograms)
        .sum();
  }

  /**
   * Returns the total liters collected across all daily plans.
   *
   * @return total collected liters
   */
  public double getTotalCollectedLiters() {
    return this.dailyPlans.stream()
        .mapToDouble(DailyPlan::getTotalCollectedLiters)
        .sum();
  }

  @Override
  public String toString() {
    return String.format(
        "DeliveryPlanningSolution{status=%s, executedAt=%s, clusters=%d, dailyPlans=%d,"
            + " totalDistanceMeters=%.2f}",
        this.status,
        this.executedAt,
        this.clusters.size(),
        this.dailyPlans.size(),
        getTotalDistanceMeters());
  }
}