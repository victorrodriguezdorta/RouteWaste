package com.ull.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import com.ull.domain.enumerate.StopType;

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
    this.totalDistanceMeters = 0.0;
    this.totalCollectedKilograms = 0.0;
    this.totalCollectedLiters = 0.0;
  }

  private void validatePlanDay(int planDay) {
    if (planDay < 1) {
      throw new IllegalArgumentException(PLAN_DAY_NOT_VALID);
    }
  }

  private void validateServiceDate(LocalDate serviceDate) {
    if (serviceDate == null) {
      throw new IllegalArgumentException(SERVICE_DATE_NOT_DEFINED);
    }
  }

  private void validateOriginFacility(Facility originFacility) {
    if (originFacility == null) {
      throw new IllegalArgumentException(FACILITY_NOT_DEFINED);
    }
  }

  private void validateVehicle(Vehicle vehicle) {
    if (vehicle == null) {
      throw new IllegalArgumentException(VEHICLE_NOT_DEFINED);
    }
  }

  private void validateContainer(Container container) {
    if (container == null) {
      throw new IllegalArgumentException(CONTAINER_NOT_DEFINED);
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

  public int getPlanDay() {
    return this.planDay;
  }

  public LocalDate getServiceDate() {
    return this.serviceDate;
  }

  public Facility getOriginFacility() {
    return this.originFacility;
  }

  public Vehicle getVehicle() {
    return this.vehicle;
  }

  public List<DailyPlanStop> getStops() {
    return Collections.unmodifiableList(this.stops);
  }

  public double getTotalDistanceMeters() {
    return this.totalDistanceMeters;
  }

  public double getTotalCollectedKilograms() {
    return this.totalCollectedKilograms;
  }

  public double getTotalCollectedLiters() {
    return this.totalCollectedLiters;
  }

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
    if (distanceFromPreviousMeters < 0.0) {
      throw new IllegalArgumentException(DISTANCE_NOT_VALID);
    }

    double cumulativeDistanceMeters = this.totalDistanceMeters + distanceFromPreviousMeters;
    int sequence = this.stops.size() + 1;

    DailyPlanStop stop = DailyPlanStop.forFacility(
        sequence,
        distanceFromPreviousMeters,
        cumulativeDistanceMeters,
        alerts != null ? alerts : new ArrayList<>());

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
    validateContainer(container);
    validateCollectedKilograms(collectedKilograms);
    validateCollectedLiters(collectedLiters);
    if (distanceFromPreviousMeters < 0.0) {
      throw new IllegalArgumentException(DISTANCE_NOT_VALID);
    }

    double cumulativeDistanceMeters = this.totalDistanceMeters + distanceFromPreviousMeters;
    int sequence = this.stops.size() + 1;

    DailyPlanStop stop = new DailyPlanStop(
      sequence,
      StopType.CONTAINER,
      container,
      distanceFromPreviousMeters,
      cumulativeDistanceMeters,
      collectedKilograms,
      collectedLiters,
      containerActualLiters,
      alerts);

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
    addStop(container, collectedKilograms, collectedLiters, 0.0, new ArrayList<>());
  }

  /**
   * Adds distance travelled without creating a stop entry.
   *
   * <p>This is used for deadhead movements such as returning to the facility to unload.
   *
   * @param distanceMeters distance to add to the route total
   */
  public void addTransitDistance(double distanceMeters) {
    if (distanceMeters < 0.0) {
      throw new IllegalArgumentException(DISTANCE_NOT_VALID);
    }
    this.totalDistanceMeters += distanceMeters;
  }

  /**
   * Removes all route stops and resets the accumulated metrics.
   */
  public void clearStops() {
    this.stops.clear();
    this.totalDistanceMeters = 0.0;
    this.totalCollectedKilograms = 0.0;
    this.totalCollectedLiters = 0.0;
  }

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

  @Override
  public int hashCode() {
    return Objects.hash(this.planDay, this.serviceDate, this.originFacility, this.vehicle);
  }

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
