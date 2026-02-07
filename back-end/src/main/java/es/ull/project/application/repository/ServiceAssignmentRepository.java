package es.ull.project.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import es.ull.project.domain.entity.ServiceAssignment;

/**
 * Repository interface for ServiceAssignment entities.
 *
 * Defines persistence operations for service assignments used by
 * application services without exposing storage details.
 */
public interface ServiceAssignmentRepository {

    /**
     * Delete a service assignment.
     *
     * @param entity ServiceAssignment to delete
     */
    public abstract void delete(ServiceAssignment entity);

    /**
     * Fetch all service assignments.
     *
     * @return list of service assignments
     */
    public abstract List<ServiceAssignment> fetchAll();

    /**
     * Find all service assignments (alias used by some services).
     *
     * @return list of service assignments
     */
    public abstract List<ServiceAssignment> findAll();

    /**
     * Save or update a service assignment.
     *
     * @param entity ServiceAssignment to persist
     * @return persisted ServiceAssignment
     */
    public abstract ServiceAssignment save(ServiceAssignment entity);

    /**
     * Find a service assignment by UUID.
     *
     * @param id assignment UUID
     * @return optional with the assignment if found
     */
    public abstract Optional<ServiceAssignment> findById(UUID id);
}
