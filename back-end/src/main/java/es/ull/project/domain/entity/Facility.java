package es.ull.project.domain.entity;


import java.util.Objects;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.valueobject.identifiers.FacilityId;
import es.ull.project.domain.valueobject.clasification.FacilityType;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;

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
     */
    private final FacilityId id;

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
     * @param id                Facility identifier.
     * @param facilityType      Type of facility.
     * @param location          Facility location.
     * @param capacity          Facility capacity.
     * @param openingFixedCost  Fixed opening cost.
     * @param status            Facility status.
     */
    public Facility(FacilityId id,
                    FacilityType facilityType,
                    Location location,
                    Capacity capacity,
                    OpeningFixedCost openingFixedCost,
                    FacilityStatus status) {

        this.validateId(id);
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
        this.assignedWasteDemand = new WasteDemand(0.0);
    }

    private void validateId(FacilityId id) {
        if (id == null) {
            throw new IllegalArgumentException(ID_NOT_DEFINED);
        }
    }

    private void validateFacilityType(FacilityType facilityType) {
        if (facilityType == null) {
            throw new IllegalArgumentException(TYPE_NOT_DEFINED);
        }
    }

    private void validateLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException(LOCATION_NOT_DEFINED);
        }
    }

    private void validateCapacity(Capacity capacity) {
        if (capacity == null) {
            throw new IllegalArgumentException(CAPACITY_NOT_DEFINED);
        }
    }

    private void validateOpeningFixedCost(OpeningFixedCost openingFixedCost) {
        if (openingFixedCost == null) {
            throw new IllegalArgumentException(OPENING_COST_NOT_DEFINED);
        }
    }

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
    public void assignWasteDemand(WasteDemand demand) {  // TODO: Check if this is correct
        if (this.status.isDiscarded()) {
            throw new IllegalStateException(FACILITY_DISCARDED);
        }

        WasteDemand newTotal = this.assignedWasteDemand.add(demand);
        if (newTotal.greaterThan(this.capacity)) {
            throw new IllegalStateException(CAPACITY_EXCEEDED);
        }

        this.assignedWasteDemand = newTotal;
    }

    public FacilityId getId() {
        return this.id;
    }

    public FacilityType getFacilityType() {
        return this.facilityType;
    }

    public Location getLocation() {
        return this.location;
    }

    public Capacity getCapacity() {
        return this.capacity;
    }

    public OpeningFixedCost getOpeningFixedCost() {
        return this.openingFixedCost;
    }

    public FacilityStatus getStatus() {
        return this.status;
    }

    public WasteDemand getAssignedWasteDemand() {
        return this.assignedWasteDemand;
    }

    public void updateStatus(FacilityStatus status) {
        this.validateStatus(status);
        this.status = status;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

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
                this.status
        );
    }
}