package es.ull.project.adapter.rest.response.facility;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
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
     * Storage capacity of the facility in kilograms (value object)
     */
    public StorageCapacityKilograms storageCapacity;

    /**
     * Processing capacity of the facility in kilograms per day (value object)
     */
    public ProcessingCapacityKilogramsPerDay processingCapacity;

    /**
     * Truck unloading time in minutes (value object)
     */
    public UnloadingTime unloadingTime;

    /**
     * Fixed cost to open the facility (value object)
     */
    public OpeningFixedCost openingFixedCost;

    /**
     * Current status of the facility (enum)
     */
    public FacilityStatus status;

    /**
     * Current filling level - accumulated waste demand assigned to the facility (value object)
     */
    public DailyWasteDemandLitersPerDay currentFillingLevel;
}
