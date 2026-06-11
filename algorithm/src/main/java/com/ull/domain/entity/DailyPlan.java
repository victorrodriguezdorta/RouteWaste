package com.ull.domain.entity;

import com.ull.domain.enumerate.StopType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the daily route assigned to a vehicle starting from a facility.
 */
public class DailyPlan {

  public static final String PLAN_DAY_NOT_VALID = "Plan day is not valid";
  public static final String SERVICE_DATE_NOT_DEFINED = "Service date is not defined";
  public static final String FACILITY_NOT_DEFINED = "Facility is not defined";
  public static final String VEHICLE_NOT_DEFINED = "Vehicle is not defined";
  public static final String CONTAINER_NOT_DEFINED = "Container is not defined";
  public static final String DISTANCE_NOT_VALID = "Distance from previous stop is not valid";
  public static final String COLLECTED_KILOGRAMS_NOT_VALID = "Collected kilograms is not valid";
  public static final String COLLECTED_LITERS_NOT_VALID = "Collected liters is not valid";
  private static final int MIN_PLAN_DAY = 1;
  private static final double ZERO_AMOUNT = 0.0;

  private final int planDay;
  private final LocalDate serviceDate;
  private final Facility originFacility;
  private final Vehicle vehicle;
  private final List<DailyPlanStop> stops;
  private double totalDistanceMeters;
  private double totalCollectedKilograms;
  private double totalCollectedLiters;

  /**
   * Creates a new daily plan for a facility, a vehicle, and a service date.
   *
   * @param planDay the day number inside the planning horizon
   * @param serviceDate the day of service
   * @param originFacility the facility where the route starts
   * @param vehicle the vehicle assigned to the route
   */
  public DailyPlan(int planDay, LocalDate serviceDate, Facility originFacility, Vehicle vehicle) {
    validatePlanDay(planDay);
    validateServiceDate(serviceDate);
    validateOriginFacility(originFacility);
    validateVehicle(vehicle);
    this.planDay = planDay;
    this.serviceDate = serviceDate;
    this.originFacility = originFacility;
    this.vehicle = vehicle;
    this.stops = new ArrayList<>();
    this.totalDistanceMeters = ZERO_AMOUNT;
    this.totalCollectedKilograms = ZERO_AMOUNT;
    this.totalCollectedLiters = ZERO_AMOUNT;
  }

  /**
   * Validates that the planning day is positive.
   *
   * @param planDay planning day to validate
   */
  private void validatePlanDay(int planDay) {
    if (planDay < MIN_PLAN_DAY) {
      throw new IllegalArgumentException(PLAN_DAY_NOT_VALID);
    }
  }

  /**
   * Validates that the service date is present.
   *
   * @param serviceDate service date to validate
   */
  private void validateServiceDate(LocalDate serviceDate) {
    if (serviceDate == null) {
      throw new IllegalArgumentException(SERVICE_DATE_NOT_DEFINED);
    }
  }

  /**
   * Validates that the origin facility is present.
   *
   * @param originFacility origin facility to validate
   */
  private void validateOriginFacility(Facility originFacility) {
    if (originFacility == null) {
      throw new IllegalArgumentException(FACILITY_NOT_DEFINED);
    }
  }

  /**
   * Validates that the route vehicle is present.
   *
   * @param vehicle vehicle to validate
   */
  private void validateVehicle(Vehicle vehicle) {
    if (vehicle == null) {
      throw new IllegalArgumentException(VEHICLE_NOT_DEFINED);
    }
  }

  /**
   * Validates that the stop container is present.
   *
   * @param container container to validate
   */
  private void validateContainer(Container container) {
    if (container == null) {
      throw new IllegalArgumentException(CONTAINER_NOT_DEFINED);
    }
  }

  /**
   * Validates that collected kilograms are not negative.
   *
   * @param collectedKilograms collected kilograms to validate
   */
  private void validateCollectedKilograms(double collectedKilograms) {
    if (collectedKilograms < ZERO_AMOUNT) {
      throw new IllegalArgumentException(COLLECTED_KILOGRAMS_NOT_VALID);
    }
  }

  /**
   * Validates that collected liters are not negative.
   *
   * @param collectedLiters collected liters to validate
   */
  private void validateCollectedLiters(double collectedLiters) {
    if (collectedLiters < ZERO_AMOUNT) {
      throw new IllegalArgumentException(COLLECTED_LITERS_NOT_VALID);
    }
  }

  /**
   * Returns the planning day.
   *
   * @return one-based planning day
   */
  public int getPlanDay() {
    return this.planDay;
  }

  /**
   * Returns the service date.
   *
   * @return service date
   */
  public LocalDate getServiceDate() {
    return this.serviceDate;
  }

  /**
   * Returns the origin facility.
   *
   * @return origin facility
   */
  public Facility getOriginFacility() {
    return this.originFacility;
  }

  /**
   * Returns the assigned vehicle.
   *
   * @return assigned vehicle
   */
  public Vehicle getVehicle() {
    return this.vehicle;
  }

  /**
   * Returns the route stops.
   *
   * @return unmodifiable stops list
   */
  public List<DailyPlanStop> getStops() {
    return Collections.unmodifiableList(this.stops);
  }

  /**
   * Returns the total travelled distance in meters.
   *
   * @return total distance in meters
   */
  public double getTotalDistanceMeters() {
    return this.totalDistanceMeters;
  }

  /**
   * Returns total collected kilograms.
   *
   * @return total collected kilograms
   */
  public double getTotalCollectedKilograms() {
    return this.totalCollectedKilograms;
  }

  /**
   * Returns total collected liters.
   *
   * @return total collected liters
   */
  public double getTotalCollectedLiters() {
    return this.totalCollectedLiters;
  }

  /**
   * Returns the last container stop in the route.
   *
   * @return last visited container, or null when no container stop exists
   */
  public Container getLastContainer() {
    if (this.stops.isEmpty()) {
      return null;
    }
    return this.stops.get(this.stops.size() - 1).getContainer();
  }

  /**
   * Adds a facility stop (no container) to the daily plan and updates totals.
   * This represents returning to the origin facility to unload.
   *
   * @param distanceFromPreviousMeters distance travelled to reach the facility
   * @param alerts optional alerts for the stop
   */
  public void addFacilityStop(double distanceFromPreviousMeters, List<Alert> alerts) {
    addFacilityStop(distanceFromPreviousMeters, alerts, null);
  }

  /**
   * Adds a facility stop (no container) to the daily plan including the time of the visit.
   *
   * @param distanceFromPreviousMeters distance travelled to reach the facility
   * @param alerts optional alerts for the stop
   * @param collectedAt time of day at which the facility stop is performed
   */
  public void addFacilityStop(double distanceFromPreviousMeters, List<Alert> alerts, LocalTime collectedAt) {
    if (distanceFromPreviousMeters < ZERO_AMOUNT) {
      throw new IllegalArgumentException(DISTANCE_NOT_VALID);
    }
    double cumulativeDistanceMeters = this.totalDistanceMeters + distanceFromPreviousMeters;
    int sequence = this.stops.size() + MIN_PLAN_DAY;
    DailyPlanStop stop = DailyPlanStop.forFacility(
        sequence,
        distanceFromPreviousMeters,
        cumulativeDistanceMeters,
        alerts != null ? alerts : new ArrayList<>(),
        collectedAt);
    this.stops.add(stop);
    this.totalDistanceMeters = cumulativeDistanceMeters;
  }

  /**
   * Adds a new stop to the daily plan and updates the route totals.
   *
   * @param container the visited container
   * @param collectedKilograms kilograms collected at the stop
   * @param collectedLiters liters collected at the stop
   * @param containerActualLiters the actual liters in the container before collection
   * @param alerts list of alerts generated at this stop
   */
  public void addStop(
      Container container,
      double collectedKilograms,
      double collectedLiters,
      double containerActualLiters,
      List<Alert> alerts) {
    validateContainer(container);
    validateCollectedKilograms(collectedKilograms);
    validateCollectedLiters(collectedLiters);
    double distanceFromPreviousMeters;
    if (this.stops.isEmpty()) {
      distanceFromPreviousMeters = this.originFacility.calculateDistanceTo(container);
    } else {
      Container lastContainer = this.stops.get(this.stops.size() - 1).getContainer();
      distanceFromPreviousMeters = lastContainer != null
          ? lastContainer.calculateDistanceTo(container)
          : this.originFacility.calculateDistanceTo(container);
    }
    addStop(
        container,
        collectedKilograms,
        collectedLiters,
        containerActualLiters,
        distanceFromPreviousMeters,
        alerts);
  }

  /**
   * Adds a new stop using an explicitly supplied distance from the previous operational point.
   *
   * @param container the visited container
   * @param collectedKilograms kilograms collected at the stop
   * @param collectedLiters liters collected at the stop
   * @param containerActualLiters the actual liters in the container before collection
   * @param distanceFromPreviousMeters distance travelled to reach the stop
   * @param alerts list of alerts generated at this stop
   */
  public void addStop(
      Container container,
      double collectedKilograms,
      double collectedLiters,
      double containerActualLiters,
      double distanceFromPreviousMeters,
      List<Alert> alerts) {
    addStop(
        container,
        collectedKilograms,
        collectedLiters,
        containerActualLiters,
        distanceFromPreviousMeters,
        alerts,
        null);
  }

  /**
   * Adds a new stop using an explicitly supplied distance and the time of the visit.
   *
   * @param container the visited container
   * @param collectedKilograms kilograms collected at the stop
   * @param collectedLiters liters collected at the stop
   * @param containerActualLiters the actual liters in the container before collection
   * @param distanceFromPreviousMeters distance travelled to reach the stop
   * @param alerts list of alerts generated at this stop
   * @param collectedAt time of day at which the container is collected
   */
  public void addStop(
      Container container,
      double collectedKilograms,
      double collectedLiters,
      double containerActualLiters,
      double distanceFromPreviousMeters,
      List<Alert> alerts,
      LocalTime collectedAt) {
    validateContainer(container);
    validateCollectedKilograms(collectedKilograms);
    validateCollectedLiters(collectedLiters);
    if (distanceFromPreviousMeters < ZERO_AMOUNT) {
      throw new IllegalArgumentException(DISTANCE_NOT_VALID);
    }
    double cumulativeDistanceMeters = this.totalDistanceMeters + distanceFromPreviousMeters;
    int sequence = this.stops.size() + MIN_PLAN_DAY;
    DailyPlanStop stop = new DailyPlanStop(
      sequence,
      StopType.CONTAINER,
      container,
      distanceFromPreviousMeters,
      cumulativeDistanceMeters,
      collectedKilograms,
      collectedLiters,
      containerActualLiters,
      alerts,
      collectedAt);
    this.stops.add(stop);
    this.totalDistanceMeters = cumulativeDistanceMeters;
    this.totalCollectedKilograms += collectedKilograms;
    this.totalCollectedLiters += collectedLiters;
  }

  /**
   * Adds a new stop to the daily plan and updates the route totals.
   * Legacy method for backward compatibility (no alerts, no container actual liters).
   *
   * @param container the visited container
   * @param collectedKilograms kilograms collected at the stop
   * @param collectedLiters liters collected at the stop
   */
  public void addStop(Container container, double collectedKilograms, double collectedLiters) {
    addStop(container, collectedKilograms, collectedLiters, ZERO_AMOUNT, new ArrayList<>());
  }

  /**
   * Adds distance travelled without creating a stop entry.
   *
   * <p>This is used for deadhead movements such as returning to the facility to unload.
   *
   * @param distanceMeters distance to add to the route total
   */
  public void addTransitDistance(double distanceMeters) {
    if (distanceMeters < ZERO_AMOUNT) {
      throw new IllegalArgumentException(DISTANCE_NOT_VALID);
    }
    this.totalDistanceMeters += distanceMeters;
  }

  /**
   * Removes all route stops and resets the accumulated metrics.
   */
  public void clearStops() {
    this.stops.clear();
    this.totalDistanceMeters = ZERO_AMOUNT;
    this.totalCollectedKilograms = ZERO_AMOUNT;
    this.totalCollectedLiters = ZERO_AMOUNT;
  }

  /**
   * Compares this daily plan with another object by route identity.
   *
   * @param otherObject object to compare
   * @return true when both plans share day, date, facility, and vehicle
   */
  @Override
  public boolean equals(Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }
    DailyPlan otherPlan = (DailyPlan) otherObject;
    return this.planDay == otherPlan.planDay
        && Objects.equals(this.serviceDate, otherPlan.serviceDate)
        && Objects.equals(this.originFacility, otherPlan.originFacility)
        && Objects.equals(this.vehicle, otherPlan.vehicle);
  }

  /**
   * Returns a hash code based on route identity.
   *
   * @return hash code for this daily plan
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.planDay, this.serviceDate, this.originFacility, this.vehicle);
  }

  /**
   * Returns a readable representation of this daily plan.
   *
   * @return text containing route attributes, stops, and totals
   */
  @Override
  public String toString() {
    return String.format(
        "DailyPlan{planDay=%s, serviceDate=%s, originFacility=%s, vehicle=%s, stops=%s, totalDistanceMeters=%s, totalCollectedKilograms=%s, totalCollectedLiters=%s}",
        this.planDay,
        this.serviceDate,
        this.originFacility,
        this.vehicle,
        this.stops,
        this.totalDistanceMeters,
        this.totalCollectedKilograms,
        this.totalCollectedLiters);
  }
}
