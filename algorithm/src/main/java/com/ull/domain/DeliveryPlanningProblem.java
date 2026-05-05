package com.ull.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ull.domain.entity.Container;
import com.ull.domain.entity.FacilityWithVehicles;
import com.ull.domain.valueobject.cost.MaximumBudget;

/**
 * Represents the algorithm input problem.
 *
 * <p>Holds all typed domain objects built from the incoming JSON: the global
 * planning parameters, the facilities paired with their assigned vehicles, and
 * the set of containers that must be collected.
 */
public class DeliveryPlanningProblem {

  public static final String FACILITIES_NOT_DEFINED = "Facilities with vehicles list is not defined";
  public static final String CONTAINERS_NOT_DEFINED = "Selected containers list is not defined";
  public static final String AVERAGE_PICKUP_TIME_NOT_VALID = "Average pickup time in minutes must be >= 0";
  public static final String NUMBER_OF_DAYS_NOT_VALID = "Number of days must be >= 1";

  private final int averagePickupTimeMinutes;
  private final int numberOfDays;
  private final MaximumBudget maxBudget;
  private final List<FacilityWithVehicles> facilitiesWithVehicles;
  private final List<Container> containers;

  /**
   * Creates a fully initialised planning problem.
   *
   * @param averagePickupTimeMinutes average pickup time at each container stop, in minutes
   * @param numberOfDays             planning horizon in days
   * @param facilitiesWithVehicles   facilities paired with their assigned vehicles
   * @param containers               containers to be collected during the planning horizon
   */
  public DeliveryPlanningProblem(
      int averagePickupTimeMinutes,
      int numberOfDays,
      List<FacilityWithVehicles> facilitiesWithVehicles,
      List<Container> containers) {
    this(averagePickupTimeMinutes, numberOfDays, facilitiesWithVehicles, containers, null);
  }

  /**
   * Creates a fully initialised planning problem.
   *
   * @param averagePickupTimeMinutes average pickup time at each container stop, in minutes
   * @param numberOfDays             planning horizon in days
   * @param facilitiesWithVehicles   facilities paired with their assigned vehicles
   * @param containers               containers to be collected during the planning horizon
   * @param maxBudget                maximum budget for the planning horizon
   */
  public DeliveryPlanningProblem(
      int averagePickupTimeMinutes,
      int numberOfDays,
      List<FacilityWithVehicles> facilitiesWithVehicles,
      List<Container> containers,
      MaximumBudget maxBudget) {
    validateAveragePickupTime(averagePickupTimeMinutes);
    validateNumberOfDays(numberOfDays);
    validateFacilities(facilitiesWithVehicles);
    validateContainers(containers);
    this.averagePickupTimeMinutes = averagePickupTimeMinutes;
    this.numberOfDays = numberOfDays;
    this.maxBudget = maxBudget;
    this.facilitiesWithVehicles = new ArrayList<>(facilitiesWithVehicles);
    this.containers = new ArrayList<>(containers);
  }

  private void validateAveragePickupTime(int averagePickupTimeMinutes) {
    if (averagePickupTimeMinutes < 0) {
      throw new IllegalArgumentException(AVERAGE_PICKUP_TIME_NOT_VALID);
    }
  }

  private void validateNumberOfDays(int numberOfDays) {
    if (numberOfDays < 1) {
      throw new IllegalArgumentException(NUMBER_OF_DAYS_NOT_VALID);
    }
  }

  private void validateFacilities(List<FacilityWithVehicles> facilitiesWithVehicles) {
    if (facilitiesWithVehicles == null) {
      throw new IllegalArgumentException(FACILITIES_NOT_DEFINED);
    }
  }

  private void validateContainers(List<Container> containers) {
    if (containers == null) {
      throw new IllegalArgumentException(CONTAINERS_NOT_DEFINED);
    }
  }

  public int getAveragePickupTimeMinutes() {
    return this.averagePickupTimeMinutes;
  }

  public int getNumberOfDays() {
    return this.numberOfDays;
  }

  public MaximumBudget getMaxBudget() {
    return this.maxBudget;
  }

  /**
   * Returns an unmodifiable view of the facilities-with-vehicles list.
   *
   * @return the facilities with their assigned vehicles
   */
  public List<FacilityWithVehicles> getFacilitiesWithVehicles() {
    return Collections.unmodifiableList(this.facilitiesWithVehicles);
  }

  /**
   * Returns an unmodifiable view of the containers list.
   *
   * @return the containers to be collected
   */
  public List<Container> getContainers() {
    return Collections.unmodifiableList(this.containers);
  }

  /** Returns the total number of facilities in the problem. */
  public int getNumberOfFacilities() {
    return this.facilitiesWithVehicles.size();
  }

  /** Returns the total number of containers in the problem. */
  public int getNumberOfContainers() {
    return this.containers.size();
  }

  @Override
  public String toString() {
    return String.format(
        "DeliveryPlanningProblem{averagePickupTimeMinutes=%d, numberOfDays=%d,"
            + " facilities=%d, containers=%d}",
        this.averagePickupTimeMinutes,
        this.numberOfDays,
        this.facilitiesWithVehicles.size(),
        this.containers.size());
  }
}