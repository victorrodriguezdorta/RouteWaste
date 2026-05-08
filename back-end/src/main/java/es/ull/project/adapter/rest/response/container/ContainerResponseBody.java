package es.ull.project.adapter.rest.response.container;

import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import java.util.UUID;
/**
 * Data Transfer Object representing a Container response
 * This class is used to send Container data in HTTP responses
 * Uses domain value objects and enums directly
 */
public class ContainerResponseBody {

    /**
     * Unique identifier of the container
     */
    public UUID id;

    /**
     * Physical location of the container (value object)
     */
    public Location location;

    /**
     * Type of waste collected by the container (enum)
     */
    public WasteType wasteType;

    /**
     * Maximum capacity of the container in liters (value object)
     */
    public ContainerCapacityLiters capacityLiters;

    /**
     * Approximate daily waste demand in liters per day (value object)
     */
    public DailyWasteDemandLitersPerDay dailyDemandLitersPerDay;

    /**
     * Service zone where the container is located (enum)
     * This field is optional
     */
    public ServiceZone serviceZone;
}
