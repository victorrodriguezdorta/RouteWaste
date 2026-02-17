package es.ull.project.adapter.rest.request.facility;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.location.Location;

/**
 * FacilityPostRequestBody
 * 
 * Data Transfer Object representing the request body for creating a new Facility.
 * This DTO is used in POST requests to the facility endpoint.
 * 
 * Public attributes are used to allow direct access without getters/setters,
 * reducing complexity and facilitating serialization/deserialization in the
 * REST API context. As a DTO designed exclusively for data transfer, it does
 * not require encapsulation like domain entities.
 * 
 * This class contains no business logic, only data representation.
 */
public class FacilityPostRequestBody {

    /**
     * Type of the facility (e.g., TRANSFER_STATION, TREATMENT_PLANT).
     * Required field.
     */
    public FacilityType facilityType;

    /**
     * Physical location of the facility.
     * Required field.
     */
    public Location location;

    /**
     * Maximum capacity of the facility.
     * Required field.
     */
    public Capacity capacity;

    /**
     * Fixed cost to open the facility.
     * Required field.
     */
    public OpeningFixedCost openingFixedCost;

    /**
     * Current status of the facility (e.g., OPEN, CLOSED, UNDER_CONSTRUCTION).
     * Required field.
     */
    public FacilityStatus status;

    /**
     * Returns a string representation of this request body.
     * 
     * @return formatted string containing all attributes
     */
    @Override
    public String toString() {
        return String.format(
                "FacilityPostRequestBody={facilityType=%s, location=%s, capacity=%s, openingFixedCost=%s, status=%s}",
                this.facilityType,
                this.location,
                this.capacity,
                this.openingFixedCost,
                this.status);
    }
}
