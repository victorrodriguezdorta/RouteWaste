package es.ull.project.application.usecase.serviceassignment;

import java.util.UUID;

import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;

/**
 * Use case for creating a new service assignment.
 * 
 * This use case receives the UUIDs of the container and facility,
 * fetches the complete entities from their repositories, validates
 * their existence, and creates a service assignment with the full entities.
 */
public interface CreateServiceAssignmentUseCase {
    /**
     * Creates a new service assignment with the specified attributes.
     *
     * @param containerId   the UUID of the container to assign
     * @param facilityId    the UUID of the facility to assign to
     * @param wasteDemand   the waste demand of the assignment
     * @param distance      the distance between container and facility
     * @param serviceTime   the service time required
     * @param transportCost the transportation cost
     * @return the created service assignment
     * @throws IllegalArgumentException if container or facility does not exist
     */
    ServiceAssignment create(UUID containerId, UUID facilityId, WasteDemand wasteDemand, Distance distance, ServiceTime serviceTime, TransportationVariableCost transportCost);
}
