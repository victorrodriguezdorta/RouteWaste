package es.ull.project.application.service.serviceassignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.ull.project.adapter.rest.exception.FieldError;
import es.ull.project.adapter.rest.exception.ValidationException;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
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
 * 
 * This service receives UUIDs for container and facility, fetches the complete entities
 * from their repositories, validates their existence, and creates the service assignment
 * with the full entity references as required by the domain model.
 */
public class CreateServiceAssignmentService implements CreateServiceAssignmentUseCase {

    private final ServiceAssignmentRepository serviceAssignmentRepository;
    private final ContainerRepository containerRepository;
    private final FacilityRepository facilityRepository;

    /**
     * Constructs a new CreateServiceAssignmentService with the specified repositories.
     *
     * @param serviceAssignmentRepository the service assignment repository used for persistence operations
     * @param containerRepository the container repository used to fetch container entities
     * @param facilityRepository the facility repository used to fetch facility entities
     */
    public CreateServiceAssignmentService(
            ServiceAssignmentRepository serviceAssignmentRepository,
            ContainerRepository containerRepository,
            FacilityRepository facilityRepository) {
        this.serviceAssignmentRepository = serviceAssignmentRepository;
        this.containerRepository = containerRepository;
        this.facilityRepository = facilityRepository;
    }

    /**
     * Creates a new service assignment with the specified parameters.
     * 
     * This method first validates that both Container and Facility entities exist
     * by checking their respective repositories. If one or both entities do not exist,
     * a ValidationException is thrown with all the validation errors in structured format.
     * Once both entities are retrieved, a new ServiceAssignment is created with
     * the complete entity references and persisted to the database.
     *
     * @param containerId the UUID of the container to be assigned
     * @param facilityId the UUID of the facility responsible for servicing the container
     * @param wasteDemand the waste demand for this assignment
     * @param distance the distance between the container and facility
     * @param serviceTime the time required to service the container
     * @param transportCost the transportation cost for this assignment
     * @return the newly created and persisted service assignment
     * @throws ValidationException if the container or facility (or both) do not exist
     */
    @Override
    public ServiceAssignment create(UUID containerId, UUID facilityId, WasteDemand wasteDemand, Distance distance, ServiceTime serviceTime, TransportationVariableCost transportCost) {
        // Validate both IDs and collect all errors
        List<FieldError> errors = new ArrayList<>();
        
        // Try to fetch container
        Optional<Container> containerOpt = containerRepository.findById(containerId);
        if (containerOpt.isEmpty()) {
            errors.add(new FieldError("containerId", "Container with id " + containerId + " not found"));
        }
        
        // Try to fetch facility
        Optional<Facility> facilityOpt = facilityRepository.findById(facilityId);
        if (facilityOpt.isEmpty()) {
            errors.add(new FieldError("facilityId", "Facility with id " + facilityId + " not found"));
        }
        
        // If there are any errors, throw ValidationException with all error details
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        
        // Get the entities (we know they exist at this point)
        Container container = containerOpt.get();
        Facility facility = facilityOpt.get();
        
        // Create service assignment with complete entities
        ServiceAssignment newAssignment = new ServiceAssignment(container, facility, wasteDemand, distance, serviceTime, transportCost);
        
        // Persist and return
        return this.serviceAssignmentRepository.save(newAssignment);
    }
}
