package com.ull.domain;

import com.ull.domain.entity.ContainerDailyState;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.valueobject.cost.MaximumBudget;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
  public static final String CONTAINER_DAILY_STATE_NOT_DEFINED = "Container daily state is not defined";
  public static final String DAILY_PLANS_NOT_DEFINED = "Daily plans list is not defined";
  public static final String STATUS_NOT_DEFINED = "Status is not defined";

  private DeliveryPlanningStatus status;
  private final Instant executedAt;
  private MaximumBudget maxBudget;
  private final List<FacilityCluster> clusters;
  private final List<DailyPlan> dailyPlans;
  private final List<ContainerDailyState> containerStateMonitoring;

  /**
   * Creates an empty solution stamped with the current instant.
   * Status defaults to {@link DeliveryPlanningStatus#INFEASIBLE} until the algorithm updates it.
   */
  public DeliveryPlanningSolution() {
    this.status = DeliveryPlanningStatus.INFEASIBLE;
    this.executedAt = Instant.now();
    this.clusters = new ArrayList<>();
    this.dailyPlans = new ArrayList<>();
    this.containerStateMonitoring = new ArrayList<>();
  }

  /**
   * Returns the current execution status.
   *
   * @return current planning status
   */
  public DeliveryPlanningStatus getStatus() {
    return this.status;
  }

  /**
   * Updates the execution status.
   *
   * @param status the new planning status
   */
  public void updateStatus(DeliveryPlanningStatus status) {
    if (status == null) {
      throw new IllegalArgumentException(STATUS_NOT_DEFINED);
    }
    this.status = status;
  }

  /**
   * Returns the instant when this solution was created.
   *
   * @return execution timestamp
   */
  public Instant getExecutedAt() {
    return this.executedAt;
  }

  /**
   * Returns the maximum budget used by the algorithm.
   *
   * @return maximum budget, or null when not provided
   */
  public MaximumBudget getMaxBudget() {
    return this.maxBudget;
  }

  /**
   * Updates the maximum budget associated with this solution.
   *
   * @param maxBudget the maximum budget to store
   */
  public void updateMaxBudget(MaximumBudget maxBudget) {
    this.maxBudget = maxBudget;
  }

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
      throw new IllegalArgumentException(CONTAINER_DAILY_STATE_NOT_DEFINED);
    }
    this.containerStateMonitoring.add(containerDailyState);
  }

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

  /**
   * Returns a readable representation of this solution.
   *
   * @return text containing status, timestamp, counts, and total distance
   */
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