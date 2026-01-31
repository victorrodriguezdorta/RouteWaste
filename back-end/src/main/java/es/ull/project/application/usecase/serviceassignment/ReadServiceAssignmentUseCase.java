package es.ull.project.application.usecase.serviceassignment;

import es.ull.project.domain.entity.ServiceAssignment;
import java.util.List;
import java.util.UUID;

/**
 * Use case for reading service assignments.
 */
public interface ReadServiceAssignmentUseCase {
    /**
     * Retrieves a service assignment by its unique identifier.
     *
     * @param id the unique identifier of the service assignment
     * @return the service assignment
     */
    ServiceAssignment fetch(UUID id);

    /**
     * Retrieves all service assignments.
     *
     * @return a list of all service assignments
     */
    List<ServiceAssignment> fetchAll();
}
