package es.ull.project.adapter.rest.response.facility;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.Capacity;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;

import java.util.UUID;

/**
 * Data Transfer Object representing a Facility response
 * This class is used to send Facility data in HTTP responses
 * Uses domain value objects and enums directly
 */
public class FacilityResponseBody {

    /**
     * Unique identifier of the facility
     */
    public UUID id;

    /**
     * Type of the facility (enum)
     */
    public FacilityType facilityType;

    /**
     * Physical location of the facility (value object)
     */
    public Location location;

    /**
     * Maximum capacity of the facility (value object)
     */
    public Capacity capacity;

    /**
     * Fixed cost to open the facility (value object)
     */
    public OpeningFixedCost openingFixedCost;

    /**
     * Current status of the facility (enum)
     */
    public FacilityStatus status;

    /**
     * Accumulated waste demand assigned to the facility (value object)
     */
    public WasteDemand assignedWasteDemand;
}
