package es.ull.project.adapter.rest.request.facility;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;

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

    public Name name;

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
     * Storage capacity of the facility in kilograms.
     * Required field.
     */
    public StorageCapacityKilograms storageCapacity;

    /**
     * Processing capacity of the facility in kilograms per day.
     * Required field.
     */
    public ProcessingCapacityKilogramsPerDay processingCapacity;

    /**
     * Truck unloading time in minutes.
     * Required field.
     */
    public UnloadingTime unloadingTime;

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
                "FacilityPostRequestBody={name=%s, facilityType=%s, location=%s, storageCapacity=%s, processingCapacity=%s, unloadingTime=%s, openingFixedCost=%s, status=%s}",
                this.name,
                this.facilityType,
                this.location,
                this.storageCapacity,
                this.processingCapacity,
                this.unloadingTime,
                this.openingFixedCost,
                this.status);
    }
}
