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

    /**
     * Find all service assignments that reference a specific container.
     *
     * @param containerId the container UUID
     * @return list of service assignments using this container
     */
    public abstract List<ServiceAssignment> findByContainerId(UUID containerId);

    /**
     * Find all service assignments that reference a specific facility.
     *
     * @param facilityId the facility UUID
     * @return list of service assignments using this facility
     */
    public abstract List<ServiceAssignment> findByFacilityId(UUID facilityId);
    /**
     * Find multiple service assignments by their identifiers (batch operation).
     * This method is optimized for retrieving multiple entities at once,
     * reducing the number of database queries (solving N+1 problem).
     *
     * @param ids collection of service assignment UUIDs
     * @return list of found service assignments (may be less than requested if some don't exist)
     */
    public abstract List<ServiceAssignment> findAllById(List<UUID> ids);}
