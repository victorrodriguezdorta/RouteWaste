package es.ull.project.domain.entity;

import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.time.PlanDay;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * DailyPlan
 *
 * Aggregate Root representing the daily routing operation assigned to a vehicle
 * starting from a facility.
 */
public class DailyPlan {

    public static final String INFRASTRUCTURE_PLAN_NOT_DEFINED = "Infrastructure plan is not defined";
    public static final String FACILITY_NOT_DEFINED = "Facility is not defined";
    public static final String SERVICE_DATE_NOT_DEFINED = "Service date is not defined";
    public static final String VEHICLE_NOT_DEFINED = "Vehicle is not defined";
    public static final String TOTAL_KILOGRAMS_NOT_DEFINED = "Total collected kilograms is not defined";
    public static final String TOTAL_LITERS_NOT_DEFINED = "Total collected liters is not defined";
    public static final String TOTAL_DISTANCE_NOT_DEFINED = "Total distance is not defined";
    public static final String INVALID_STOP = "Stop is invalid";

    /**
     * Unique identifier for the daily plan.
     * It is a computed attribute.
     */
    private final UUID id;

    /**
     * The parent infrastructure plan.
     * It is a required attribute.
     */
    private final InfrastructurePlan infrastructurePlan;

    /**
     * The facility where the route starts and ends.
     * It is a required attribute.
     */
    private final Facility facility;

    /**
     * The date when the service is executed.
     * It is a required attribute.
     */
    private final LocalDate serviceDate;

    /**
     * The planning day within the execution horizon.
     * It is an optional attribute.
     */
    private final PlanDay planDay;

    /**
     * The vehicle assigned to the route.
     * It is a required attribute.
     */
    private final Vehicle vehicle;

    /**
     * Total weight collected.
     * It is a computed attribute.
     */
    private final CollectedWeightKilograms totalCollectedKilograms;

    /**
     * Total volume collected.
     * It is a computed attribute.
     */
    private final CollectedVolumeLiters totalCollectedLiters;

    /**
     * Total distance of the route.
     * It is a computed attribute.
     */
    private final Distance totalDistanceMeters;

    /**
     * List of stops in the route.
     * It is a computed attribute.
     */
    private final List<Stop> stops;

    /**
     * Creates a new DailyPlan.
     *
     * @param infrastructurePlan      The parent infrastructure plan.
     * @param facility                The facility where the route starts/ends.
     * @param serviceDate             The date when the service is executed.
    * @param planDay                 The planning day within the execution horizon.
     * @param vehicle                 The vehicle assigned to the route.
     * @param totalCollectedKilograms Total weight collected.
     * @param totalCollectedLiters    Total volume collected.
     * @param totalDistanceMeters     Total distance of the route.
     * @param stops                   List of stops in the route.
     */
    public DailyPlan(InfrastructurePlan infrastructurePlan,
                     Facility facility,
                     LocalDate serviceDate,
                     PlanDay planDay,
                     Vehicle vehicle,
                     CollectedWeightKilograms totalCollectedKilograms,
                     CollectedVolumeLiters totalCollectedLiters,
                     Distance totalDistanceMeters,
                     List<Stop> stops) {
        validate(infrastructurePlan, facility, serviceDate, vehicle, totalCollectedKilograms, totalCollectedLiters, totalDistanceMeters);
        this.id = UUID.randomUUID();
        this.infrastructurePlan = infrastructurePlan;
        this.facility = facility;
        this.serviceDate = serviceDate;
        this.planDay = planDay;
        this.vehicle = vehicle;
        this.totalCollectedKilograms = totalCollectedKilograms;
        this.totalCollectedLiters = totalCollectedLiters;
        this.totalDistanceMeters = totalDistanceMeters;
        this.stops = stops != null ? new ArrayList<>(stops) : new ArrayList<>();
    }

    /**
     * Copy constructor.
     * Creates a new DailyPlan as a copy of another DailyPlan.
     *
     * @param otherObject the DailyPlan to copy
     */
    public DailyPlan(DailyPlan otherObject) {
        this.id = otherObject.id;
        this.infrastructurePlan = otherObject.infrastructurePlan;
        this.facility = otherObject.facility;
        this.serviceDate = otherObject.serviceDate;
        this.planDay = otherObject.planDay;
        this.vehicle = otherObject.vehicle;
        this.totalCollectedKilograms = otherObject.totalCollectedKilograms;
        this.totalCollectedLiters = otherObject.totalCollectedLiters;
        this.totalDistanceMeters = otherObject.totalDistanceMeters;
        this.stops = new ArrayList<>(otherObject.stops);
    }

    /**
     * Restore constructor.
     * Restores a DailyPlan from persistence with all its attributes.
     *
     * @param id                      the unique identifier of the daily plan
     * @param infrastructurePlan      the parent infrastructure plan
     * @param facility                the facility where the route starts/ends
     * @param serviceDate             the date when the service is executed
     * @param planDay                 the planning day within the execution horizon
     * @param vehicle                 the vehicle assigned to the route
     * @param totalCollectedKilograms total weight collected
     * @param totalCollectedLiters    total volume collected
     * @param totalDistanceMeters     total distance of the route
     * @param stops                   list of stops in the route
     */
    public DailyPlan(UUID id,
                     InfrastructurePlan infrastructurePlan,
                     Facility facility,
                     LocalDate serviceDate,
                     PlanDay planDay,
                     Vehicle vehicle,
                     CollectedWeightKilograms totalCollectedKilograms,
                     CollectedVolumeLiters totalCollectedLiters,
                     Distance totalDistanceMeters,
                     List<Stop> stops) {
        validate(infrastructurePlan, facility, serviceDate, vehicle, totalCollectedKilograms, totalCollectedLiters, totalDistanceMeters);
        this.id = id;
        this.infrastructurePlan = infrastructurePlan;
        this.facility = facility;
        this.serviceDate = serviceDate;
        this.planDay = planDay;
        this.vehicle = vehicle;
        this.totalCollectedKilograms = totalCollectedKilograms;
        this.totalCollectedLiters = totalCollectedLiters;
        this.totalDistanceMeters = totalDistanceMeters;
        this.stops = stops != null ? new ArrayList<>(stops) : new ArrayList<>();
    }

    /**
     * Validates that all required parameters are non-null.
     *
     * @param infrastructurePlan      the parent infrastructure plan
     * @param facility                the facility where the route starts/ends
     * @param serviceDate             the service date
     * @param vehicle                 the assigned vehicle
     * @param totalCollectedKilograms total weight collected
     * @param totalCollectedLiters    total volume collected
     * @param totalDistanceMeters     total route distance
     * @throws IllegalArgumentException if any required parameter is null
     */
    private void validate(InfrastructurePlan infrastructurePlan, Facility facility, LocalDate serviceDate, Vehicle vehicle,
                          CollectedWeightKilograms totalCollectedKilograms, CollectedVolumeLiters totalCollectedLiters,
                          Distance totalDistanceMeters) {
        if (infrastructurePlan == null) {
            throw new IllegalArgumentException(INFRASTRUCTURE_PLAN_NOT_DEFINED);
        }
        if (facility == null) {
            throw new IllegalArgumentException(FACILITY_NOT_DEFINED);
        }
        if (serviceDate == null) {
            throw new IllegalArgumentException(SERVICE_DATE_NOT_DEFINED);
        }
        if (vehicle == null) {
            throw new IllegalArgumentException(VEHICLE_NOT_DEFINED);
        }
        if (totalCollectedKilograms == null) {
            throw new IllegalArgumentException(TOTAL_KILOGRAMS_NOT_DEFINED);
        }
        if (totalCollectedLiters == null) {
            throw new IllegalArgumentException(TOTAL_LITERS_NOT_DEFINED);
        }
        if (totalDistanceMeters == null) {
            throw new IllegalArgumentException(TOTAL_DISTANCE_NOT_DEFINED);
        }
    }

    /**
     * Adds a stop to this daily plan's route.
     *
     * @param stop the stop to add
     * @throws IllegalArgumentException if stop is null
     */
    public void addStop(Stop stop) {
        if (stop == null) {
            throw new IllegalArgumentException(INVALID_STOP);
        }
        this.stops.add(stop);
    }

    /**
     * Returns the unique identifier of this daily plan.
     *
     * @return the daily plan UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the parent infrastructure plan.
     *
     * @return the infrastructure plan
     */
    public InfrastructurePlan getInfrastructurePlan() {
        return infrastructurePlan;
    }

    /**
     * Returns the facility where this route starts and ends.
     *
     * @return the facility
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * Returns the date when the service is executed.
     *
     * @return the service date
     */
    public LocalDate getServiceDate() {
        return serviceDate;
    }

    /**
     * Returns the planning day within the execution horizon.
     *
     * @return the plan day number
     */
    public PlanDay getPlanDay() {
        return planDay;
    }

    /**
     * Returns the vehicle assigned to this daily plan.
     *
     * @return the vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the total weight collected during this route.
     *
     * @return total collected kilograms
     */
    public CollectedWeightKilograms getTotalCollectedKilograms() {
        return totalCollectedKilograms;
    }

    /**
     * Returns the total volume collected during this route.
     *
     * @return total collected liters
     */
    public CollectedVolumeLiters getTotalCollectedLiters() {
        return totalCollectedLiters;
    }

    /**
     * Returns the total distance of the route in meters.
     *
     * @return total distance in meters
     */
    public Distance getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    /**
     * Returns an unmodifiable view of the stops in this daily plan.
     *
     * @return unmodifiable list of stops
     */
    public List<Stop> getStops() {
        return Collections.unmodifiableList(stops);
    }

    /**
     * Compares this daily plan to another object for equality based on the unique identifier.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if the objects have the same id, {@code false} otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        DailyPlan other = (DailyPlan) otherObject;
        return Objects.equals(this.id, other.id);
    }

    /**
     * Returns a hash code value for this daily plan based on the unique identifier.
     *
     * @return hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this daily plan.
     *
     * @return a formatted string with key daily plan attributes
     */
    @Override
    public String toString() {
        return String.format("DailyPlan={id=%s, planId=%s, facilityId=%s, date=%s, planDay=%s, vehicle=%s, distance=%s}",
            id, infrastructurePlan.getId(), facility.getId(), serviceDate, planDay, vehicle.getId(), totalDistanceMeters);
    }
}
