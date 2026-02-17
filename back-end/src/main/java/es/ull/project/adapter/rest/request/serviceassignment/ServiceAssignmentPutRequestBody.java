package es.ull.project.adapter.rest.request.serviceassignment;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;

/**
 * ServiceAssignmentPutRequestBody
 * 
 * Data Transfer Object representing the request body for updating an existing ServiceAssignment.
 * This DTO is used in PUT requests to the service assignment endpoint.
 * 
 * Public attributes are used to allow direct access without getters/setters,
 * reducing complexity and facilitating serialization/deserialization in the
 * REST API context. As a DTO designed exclusively for data transfer, it does
 * not require encapsulation like domain entities.
 * 
 * This class contains no business logic, only data representation.
 */
public class ServiceAssignmentPutRequestBody {

    /**
     * Container entity for the assignment.
     * Required field.
     */
    public Container container;

    /**
     * Facility entity for the assignment.
     * Required field.
     */
    public Facility facility;

    /**
     * Waste demand of the container.
     * Required field.
     */
    public WasteDemand wasteDemand;

    /**
     * Distance between container and facility.
     * Required field.
     */
    public Distance distance;

    /**
     * Service time required for the assignment.
     * Required field.
     */
    public ServiceTime serviceTime;

    /**
     * Transportation cost applied for the assignment.
     * Required field.
     */
    public TransportationVariableCost transportCost;

    /**
     * Returns a string representation of this request body.
     * 
     * @return formatted string containing all attributes
     */
    @Override
    public String toString() {
        return String.format(
                "ServiceAssignmentPutRequestBody={container=%s, facility=%s, wasteDemand=%s, distance=%s, serviceTime=%s, transportCost=%s}",
                this.container,
                this.facility,
                this.wasteDemand,
                this.distance,
                this.serviceTime,
                this.transportCost);
    }
}
