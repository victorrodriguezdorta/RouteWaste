package com.ull.domain;

import com.ull.domain.entity.Container;
import com.ull.domain.entity.FacilityWithVehicles;
import com.ull.domain.valueobject.algorithm.GreedyWeights;
import com.ull.domain.valueobject.cost.MaximumBudget;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
  public static final String AVERAGE_TRANSFER_TIME_NOT_VALID = "Average transfer time in minutes must be >= 0";
  private static final int MIN_AVERAGE_PICKUP_TIME_MINUTES = 0;
  private static final int MIN_NUMBER_OF_DAYS = 1;
  private static final int MIN_AVERAGE_TRANSFER_TIME_MINUTES = 0;
  /**
   * Default collection day start time.
   *
   * <p>Applied when the request does not provide an explicit workday start.
   */
  private static final LocalTime DEFAULT_COLLECTION_START_TIME = LocalTime.of(8, 0);
  /**
   * Default average transfer time between route points.
   *
   * <p>Applied when the request does not provide a custom transfer duration.
   */
  private static final int DEFAULT_AVERAGE_TRANSFER_TIME_MINUTES = 0;

  private final int averagePickupTimeMinutes;
  private final int numberOfDays;
  private final MaximumBudget maxBudget;
  /**
   * Time of day when the collection journey starts.
   *
   * <p>Used as the initial clock value to schedule each vehicle's stops.
   */
  private final LocalTime collectionStartTime;
  /**
   * Average travelling time between route points.
   *
   * <p>Measured in minutes and applied to every simulated movement.
   */
  private final int averageTransferTimeMinutes;
  /**
   * Weights applied to the greedy container selection score.
   *
   * <p>Balances the distance penalty against the container fill reward.
   */
  private final GreedyWeights greedyWeights;
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
    this(
        averagePickupTimeMinutes,
        numberOfDays,
        facilitiesWithVehicles,
        containers,
        maxBudget,
        DEFAULT_COLLECTION_START_TIME,
        DEFAULT_AVERAGE_TRANSFER_TIME_MINUTES,
        GreedyWeights.defaultWeights());
  }

  /**
   * Creates a fully initialised planning problem including the scheduling parameters
   * and the greedy scoring weights provided by the client request.
   *
   * @param averagePickupTimeMinutes   average pickup time at each container stop, in minutes
   * @param numberOfDays               planning horizon in days
   * @param facilitiesWithVehicles     facilities paired with their assigned vehicles
   * @param containers                 containers to be collected during the planning horizon
   * @param maxBudget                  maximum budget for the planning horizon
   * @param collectionStartTime        time of day when the collection journey starts
   * @param averageTransferTimeMinutes average travelling time between points, in minutes
   * @param greedyWeights              weights applied to the greedy selection score
   */
  public DeliveryPlanningProblem(
      int averagePickupTimeMinutes,
      int numberOfDays,
      List<FacilityWithVehicles> facilitiesWithVehicles,
      List<Container> containers,
      MaximumBudget maxBudget,
      LocalTime collectionStartTime,
      int averageTransferTimeMinutes,
      GreedyWeights greedyWeights) {
    validateAveragePickupTime(averagePickupTimeMinutes);
    validateNumberOfDays(numberOfDays);
    validateFacilities(facilitiesWithVehicles);
    validateContainers(containers);
    validateAverageTransferTime(averageTransferTimeMinutes);
    this.averagePickupTimeMinutes = averagePickupTimeMinutes;
    this.numberOfDays = numberOfDays;
    this.maxBudget = maxBudget;
    this.collectionStartTime = collectionStartTime != null ? collectionStartTime : DEFAULT_COLLECTION_START_TIME;
    this.averageTransferTimeMinutes = averageTransferTimeMinutes;
    this.greedyWeights = greedyWeights != null ? greedyWeights : GreedyWeights.defaultWeights();
    this.facilitiesWithVehicles = new ArrayList<>(facilitiesWithVehicles);
    this.containers = new ArrayList<>(containers);
  }

  /**
   * Validates that average pickup time is not negative.
   *
   * @param averagePickupTimeMinutes average pickup time to validate
   */
  private void validateAveragePickupTime(int averagePickupTimeMinutes) {
    if (averagePickupTimeMinutes < MIN_AVERAGE_PICKUP_TIME_MINUTES) {
      throw new IllegalArgumentException(AVERAGE_PICKUP_TIME_NOT_VALID);
    }
  }

  /**
   * Validates that number of planning days is positive.
   *
   * @param numberOfDays number of days to validate
   */
  private void validateNumberOfDays(int numberOfDays) {
    if (numberOfDays < MIN_NUMBER_OF_DAYS) {
      throw new IllegalArgumentException(NUMBER_OF_DAYS_NOT_VALID);
    }
  }

  /**
   * Validates that average transfer time is not negative.
   *
   * @param averageTransferTimeMinutes average transfer time to validate
   */
  private void validateAverageTransferTime(int averageTransferTimeMinutes) {
    if (averageTransferTimeMinutes < MIN_AVERAGE_TRANSFER_TIME_MINUTES) {
      throw new IllegalArgumentException(AVERAGE_TRANSFER_TIME_NOT_VALID);
    }
  }

  /**
   * Validates that the facility assignments list exists.
   *
   * @param facilitiesWithVehicles facility assignments to validate
   */
  private void validateFacilities(List<FacilityWithVehicles> facilitiesWithVehicles) {
    if (facilitiesWithVehicles == null) {
      throw new IllegalArgumentException(FACILITIES_NOT_DEFINED);
    }
  }

  /**
   * Validates that the selected containers list exists.
   *
   * @param containers containers list to validate
   */
  private void validateContainers(List<Container> containers) {
    if (containers == null) {
      throw new IllegalArgumentException(CONTAINERS_NOT_DEFINED);
    }
  }

  /**
   * Returns the average pickup time in minutes.
   *
   * @return average pickup time in minutes
   */
  public int getAveragePickupTimeMinutes() {
    return this.averagePickupTimeMinutes;
  }

  /**
   * Returns the planning horizon length.
   *
   * @return number of planning days
   */
  public int getNumberOfDays() {
    return this.numberOfDays;
  }

  /**
   * Returns the maximum budget configured for the problem.
   *
   * @return maximum budget, or null when not provided
   */
  public MaximumBudget getMaxBudget() {
    return this.maxBudget;
  }

  /**
   * Returns the time of day when the collection journey starts.
   *
   * @return collection start time
   */
  public LocalTime getCollectionStartTime() {
    return this.collectionStartTime;
  }

  /**
   * Returns the average travelling time between points, in minutes.
   *
   * @return average transfer time in minutes
   */
  public int getAverageTransferTimeMinutes() {
    return this.averageTransferTimeMinutes;
  }

  /**
   * Returns the weights applied to the greedy container selection score.
   *
   * @return greedy scoring weights
   */
  public GreedyWeights getGreedyWeights() {
    return this.greedyWeights;
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

  /**
   * Returns the total number of facilities in the problem.
   *
   * @return number of facility assignments
   */
  public int getNumberOfFacilities() {
    return this.facilitiesWithVehicles.size();
  }

  /**
   * Returns the total number of containers in the problem.
   *
   * @return number of selected containers
   */
  public int getNumberOfContainers() {
    return this.containers.size();
  }

  /**
   * Returns a readable representation of this planning problem.
   *
   * @return text containing planning parameters and collection sizes
   */
  @Override
  public String toString() {
    return String.format(
        "DeliveryPlanningProblem{averagePickupTimeMinutes=%d, numberOfDays=%d,"
            + " collectionStartTime=%s, averageTransferTimeMinutes=%d, greedyWeights=%s,"
            + " facilities=%d, containers=%d}",
        this.averagePickupTimeMinutes,
        this.numberOfDays,
        this.collectionStartTime,
        this.averageTransferTimeMinutes,
        this.greedyWeights,
        this.facilitiesWithVehicles.size(),
        this.containers.size());
  }
}