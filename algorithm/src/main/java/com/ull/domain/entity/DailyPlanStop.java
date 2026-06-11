package com.ull.domain.entity;

import com.ull.domain.enumerate.StopType;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a stop inside a daily collection plan.
 */
public class DailyPlanStop {
  public static final int MIN_SEQUENCE = 1;
  public static final double ZERO_AMOUNT = 0.0;
  public static final String CONTAINER_NOT_DEFINED = "Container is not defined";
  public static final String SEQUENCE_NOT_VALID = "Stop sequence is not valid";
  public static final String DISTANCE_NOT_VALID = "Distance from previous stop is not valid";
  public static final String CUMULATIVE_DISTANCE_NOT_VALID = "Cumulative distance is not valid";
  public static final String COLLECTED_KILOGRAMS_NOT_VALID = "Collected kilograms is not valid";
  public static final String COLLECTED_LITERS_NOT_VALID = "Collected liters is not valid";
  public static final String CONTAINER_ACTUAL_LITERS_NOT_VALID = "Container actual liters is not valid";

  private final int sequence;
  private final Container container;
  private final StopType type;
  private final double distanceFromPreviousMeters;
  private final double cumulativeDistanceMeters;
  private final double collectedKilograms;
  private final double collectedLiters;
  private final double containerActualLiters;
  /** Time of day at which the vehicle performs this stop (null when not computed). */
  private final LocalTime collectedAt;
  private final List<Alert> alerts;

  /**
   * Creates a stop with its route and collection metrics.
   *
   * @param sequence stop sequence inside the plan
   * @param type kind of stop represented in the daily plan
   * @param container visited container
   * @param distanceFromPreviousMeters distance from the previous point in meters
   * @param cumulativeDistanceMeters cumulative route distance in meters
   * @param collectedKilograms kilograms collected at this stop
   * @param collectedLiters liters collected at this stop
   * @param containerActualLiters the actual liters in the container before collection
   * @param alerts list of alerts generated at this stop
   */
  public DailyPlanStop(
      int sequence,
      StopType type,
      Container container,
      double distanceFromPreviousMeters,
      double cumulativeDistanceMeters,
      double collectedKilograms,
      double collectedLiters,
      double containerActualLiters,
      List<Alert> alerts) {
    this(
        sequence,
        type,
        container,
        distanceFromPreviousMeters,
        cumulativeDistanceMeters,
        collectedKilograms,
        collectedLiters,
        containerActualLiters,
        alerts,
        null);
  }

  /**
   * Creates a stop with its route and collection metrics including the scheduled time.
   *
   * @param sequence stop sequence inside the plan
   * @param type kind of stop represented in the daily plan
   * @param container visited container
   * @param distanceFromPreviousMeters distance from the previous point in meters
   * @param cumulativeDistanceMeters cumulative route distance in meters
   * @param collectedKilograms kilograms collected at this stop
   * @param collectedLiters liters collected at this stop
   * @param containerActualLiters the actual liters in the container before collection
   * @param alerts list of alerts generated at this stop
   * @param collectedAt time of day at which the stop is performed
   */
  public DailyPlanStop(
      int sequence,
      StopType type,
      Container container,
      double distanceFromPreviousMeters,
      double cumulativeDistanceMeters,
      double collectedKilograms,
      double collectedLiters,
      double containerActualLiters,
      List<Alert> alerts,
      LocalTime collectedAt) {
    validateSequence(sequence);
    if (type == StopType.CONTAINER) {
      validateContainer(container);
    }
    validateDistance(distanceFromPreviousMeters);
    validateCumulativeDistance(cumulativeDistanceMeters);
    validateCollectedKilograms(collectedKilograms);
    validateCollectedLiters(collectedLiters);
    validateContainerActualLiters(containerActualLiters);
    this.sequence = sequence;
    this.type = type;
    this.container = container;
    this.distanceFromPreviousMeters = distanceFromPreviousMeters;
    this.cumulativeDistanceMeters = cumulativeDistanceMeters;
    this.collectedKilograms = collectedKilograms;
    this.collectedLiters = collectedLiters;
    this.containerActualLiters = containerActualLiters;
    this.collectedAt = collectedAt;
    this.alerts = alerts != null ? new ArrayList<>(alerts) : new ArrayList<>();
  }

  /**
   * Legacy constructor for backward compatibility (no alerts, no container actual liters).
   *
   * @param sequence stop sequence inside the plan
   * @param container visited container
   * @param distanceFromPreviousMeters distance from the previous point in meters
   * @param cumulativeDistanceMeters cumulative route distance in meters
   * @param collectedKilograms kilograms collected at this stop
   * @param collectedLiters liters collected at this stop
   */
  public DailyPlanStop(
      int sequence,
      Container container,
      double distanceFromPreviousMeters,
      double cumulativeDistanceMeters,
      double collectedKilograms,
      double collectedLiters) {
    this(
        sequence,
        StopType.CONTAINER,
        container,
        distanceFromPreviousMeters,
        cumulativeDistanceMeters,
        collectedKilograms,
        collectedLiters,
        ZERO_AMOUNT,
        new ArrayList<>());
  }

  /**
   * Factory to create a facility stop (no container).
   *
   * @param sequence stop sequence inside the plan
   * @param distanceFromPreviousMeters distance from the previous point in meters
   * @param cumulativeDistanceMeters cumulative route distance in meters
   * @param alerts list of alerts generated at the facility stop
   * @return a daily plan stop representing a visit to the facility
   */
  public static DailyPlanStop forFacility(
      int sequence,
      double distanceFromPreviousMeters,
      double cumulativeDistanceMeters,
      List<Alert> alerts) {
    return forFacility(sequence, distanceFromPreviousMeters, cumulativeDistanceMeters, alerts, null);
  }

  /**
   * Factory to create a facility stop (no container) with a scheduled time.
   *
   * @param sequence stop sequence inside the plan
   * @param distanceFromPreviousMeters distance from the previous point in meters
   * @param cumulativeDistanceMeters cumulative route distance in meters
   * @param alerts list of alerts generated at the facility stop
   * @param collectedAt time of day at which the facility stop is performed
   * @return a daily plan stop representing a visit to the facility
   */
  public static DailyPlanStop forFacility(
      int sequence,
      double distanceFromPreviousMeters,
      double cumulativeDistanceMeters,
      List<Alert> alerts,
      LocalTime collectedAt) {
    return new DailyPlanStop(
        sequence,
        StopType.FACILITY,
        null,
        distanceFromPreviousMeters,
        cumulativeDistanceMeters,
        ZERO_AMOUNT,
        ZERO_AMOUNT,
        ZERO_AMOUNT,
        alerts != null ? alerts : new ArrayList<>(),
        collectedAt);
  }

  /**
   * Validates that the stop sequence is inside the accepted route range.
   *
   * @param sequence stop sequence to validate
   */
  private void validateSequence(int sequence) {
    if (sequence < MIN_SEQUENCE) {
      throw new IllegalArgumentException(SEQUENCE_NOT_VALID);
    }
  }

  /**
   * Validates that a container stop has an associated container.
   *
   * @param container container to validate
   */
  private void validateContainer(Container container) {
    if (container == null) {
      throw new IllegalArgumentException(CONTAINER_NOT_DEFINED);
    }
  }

  /**
   * Validates that the distance from the previous stop is non-negative.
   *
   * @param distanceFromPreviousMeters distance from the previous stop in meters
   */
  private void validateDistance(double distanceFromPreviousMeters) {
    if (distanceFromPreviousMeters < ZERO_AMOUNT) {
      throw new IllegalArgumentException(DISTANCE_NOT_VALID);
    }
  }

  /**
   * Validates that the accumulated route distance is non-negative.
   *
   * @param cumulativeDistanceMeters accumulated route distance in meters
   */
  private void validateCumulativeDistance(double cumulativeDistanceMeters) {
    if (cumulativeDistanceMeters < ZERO_AMOUNT) {
      throw new IllegalArgumentException(CUMULATIVE_DISTANCE_NOT_VALID);
    }
  }

  /**
   * Validates that collected kilograms is non-negative.
   *
   * @param collectedKilograms collected kilograms to validate
   */
  private void validateCollectedKilograms(double collectedKilograms) {
    if (collectedKilograms < ZERO_AMOUNT) {
      throw new IllegalArgumentException(COLLECTED_KILOGRAMS_NOT_VALID);
    }
  }

  /**
   * Validates that collected liters is non-negative.
   *
   * @param collectedLiters collected liters to validate
   */
  private void validateCollectedLiters(double collectedLiters) {
    if (collectedLiters < ZERO_AMOUNT) {
      throw new IllegalArgumentException(COLLECTED_LITERS_NOT_VALID);
    }
  }

  /**
   * Validates that the container actual liters value is non-negative.
   *
   * @param containerActualLiters actual liters to validate
   */
  private void validateContainerActualLiters(double containerActualLiters) {
    if (containerActualLiters < ZERO_AMOUNT) {
      throw new IllegalArgumentException(CONTAINER_ACTUAL_LITERS_NOT_VALID);
    }
  }

  /**
   * Gets the route sequence of the stop.
   *
   * @return route sequence of the stop
   */
  public int getSequence() {
    return this.sequence;
  }

  /**
   * Gets the visited container.
   *
   * @return visited container, or {@code null} for facility stops
   */
  public Container getContainer() {
    return this.container;
  }

  /**
   * Gets the type of stop.
   *
   * @return stop type
   */
  public StopType getType() {
    return this.type;
  }

  /**
   * Gets the distance from the previous route point.
   *
   * @return distance from the previous route point in meters
   */
  public double getDistanceFromPreviousMeters() {
    return this.distanceFromPreviousMeters;
  }

  /**
   * Gets the accumulated distance at this stop.
   *
   * @return accumulated distance in meters
   */
  public double getCumulativeDistanceMeters() {
    return this.cumulativeDistanceMeters;
  }

  /**
   * Gets the kilograms collected at this stop.
   *
   * @return kilograms collected at this stop
   */
  public double getCollectedKilograms() {
    return this.collectedKilograms;
  }

  /**
   * Gets the liters collected at this stop.
   *
   * @return liters collected at this stop
   */
  public double getCollectedLiters() {
    return this.collectedLiters;
  }

  /**
   * Gets the actual liters in the container before collection.
   *
   * @return actual liters in the container before collection
   */
  public double getContainerActualLiters() {
    return this.containerActualLiters;
  }

  /**
   * Gets the time of day at which the vehicle performs this stop.
   *
   * @return scheduled stop time, or {@code null} when it was not computed
   */
  public LocalTime getCollectedAt() {
    return this.collectedAt;
  }

  /**
   * Gets the alerts generated at this stop.
   *
   * @return unmodifiable list of alerts generated at this stop
   */
  public List<Alert> getAlerts() {
    return Collections.unmodifiableList(this.alerts);
  }

  /**
   * Compares this stop with another object by sequence, type, and container.
   *
   * @param otherObject object to compare with this stop
   * @return true when both objects represent the same daily plan stop
   */
  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }
    DailyPlanStop otherStop = (DailyPlanStop) otherObject;
    return this.sequence == otherStop.sequence
      && this.type == otherStop.type
      && Objects.equals(this.container, otherStop.container);
  }

  /**
   * Calculates the hash code from the identifying stop fields.
   *
   * @return hash code for this stop
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.sequence, this.type, this.container);
  }

  /**
   * Gets a text representation of the daily plan stop.
   *
   * @return text representation of the daily plan stop
   */
  @Override
  public String toString() {
    return String.format(
        "DailyPlanStop{sequence=%s, type=%s, container=%s, distanceFromPreviousMeters=%s, cumulativeDistanceMeters=%s, collectedKilograms=%s, collectedLiters=%s, containerActualLiters=%s, alerts=%s}",
        this.sequence,
        this.type,
        this.container,
        this.distanceFromPreviousMeters,
        this.cumulativeDistanceMeters,
        this.collectedKilograms,
        this.collectedLiters,
        this.containerActualLiters,
        this.alerts);
  }
}
