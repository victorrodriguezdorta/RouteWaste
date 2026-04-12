package es.ull.project.adapter.rest.request.container;

import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;

/**
 * ContainerPostRequestBody
 * 
 * Data Transfer Object representing the request body for creating a new Container.
 * This DTO is used in POST requests to the container endpoint.
 * 
 * Public attributes are used to allow direct access without getters/setters,
 * reducing complexity and facilitating serialization/deserialization in the
 * REST API context. As a DTO designed exclusively for data transfer, it does
 * not require encapsulation like domain entities.
 * 
 * This class contains no business logic, only data representation.
 */
public class ContainerPostRequestBody {

    /**
     * Physical location of the container.
     * Required field.
     */
    public Location location;

    /**
     * Type of waste collected by the container.
     * Required field.
     */
    public WasteType wasteType;

    /**
     * Maximum capacity of the container in liters.
     * Required field.
     */
    public ContainerCapacityLiters capacityLiters;

    /**
     * Approximate daily waste demand in liters per day.
     * Required field.
     */
    public DailyWasteDemandLitersPerDay dailyDemandLitersPerDay;

    /**
     * Service zone where the container is located.
     * Optional field.
     */
    public ServiceZone serviceZone;

    /**
     * Returns a string representation of this request body.
     * 
     * @return formatted string containing all attributes
     */
    @Override
    public String toString() {
        return String.format(
                "ContainerPostRequestBody={location=%s, wasteType=%s, capacityLiters=%s, dailyDemandLitersPerDay=%s, serviceZone=%s}",
                this.location,
                this.wasteType,
                this.capacityLiters,
                this.dailyDemandLitersPerDay,
                this.serviceZone);
    }
}
