package com.ull.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a stop inside a daily collection plan.
 */
public class DailyPlanStop {

  public static final String CONTAINER_NOT_DEFINED = "Container is not defined";
  public static final String SEQUENCE_NOT_VALID = "Stop sequence is not valid";
  public static final String DISTANCE_NOT_VALID = "Distance from previous stop is not valid";
  public static final String CUMULATIVE_DISTANCE_NOT_VALID = "Cumulative distance is not valid";
  public static final String COLLECTED_KILOGRAMS_NOT_VALID = "Collected kilograms is not valid";
  public static final String COLLECTED_LITERS_NOT_VALID = "Collected liters is not valid";
  public static final String CONTAINER_ACTUAL_LITERS_NOT_VALID = "Container actual liters is not valid";

  private final int sequence;
  private final Container container;
  private final double distanceFromPreviousMeters;
  private final double cumulativeDistanceMeters;
  private final double collectedKilograms;
  private final double collectedLiters;
  private final double containerActualLiters;
  private final List<Alert> alerts;

  /**
   * Creates a stop with its route and collection metrics.
   *
   * @param sequence stop sequence inside the plan
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
      Container container,
      double distanceFromPreviousMeters,
      double cumulativeDistanceMeters,
      double collectedKilograms,
      double collectedLiters,
      double containerActualLiters,
      List<Alert> alerts) {
    validateSequence(sequence);
    validateContainer(container);
    validateDistance(distanceFromPreviousMeters);
    validateCumulativeDistance(cumulativeDistanceMeters);
    validateCollectedKilograms(collectedKilograms);
    validateCollectedLiters(collectedLiters);
    validateContainerActualLiters(containerActualLiters);
    this.sequence = sequence;
    this.container = container;
    this.distanceFromPreviousMeters = distanceFromPreviousMeters;
    this.cumulativeDistanceMeters = cumulativeDistanceMeters;
    this.collectedKilograms = collectedKilograms;
    this.collectedLiters = collectedLiters;
    this.containerActualLiters = containerActualLiters;
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
        container,
        distanceFromPreviousMeters,
        cumulativeDistanceMeters,
        collectedKilograms,
        collectedLiters,
        0.0,
        new ArrayList<>());
  }

  private void validateSequence(int sequence) {
    if (sequence < 1) {
      throw new IllegalArgumentException(SEQUENCE_NOT_VALID);
    }
  }

  private void validateContainer(Container container) {
    if (container == null) {
      throw new IllegalArgumentException(CONTAINER_NOT_DEFINED);
    }
  }

  private void validateDistance(double distanceFromPreviousMeters) {
    if (distanceFromPreviousMeters < 0) {
      throw new IllegalArgumentException(DISTANCE_NOT_VALID);
    }
  }

  private void validateCumulativeDistance(double cumulativeDistanceMeters) {
    if (cumulativeDistanceMeters < 0) {
      throw new IllegalArgumentException(CUMULATIVE_DISTANCE_NOT_VALID);
    }
  }

  private void validateCollectedKilograms(double collectedKilograms) {
    if (collectedKilograms < 0) {
      throw new IllegalArgumentException(COLLECTED_KILOGRAMS_NOT_VALID);
    }
  }

  private void validateCollectedLiters(double collectedLiters) {
    if (collectedLiters < 0) {
      throw new IllegalArgumentException(COLLECTED_LITERS_NOT_VALID);
    }
  }

  private void validateContainerActualLiters(double containerActualLiters) {
    if (containerActualLiters < 0) {
      throw new IllegalArgumentException(CONTAINER_ACTUAL_LITERS_NOT_VALID);
    }
  }

  public int getSequence() {
    return this.sequence;
  }

  public Container getContainer() {
    return this.container;
  }

  public double getDistanceFromPreviousMeters() {
    return this.distanceFromPreviousMeters;
  }

  public double getCumulativeDistanceMeters() {
    return this.cumulativeDistanceMeters;
  }

  public double getCollectedKilograms() {
    return this.collectedKilograms;
  }

  public double getCollectedLiters() {
    return this.collectedLiters;
  }

  public double getContainerActualLiters() {
    return this.containerActualLiters;
  }

  public List<Alert> getAlerts() {
    return Collections.unmodifiableList(this.alerts);
  }

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
        && Objects.equals(this.container, otherStop.container);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.sequence, this.container);
  }

  @Override
  public String toString() {
    return String.format(
        "DailyPlanStop{sequence=%s, container=%s, distanceFromPreviousMeters=%s, cumulativeDistanceMeters=%s, collectedKilograms=%s, collectedLiters=%s, containerActualLiters=%s, alerts=%s}",
        this.sequence,
        this.container,
        this.distanceFromPreviousMeters,
        this.cumulativeDistanceMeters,
        this.collectedKilograms,
        this.collectedLiters,
        this.containerActualLiters,
        this.alerts);
  }
}
