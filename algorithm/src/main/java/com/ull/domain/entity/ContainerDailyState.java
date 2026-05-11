package com.ull.domain.entity;

import java.util.Objects;

import com.ull.domain.enums.ContainerStatus;

/**
 * Represents the monitoring state of a container for a specific day.
 *
 * <p>Tracks the filling level of a container throughout a planning day,
 * including whether it has reached or exceeded its capacity.
 */
public class ContainerDailyState {

  public static final String CONTAINER_ID_NOT_DEFINED = "Container id is not defined";
  public static final String PLAN_DAY_NOT_VALID = "Plan day must be >= 1";
  public static final String DAILY_FILLING_NOT_VALID = "Daily filling must be >= 0";
  public static final String CONTAINER_CAPACITY_NOT_VALID = "Container capacity must be > 0";
  public static final String DAILY_DEMAND_NOT_VALID = "Daily demand must be >= 0";

  private final String containerId;
  private final int planDay;
  private final double dailyFillingLiters;
  private final double containerCapacityLiters;
  private final double dailyDemandLitersPerDay;
  private final ContainerStatus status;

  /**
   * Creates a container daily state.
   *
   * @param containerId the container identifier
   * @param planDay the day in the planning horizon (1-based)
   * @param dailyFillingLiters the liters accumulated during the day
   * @param containerCapacityLiters the container's maximum capacity
   * @param dailyDemandLitersPerDay the container's daily demand in liters
   * @param status the current status (CORRECT or OVERFLOWED)
   */
  public ContainerDailyState(
      String containerId,
      int planDay,
      double dailyFillingLiters,
      double containerCapacityLiters,
      double dailyDemandLitersPerDay,
      ContainerStatus status) {
    validateContainerId(containerId);
    validatePlanDay(planDay);
    validateDailyFilling(dailyFillingLiters);
    validateContainerCapacity(containerCapacityLiters);
    validateDailyDemand(dailyDemandLitersPerDay);

    this.containerId = containerId;
    this.planDay = planDay;
    this.dailyFillingLiters = dailyFillingLiters;
    this.containerCapacityLiters = containerCapacityLiters;
    this.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
    this.status = status != null ? status : ContainerStatus.CORRECT;
  }

  /**
     * Creates a container daily state with CORRECT status (default) and zero demand.
   *
   * @param containerId the container identifier
   * @param planDay the day in the planning horizon (1-based)
   * @param dailyFillingLiters the liters accumulated during the day
   * @param containerCapacityLiters the container's maximum capacity
   */
  public ContainerDailyState(
      String containerId,
      int planDay,
      double dailyFillingLiters,
      double containerCapacityLiters) {
    this(containerId, planDay, dailyFillingLiters, containerCapacityLiters, 0.0, ContainerStatus.CORRECT);
  }

  /**
   * Creates a container daily state with CORRECT status (default).
   *
   * @param containerId the container identifier
   * @param planDay the day in the planning horizon (1-based)
   * @param dailyFillingLiters the liters accumulated during the day
   * @param containerCapacityLiters the container's maximum capacity
   * @param dailyDemandLitersPerDay the container's daily demand in liters
   */
  public ContainerDailyState(
      String containerId,
      int planDay,
      double dailyFillingLiters,
      double containerCapacityLiters,
      double dailyDemandLitersPerDay) {
    this(containerId, planDay, dailyFillingLiters, containerCapacityLiters, dailyDemandLitersPerDay, ContainerStatus.CORRECT);
  }

  private void validateContainerId(String containerId) {
    if (containerId == null || containerId.isBlank()) {
      throw new IllegalArgumentException(CONTAINER_ID_NOT_DEFINED);
    }
  }

  private void validatePlanDay(int planDay) {
    if (planDay < 1) {
      throw new IllegalArgumentException(PLAN_DAY_NOT_VALID);
    }
  }

  private void validateDailyFilling(double dailyFillingLiters) {
    if (dailyFillingLiters < 0) {
      throw new IllegalArgumentException(DAILY_FILLING_NOT_VALID);
    }
  }

  private void validateContainerCapacity(double containerCapacityLiters) {
    if (containerCapacityLiters <= 0) {
      throw new IllegalArgumentException(CONTAINER_CAPACITY_NOT_VALID);
    }
  }

  private void validateDailyDemand(double dailyDemandLitersPerDay) {
    if (dailyDemandLitersPerDay < 0) {
      throw new IllegalArgumentException(DAILY_DEMAND_NOT_VALID);
    }
  }

  public String getContainerId() {
    return this.containerId;
  }

  public int getPlanDay() {
    return this.planDay;
  }

  public double getDailyFillingLiters() {
    return this.dailyFillingLiters;
  }

  public double getContainerCapacityLiters() {
    return this.containerCapacityLiters;
  }

  public double getDailyDemandLitersPerDay() {
    return this.dailyDemandLitersPerDay;
  }

  public ContainerStatus getStatus() {
    return this.status;
  }

  /**
   * Calculates the fill percentage of the container.
   *
   * @return the fill percentage (0-100 or higher if overflowed)
   */
  public double getFillPercentage() {
    return (this.dailyFillingLiters / this.containerCapacityLiters) * 100.0;
  }

  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }
    ContainerDailyState other = (ContainerDailyState) otherObject;
    return this.planDay == other.planDay && Objects.equals(this.containerId, other.containerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.containerId, this.planDay);
  }

  @Override
  public String toString() {
    return String.format(
        "ContainerDailyState{containerId=%s, planDay=%d, dailyFillingLiters=%.2f, capacity=%.2f, dailyDemandLitersPerDay=%.2f, status=%s, fillPercentage=%.1f%%}",
        this.containerId,
        this.planDay,
        this.dailyFillingLiters,
        this.containerCapacityLiters,
        this.dailyDemandLitersPerDay,
        this.status,
        getFillPercentage());
  }
}
