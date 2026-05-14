package es.ull.project.application.repository;

import es.ull.project.domain.entity.InfrastructurePlan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    /**
     * Finds plans that are still {@link es.ull.project.domain.enumerate.InfrastructurePlanValidityState#VALID}
     * and that reference the given master entity (execution-request JSON snapshot, selected facilities,
     * service assignments, or daily plans — see persistence layer).
     *
     * @param entityId edited facility, vehicle, or container id
     * @return matching plans (may be empty)
     */
    List<InfrastructurePlan> findValidPlansReferencingEntityInExecutionRequest(UUID entityId);

    /**
     * Returns true if any plan references the entity id in any persisted association (including obsolete plans).
     *
     * @param entityId facility, vehicle, or container id
     * @return true when at least one plan is linked to that id
     */
    boolean existsAnyPlanReferencingEntityInExecutionRequest(UUID entityId);

    /**
     * All infrastructure plans that reference the given master entity id anywhere in the stored snapshot graph
     * (execution-request JSON, selected facility ids, service assignments, daily plans and route stops),
     * regardless of {@link es.ull.project.domain.enumerate.InfrastructurePlanValidityState validity}.
     *
     * @param entityId facility, vehicle, or container id
     * @return matching plans (empty when none or {@code entityId} is null)
     */
    List<InfrastructurePlan> findPlansReferencingEntityInExecutionRequest(UUID entityId);
}
