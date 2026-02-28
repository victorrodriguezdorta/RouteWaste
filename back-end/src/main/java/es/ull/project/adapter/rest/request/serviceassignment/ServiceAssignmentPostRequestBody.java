package es.ull.project.adapter.rest.request.serviceassignment;

import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;

import java.util.UUID;

/**
 * ServiceAssignmentPostRequestBody
 * 
 * Data Transfer Object representing the request body for creating a new ServiceAssignment.
 * This DTO is used in POST requests to the service assignment endpoint.
 * 
 * Instead of receiving complete Container and Facility entities, this DTO receives only
 * their UUIDs. The service layer will fetch the complete entities from their respective
 * repositories, ensuring data integrity and avoiding duplication or inconsistencies.
 * 
 * Public attributes are used to allow direct access without getters/setters,
 * reducing complexity and facilitating serialization/deserialization in the
 * REST API context. As a DTO designed exclusively for data transfer, it does
 * not require encapsulation like domain entities.
 * 
 * This class contains no business logic, only data representation.
 */
public class ServiceAssignmentPostRequestBody {

    /**
     * UUID of the container to be assigned.
     * Required field.
     */
    public UUID containerId;

    /**
     * UUID of the facility to be assigned.
     * Required field.
     */
    public UUID facilityId;

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
                "ServiceAssignmentPostRequestBody={containerId=%s, facilityId=%s, wasteDemand=%s, distance=%s, serviceTime=%s, transportCost=%s}",
                this.containerId,
                this.facilityId,
                this.wasteDemand,
                this.distance,
                this.serviceTime,
                this.transportCost);
    }
}
