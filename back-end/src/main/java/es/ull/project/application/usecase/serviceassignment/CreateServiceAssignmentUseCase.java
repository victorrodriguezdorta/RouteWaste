package es.ull.project.application.usecase.serviceassignment;

import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;

/**
 * Use case for creating a new service assignment.
 */
public interface CreateServiceAssignmentUseCase {
    /**
     * Creates a new service assignment with the specified attributes.
     *
     * @param container     the container to assign
     * @param facility      the facility to assign to
     * @param wasteDemand   the waste demand of the assignment
     * @param distance      the distance between container and facility
     * @param serviceTime   the service time required
     * @param transportCost the transportation cost
     * @return the created service assignment
     */
    ServiceAssignment create(Container container, Facility facility, WasteDemand wasteDemand, Distance distance, ServiceTime serviceTime, TransportationVariableCost transportCost);
}
