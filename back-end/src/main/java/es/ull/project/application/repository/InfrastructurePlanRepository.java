package es.ull.project.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Find all infrastructure plans using pagination and sorting.
     *
     * @param pageable pagination and sort information
     * @return page of infrastructure plans
     */
    public abstract Page<InfrastructurePlan> findAll(Pageable pageable);

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
}
