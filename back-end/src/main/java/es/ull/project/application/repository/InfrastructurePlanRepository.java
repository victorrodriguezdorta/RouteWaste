package es.ull.project.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.ull.project.domain.entity.InfrastructurePlan;

/**
 * Repository interface for InfrastructurePlan aggregates.
 *
 * Encapsulates persistence operations for planning entities so that
 * use cases can operate independently of the storage mechanism.
 */
public interface InfrastructurePlanRepository {

    /**
     * Delete an infrastructure plan.
     *
     * @param entity plan to delete
     */
    public abstract void delete(InfrastructurePlan entity);

    /**
     * Fetch all infrastructure plans.
     *
     * @return list of infrastructure plans
     */
    public abstract List<InfrastructurePlan> fetchAll();

    /**
     * Find all infrastructure plans (alias for compatibility).
     *
     * @return list of infrastructure plans
     */
    public abstract List<InfrastructurePlan> findAll();

    /**
     * Save or update an infrastructure plan.
     *
     * @param entity plan to persist
     * @return persisted InfrastructurePlan
     */
    public abstract InfrastructurePlan save(InfrastructurePlan entity);

    /**
     * Find an infrastructure plan by UUID.
     *
     * @param id plan UUID
     * @return optional with the plan if present
     */
    public abstract Optional<InfrastructurePlan> findById(UUID id);

    /**
     * Find all infrastructure plans that include a specific facility.
     *
     * @param facilityId the facility UUID
     * @return list of infrastructure plans using this facility
     */
    public abstract List<InfrastructurePlan> findByFacilityId(UUID facilityId);

    /**
     * Find all infrastructure plans that include a specific service assignment.
     *
     * @param serviceAssignmentId the service assignment UUID
     * @return list of infrastructure plans using this service assignment
     */
    public abstract List<InfrastructurePlan> findByServiceAssignmentId(UUID serviceAssignmentId);
}
