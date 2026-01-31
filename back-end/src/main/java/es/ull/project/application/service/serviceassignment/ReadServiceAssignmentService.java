package es.ull.project.application.service.serviceassignment;

import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.serviceassignment.ReadServiceAssignmentUseCase;
import es.ull.project.domain.entity.ServiceAssignment;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service responsible for reading service assignment data from the system.
 * This service implements the {@link ReadServiceAssignmentUseCase} interface and provides
 * the business logic for service assignment retrieval operations.
 */
public class ReadServiceAssignmentService implements ReadServiceAssignmentUseCase {

    private final ServiceAssignmentRepository repository;

    /**
     * Constructs a new ReadServiceAssignmentService with the specified repository.
     *
     * @param repository the service assignment repository used for persistence operations
     */
    public ReadServiceAssignmentService(ServiceAssignmentRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a service assignment by its unique identifier.
     *
     * @param id the unique identifier of the service assignment to retrieve
     * @return the service assignment with the specified identifier
     * @throws NoSuchElementException if no service assignment is found with the given identifier
     */
    @Override
    public ServiceAssignment fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("ServiceAssignment not found"));
    }

    /**
     * Retrieves all service assignments from the system.
     *
     * @return a list containing all service assignments
     */
    @Override
    public List<ServiceAssignment> fetchAll() {
        return this.repository.findAll();
    }
}
