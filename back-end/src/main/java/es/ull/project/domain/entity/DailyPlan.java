package es.ull.project.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.location.Distance;

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

    private final UUID id;
    private final InfrastructurePlan infrastructurePlan;
    private final Facility facility;
    private final LocalDate serviceDate;
    private final Integer planDay;
    private final Vehicle vehicle;
    private final CollectedWeightKilograms totalCollectedKilograms;
    private final CollectedVolumeLiters totalCollectedLiters;
    private final Distance totalDistanceMeters;
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
                     Integer planDay,
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
     * Restore constructor.
     * Restores a DailyPlan from persistence with all its attributes.
     */
    public DailyPlan(UUID id,
                     InfrastructurePlan infrastructurePlan,
                     Facility facility,
                     LocalDate serviceDate,
                     Integer planDay,
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

    public void addStop(Stop stop) {
        if (stop == null) {
            throw new IllegalArgumentException(INVALID_STOP);
        }
        this.stops.add(stop);
    }

    public UUID getId() {
        return id;
    }

    public InfrastructurePlan getInfrastructurePlan() {
        return infrastructurePlan;
    }

    public Facility getFacility() {
        return facility;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public Integer getPlanDay() {
        return planDay;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public CollectedWeightKilograms getTotalCollectedKilograms() {
        return totalCollectedKilograms;
    }

    public CollectedVolumeLiters getTotalCollectedLiters() {
        return totalCollectedLiters;
    }

    public Distance getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public List<Stop> getStops() {
        return Collections.unmodifiableList(stops);
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("DailyPlan={id=%s, planId=%s, facilityId=%s, date=%s, planDay=%s, vehicle=%s, distance=%s}",
            id, infrastructurePlan.getId(), facility.getId(), serviceDate, planDay, vehicle.getId(), totalDistanceMeters);
    }
}
