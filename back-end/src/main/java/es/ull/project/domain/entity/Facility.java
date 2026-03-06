package es.ull.project.domain.entity;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;

import java.util.Objects;
import java.util.UUID;

/**
 * Facility
 *
 * Represents a waste management infrastructure such as:
 * - Truck operating base
 * - Transfer station
 * - Treatment or classification plant
 *
 * It is an aggregate root.
 */
public class Facility {

    public static final String ID_NOT_DEFINED = "Facility id is not defined";
    public static final String TYPE_NOT_DEFINED = "Facility type is not defined";
    public static final String LOCATION_NOT_DEFINED = "Facility location is not defined";
    public static final String CAPACITY_NOT_DEFINED = "Facility capacity is not defined";
    public static final String OPENING_COST_NOT_DEFINED = "Opening fixed cost is not defined";
    public static final String STATUS_NOT_DEFINED = "Facility status is not defined";
    public static final String FACILITY_DISCARDED = "Facility is discarded and cannot receive assignments";
    public static final String CAPACITY_EXCEEDED = "Facility capacity exceeded";

    /**
     * Identifier of the facility.
     * It is a required and immutable attribute.
     * It is a computed attribute.
     */
    private final UUID id;

    /**
     * Type of the facility.
     * It is a required attribute.
     */
    private FacilityType facilityType;

    /**
     * Physical location of the facility.
     * It is a required attribute.
     */
    private Location location;

    /**
     * Maximum capacity of the facility.
     * It is a required attribute.
     */
    private Capacity capacity;

    /**
     * Fixed cost to open the facility.
     * It is a required attribute.
     */
    private OpeningFixedCost openingFixedCost;

    /**
     * Current status of the facility.
     * It is a required attribute.
     */
    private FacilityStatus status;

    /**
     * Accumulated waste demand assigned to the facility.
     * It is a computed attribute.
     */
    private WasteDemand assignedWasteDemand;

    /**
     * Creates a new Facility.
     *
     * @param id               Facility identifier.
     * @param facilityType     Type of facility.
     * @param location         Facility location.
     * @param capacity         Facility capacity.
     * @param openingFixedCost Fixed opening cost.
     * @param status           Facility status.
     */
    public Facility(
            FacilityType facilityType,
            Location location,
            Capacity capacity,
            OpeningFixedCost openingFixedCost,
            FacilityStatus status) {
        this.validateFacilityType(facilityType);
        this.validateLocation(location);
        this.validateCapacity(capacity);
        this.validateOpeningFixedCost(openingFixedCost);
        this.validateStatus(status);
        this.id = UUID.randomUUID();
        this.facilityType = facilityType;
        this.location = location;
        this.capacity = capacity;
        this.openingFixedCost = openingFixedCost;
        this.status = status;
        this.assignedWasteDemand = new WasteDemand(0.0, capacity.getQuantityUnit(), capacity.getTimeUnit());
    }

    /**
     * Copy constructor.
     * Creates a new Facility as a copy of another Facility.
     *
     * @param otherObject the Facility to copy
     */
    public Facility(Facility otherObject) {
        this.id = otherObject.id;
        this.facilityType = otherObject.facilityType;
        this.location = otherObject.location;
        this.capacity = otherObject.capacity;
        this.openingFixedCost = otherObject.openingFixedCost;
        this.status = otherObject.status;
        this.assignedWasteDemand = otherObject.assignedWasteDemand;
    }

    /**
     * Restore constructor.
     * Restores a Facility from persistence with all its attributes.
     *
     * @param id                  the facility identifier
     * @param facilityType        the type of facility
     * @param location            the facility location
     * @param capacity            the facility capacity
     * @param openingFixedCost    the fixed opening cost
     * @param status              the facility status
     * @param assignedWasteDemand the assigned waste demand
     */
    public Facility(UUID id,
            FacilityType facilityType,
            Location location,
            Capacity capacity,
            OpeningFixedCost openingFixedCost,
            FacilityStatus status,
            WasteDemand assignedWasteDemand) {
        this.validateFacilityType(facilityType);
        this.validateLocation(location);
        this.validateCapacity(capacity);
        this.validateOpeningFixedCost(openingFixedCost);
        this.validateStatus(status);
        this.id = id;
        this.facilityType = facilityType;
        this.location = location;
        this.capacity = capacity;
        this.openingFixedCost = openingFixedCost;
        this.status = status;
        this.assignedWasteDemand = assignedWasteDemand != null ? assignedWasteDemand : new WasteDemand(0.0, capacity.getQuantityUnit(), capacity.getTimeUnit());
    }

    /**
     * Returns the unique identifier of the facility.
     *
     * @return Facility UUID.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Validates that the facility type is not null.
     *
     * @param facilityType the facility type to validate
     * @throws IllegalArgumentException if facilityType is null
     */
    private void validateFacilityType(FacilityType facilityType) {
        if (facilityType == null) {
            throw new IllegalArgumentException(TYPE_NOT_DEFINED);
        }
    }

    /**
     * Validates that the location is not null.
     *
     * @param location the location to validate
     * @throws IllegalArgumentException if location is null
     */
    private void validateLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException(LOCATION_NOT_DEFINED);
        }
    }

    /**
     * Validates that the capacity is not null.
     *
     * @param capacity the capacity to validate
     * @throws IllegalArgumentException if capacity is null
     */
    private void validateCapacity(Capacity capacity) {
        if (capacity == null) {
            throw new IllegalArgumentException(CAPACITY_NOT_DEFINED);
        }
    }

    /**
     * Validates that the opening fixed cost is not null.
     *
     * @param openingFixedCost the opening fixed cost to validate
     * @throws IllegalArgumentException if openingFixedCost is null
     */
    private void validateOpeningFixedCost(OpeningFixedCost openingFixedCost) {
        if (openingFixedCost == null) {
            throw new IllegalArgumentException(OPENING_COST_NOT_DEFINED);
        }
    }

    /**
     * Validates that the status is not null.
     *
     * @param status the facility status to validate
     * @throws IllegalArgumentException if status is null
     */
    private void validateStatus(FacilityStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(STATUS_NOT_DEFINED);
        }
    }

    /**
     * Assigns waste demand to the facility.
     * Checks capacity and facility status invariants.
     *
     * @param demand Waste demand to assign.
     */
    public void assignWasteDemand(WasteDemand demand) {
        if (this.status.isDiscarded()) {
            throw new IllegalStateException(FACILITY_DISCARDED);
        }
        WasteDemand newTotal = this.assignedWasteDemand.add(demand);
        if (newTotal.greaterThan(this.capacity)) {
            throw new IllegalStateException(CAPACITY_EXCEEDED);
        }
        this.assignedWasteDemand = newTotal;
    }

    /**
     * Returns the facility type.
     *
     * @return the type of facility
     */
    public FacilityType getFacilityType() {
        return this.facilityType;
    }

    /**
     * Returns the facility location.
     *
     * @return the location of the facility
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Returns the facility capacity.
     *
     * @return the capacity of the facility
     */
    public Capacity getCapacity() {
        return this.capacity;
    }

    /**
     * Returns the opening fixed cost.
     *
     * @return the fixed cost to open the facility
     */
    public OpeningFixedCost getOpeningFixedCost() {
        return this.openingFixedCost;
    }

    /**
     * Returns the facility status.
     *
     * @return the current status of the facility
     */
    public FacilityStatus getStatus() {
        return this.status;
    }

    /**
     * Returns the assigned waste demand.
     *
     * @return the waste demand assigned to the facility
     */
    public WasteDemand getAssignedWasteDemand() {
        return this.assignedWasteDemand;
    }

    /**
     * Updates the facility status.
     *
     * @param status the new facility status
     */
    public void updateStatus(FacilityStatus status) {
        this.validateStatus(status);
        this.status = status;
    }

    /**
     * Updates the facility type.
     *
     * @param facilityType the new facility type
     */
    public void updateFacilityType(FacilityType facilityType) {
        this.validateFacilityType(facilityType);
        this.facilityType = facilityType;
    }

    /**
     * Updates the facility location.
     *
     * @param location the new facility location
     */
    public void updateLocation(Location location) {
        this.validateLocation(location);
        this.location = location;
    }

    /**
     * Updates the facility capacity.
     *
     * @param capacity the new facility capacity
     */
    public void updateCapacity(Capacity capacity) {
        this.validateCapacity(capacity);
        this.capacity = capacity;
    }

    /**
     * Updates the facility opening fixed cost.
     *
     * @param openingFixedCost the new opening fixed cost
     */
    public void updateOpeningFixedCost(OpeningFixedCost openingFixedCost) {
        this.validateOpeningFixedCost(openingFixedCost);
        this.openingFixedCost = openingFixedCost;
    }

    /**
     * Compares this facility with another object for equality.
     *
     * @param otherObject the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (getClass() != otherObject.getClass()) {
            return false;
        }
        final Facility other = (Facility) otherObject;
        return Objects.equals(this.id, other.id);
    }

    /**
     * Returns the hash code of this facility.
     *
     * @return the hash code based on the facility id
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * Returns a string representation of this facility.
     *
     * @return a formatted string with facility details
     */
    @Override
    public String toString() {
        return String.format(
                "Facility={id=%s, type=%s, location=%s, capacity=%s, assignedDemand=%s, openingCost=%s, status=%s}",
                this.id,
                this.facilityType,
                this.location,
                this.capacity,
                this.assignedWasteDemand,
                this.openingFixedCost,
                this.status);
    }
}