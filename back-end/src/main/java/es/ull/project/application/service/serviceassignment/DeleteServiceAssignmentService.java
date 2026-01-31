package es.ull.project.application.service.serviceassignment;

import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.serviceassignment.DeleteServiceAssignmentUseCase;
import es.ull.project.domain.entity.ServiceAssignment;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service responsible for deleting service assignments from the system.
 * <p>
 * This service implements the {@link DeleteServiceAssignmentUseCase} interface and provides
 * the business logic for service assignment deletion operations.
 * </p>
 *
 * @see DeleteServiceAssignmentUseCase
 * @see ServiceAssignment
 */
public class DeleteServiceAssignmentService implements DeleteServiceAssignmentUseCase {

    private final ServiceAssignmentRepository repository;

    /**
     * Constructs a new DeleteServiceAssignmentService with the specified repository.
     *
     * @param repository the service assignment repository used for persistence operations
     */
    public DeleteServiceAssignmentService(ServiceAssignmentRepository repository) {
        this.repository = repository;
    }

    /**
     * Deletes a service assignment by its unique identifier.
     *
     * @param id the unique identifier of the service assignment to delete
     * @return the deleted service assignment
     * @throws NoSuchElementException if no service assignment is found with the given identifier
     */
    @Override
    public ServiceAssignment delete(UUID id) {
        ServiceAssignment existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("ServiceAssignment not found"));
        this.repository.delete(existing);
        return existing;
    }
}
