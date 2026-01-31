package es.ull.project.application.service.serviceassignment;

import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.serviceassignment.CreateServiceAssignmentUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;

/**
 * Service responsible for creating new service assignments in the system.
 * This service implements the {@link CreateServiceAssignmentUseCase} interface and provides
 * the business logic for service assignment creation operations. A service assignment
 * represents the relationship between a container and a facility for waste collection.
 */
public class CreateServiceAssignmentService implements CreateServiceAssignmentUseCase {

    private final ServiceAssignmentRepository repository;

    /**
     * Constructs a new CreateServiceAssignmentService with the specified repository.
     *
     * @param repository the service assignment repository used for persistence operations
     */
    public CreateServiceAssignmentService(ServiceAssignmentRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new service assignment with the specified parameters.
     *
     * @param container the container to be assigned
     * @param facility the facility responsible for servicing the container
     * @param wasteDemand the waste demand for this assignment
     * @param distance the distance between the container and facility
     * @param serviceTime the time required to service the container
     * @param transportCost the transportation cost for this assignment
     * @return the newly created and persisted service assignment
     */
    @Override
    public ServiceAssignment create(Container container, Facility facility, WasteDemand wasteDemand, Distance distance, ServiceTime serviceTime, TransportationVariableCost transportCost) {
        ServiceAssignment newAssignment = new ServiceAssignment(container, facility, wasteDemand, distance, serviceTime, transportCost);
        ServiceAssignment saved = this.repository.save(newAssignment);
        return saved;
    }
}
