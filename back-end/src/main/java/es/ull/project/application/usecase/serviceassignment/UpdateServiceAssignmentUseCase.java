package es.ull.project.application.usecase.serviceassignment;

import java.util.UUID;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;

/**
 * Use case: update of a {@link ServiceAssignment}.
 */
public interface UpdateServiceAssignmentUseCase {

    /**
     * Updates an existing assignment.
     *
     * @param id               identifier of the assignment to update
     * @param containerId      identifier of the container
     * @param facilityId       identifier of the facility
     * @param newWasteDemand   new waste demand
     * @param newDistance      new calculated distance
     * @param newServiceTime   new service time
     * @param newTransportCost new transport cost
     * @return the updated assignment
     */
    ServiceAssignment update(UUID id, UUID containerId, UUID facilityId, WasteDemand newWasteDemand, Distance newDistance, ServiceTime newServiceTime, TransportationVariableCost newTransportCost);
}
