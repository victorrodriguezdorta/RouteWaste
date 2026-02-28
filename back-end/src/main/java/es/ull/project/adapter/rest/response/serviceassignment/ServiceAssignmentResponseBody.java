package es.ull.project.adapter.rest.response.serviceassignment;

import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;

import java.util.UUID;

/**
 * Data Transfer Object representing a ServiceAssignment response
 * This class is used to send ServiceAssignment data in HTTP responses.
 * 
 * Following DDD principles, this DTO uses domain value objects directly
 * instead of primitive types or nested classes. The serializer handles
 * the conversion to JSON format.
 * 
 * It contains all the data of a service assignment in a structured format,
 * including complete information about the container and facility entities.
 */
public class ServiceAssignmentResponseBody {

    /**
     * Unique identifier of the service assignment
     */
    public UUID id;

    /**
     * Complete container entity with all its data
     */
    public ContainerResponseBody container;

    /**
     * Complete facility entity with all its data
     */
    public FacilityResponseBody facility;

    /**
     * Waste demand value object from domain.
     * The serializer extracts value, quantityUnit, and timeUnit.
     */
    public WasteDemand wasteDemand;

    /**
     * Distance value object from domain.
     * The serializer extracts meters and kilometers.
     */
    public Distance distance;

    /**
     * Service time value object from domain.
     * The serializer extracts minutes.
     */
    public ServiceTime serviceTime;

    /**
     * Transportation cost value object from domain.
     * The serializer extracts amount and optional currency.
     */
    public TransportationVariableCost transportCost;
}
