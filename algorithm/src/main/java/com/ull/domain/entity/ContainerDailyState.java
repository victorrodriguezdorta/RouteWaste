package com.ull.domain.entity;

import com.ull.domain.enumerate.ContainerStatus;
import java.time.LocalTime;
import java.util.Objects;

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
  private static final int MIN_PLAN_DAY = 1;
  private static final double ZERO_LITERS = 0.0;

  private final String containerId;
  private final int planDay;
  private final double dailyFillingLiters;
  private final double dailyFillingLitersBeforeCollection;
  private final double containerCapacityLiters;
  private final double dailyDemandLitersPerDay;
  private final ContainerStatus status;
  /**
   * Time of day this snapshot represents.
   *
   * <p>This value is {@code null} for plain daily snapshots without an exact moment.
   */
  private final LocalTime time;

  /**
   * Creates a container daily state.
   *
   * @param containerId the container identifier
   * @param planDay the day in the planning horizon (1-based)
   * @param dailyFillingLiters the liters remaining at the end of the day
   * @param dailyFillingLitersBeforeCollection the liters accumulated before any collection
   * @param containerCapacityLiters the container's maximum capacity
   * @param dailyDemandLitersPerDay the container's daily demand in liters
   * @param status the current status (CORRECT or OVERFLOWED)
   */
  public ContainerDailyState(
      String containerId,
      int planDay,
      double dailyFillingLiters,
      double dailyFillingLitersBeforeCollection,
      double containerCapacityLiters,
      double dailyDemandLitersPerDay,
      ContainerStatus status) {
    this(
        containerId,
        planDay,
        dailyFillingLiters,
        dailyFillingLitersBeforeCollection,
        containerCapacityLiters,
        dailyDemandLitersPerDay,
        status,
        null);
  }

  /**
   * Creates a container state snapshot tied to a specific moment of the day.
   *
   * @param containerId the container identifier
   * @param planDay the day in the planning horizon (1-based)
   * @param dailyFillingLiters the liters present at the snapshot moment
   * @param dailyFillingLitersBeforeCollection the liters present before the related collection
   * @param containerCapacityLiters the container's maximum capacity
   * @param dailyDemandLitersPerDay the container's daily demand in liters
   * @param status the current status (CORRECT or OVERFLOWED)
   * @param time the time of day represented by this snapshot
   */
  public ContainerDailyState(
      String containerId,
      int planDay,
      double dailyFillingLiters,
      double dailyFillingLitersBeforeCollection,
      double containerCapacityLiters,
      double dailyDemandLitersPerDay,
      ContainerStatus status,
      LocalTime time) {
    validateContainerId(containerId);
    validatePlanDay(planDay);
    validateDailyFilling(dailyFillingLiters);
    validateDailyFilling(dailyFillingLitersBeforeCollection);
    validateContainerCapacity(containerCapacityLiters);
    validateDailyDemand(dailyDemandLitersPerDay);
    this.containerId = containerId;
    this.planDay = planDay;
    this.dailyFillingLiters = dailyFillingLiters;
    this.dailyFillingLitersBeforeCollection = dailyFillingLitersBeforeCollection;
    this.containerCapacityLiters = containerCapacityLiters;
    this.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
    this.status = status != null ? status : ContainerStatus.CORRECT;
    this.time = time;
  }

  /**
   * Creates a container daily state without an explicit before-collection snapshot.
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
    this(
        containerId,
        planDay,
        dailyFillingLiters,
        dailyFillingLiters,
        containerCapacityLiters,
        dailyDemandLitersPerDay,
        status);
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
    this(containerId, planDay, dailyFillingLiters, containerCapacityLiters, ZERO_LITERS, ContainerStatus.CORRECT);
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

  /**
   * Validates that the container identifier is present.
   *
   * @param containerId the container identifier to validate
   */
  private void validateContainerId(String containerId) {
    if (containerId == null || containerId.isBlank()) {
      throw new IllegalArgumentException(CONTAINER_ID_NOT_DEFINED);
    }
  }

  /**
   * Validates that the plan day is inside the planning horizon.
   *
   * @param planDay the plan day to validate
   */
  private void validatePlanDay(int planDay) {
    if (planDay < MIN_PLAN_DAY) {
      throw new IllegalArgumentException(PLAN_DAY_NOT_VALID);
    }
  }

  /**
   * Validates that the daily filling value is not negative.
   *
   * @param dailyFillingLiters the daily filling value to validate
   */
  private void validateDailyFilling(double dailyFillingLiters) {
    if (dailyFillingLiters < ZERO_LITERS) {
      throw new IllegalArgumentException(DAILY_FILLING_NOT_VALID);
    }
  }

  /**
   * Validates that the container capacity is positive.
   *
   * @param containerCapacityLiters the capacity value to validate
   */
  private void validateContainerCapacity(double containerCapacityLiters) {
    if (containerCapacityLiters <= ZERO_LITERS) {
      throw new IllegalArgumentException(CONTAINER_CAPACITY_NOT_VALID);
    }
  }

  /**
   * Validates that the daily demand value is not negative.
   *
   * @param dailyDemandLitersPerDay the daily demand value to validate
   */
  private void validateDailyDemand(double dailyDemandLitersPerDay) {
    if (dailyDemandLitersPerDay < ZERO_LITERS) {
      throw new IllegalArgumentException(DAILY_DEMAND_NOT_VALID);
    }
  }

  /**
   * Returns the monitored container identifier.
   *
   * @return container identifier
   */
  public String getContainerId() {
    return this.containerId;
  }

  /**
   * Returns the planning day for this state.
   *
   * @return one-based planning day
   */
  public int getPlanDay() {
    return this.planDay;
  }

  /**
   * Returns the daily filling in liters.
   *
   * @return daily filling liters
   */
  public double getDailyFillingLiters() {
    return this.dailyFillingLiters;
  }

  /**
   * Returns the liters accumulated before any collection on the day.
   *
   * @return daily filling liters before collection
   */
  public double getDailyFillingLitersBeforeCollection() {
    return this.dailyFillingLitersBeforeCollection;
  }

  /**
   * Returns the container capacity in liters.
   *
   * @return container capacity liters
   */
  public double getContainerCapacityLiters() {
    return this.containerCapacityLiters;
  }

  /**
   * Returns the daily demand in liters per day.
   *
   * @return daily demand liters per day
   */
  public double getDailyDemandLitersPerDay() {
    return this.dailyDemandLitersPerDay;
  }

  /**
   * Returns the computed container status.
   *
   * @return container status for the day
   */
  public ContainerStatus getStatus() {
    return this.status;
  }

  /**
   * Returns the time of day represented by this snapshot.
   *
   * @return snapshot time, or {@code null} for plain daily snapshots
   */
  public LocalTime getTime() {
    return this.time;
  }

  /**
   * Calculates the fill percentage of the container.
   *
   * @return the fill percentage (0-100 or higher if overflowed)
   */
  public double getFillPercentage() {
    return (this.dailyFillingLiters / this.containerCapacityLiters) * 100.0;
  }

  /**
   * Calculates the fill percentage before any collection on the day.
   *
   * @return the fill percentage before collection (0-100 or higher if overflowed)
   */
  public double getFillPercentageBeforeCollection() {
    return (this.dailyFillingLitersBeforeCollection / this.containerCapacityLiters) * 100.0;
  }

  /**
   * Compares this daily state with another object by container and day.
   *
   * @param otherObject object to compare
   * @return true when both states belong to the same container and day
   */
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

  /**
   * Returns a hash code based on container identifier and day.
   *
   * @return hash code for this daily state
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.containerId, this.planDay);
  }

  /**
   * Returns a readable representation of this container daily state.
   *
   * @return text containing container, day, filling, capacity, demand, and status
   */
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
