package es.ull.project.application.usecase.serviceassignment;

import es.ull.project.domain.entity.ServiceAssignment;
import java.util.UUID;

/**
 * Use case for deleting a service assignment.
 */
public interface DeleteServiceAssignmentUseCase {
    /**
     * Deletes a service assignment by its identifier.
     *
     * @param id the unique identifier of the service assignment to delete
     * @return the deleted service assignment
     */
    ServiceAssignment delete(UUID id);
}
