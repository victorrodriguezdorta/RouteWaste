package es.ull.project.application.repository;

import es.ull.project.domain.entity.DailyPlan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for DailyPlan entities.
 *
 * Defines persistence operations for daily plans used by
 * application services without exposing storage details.
 */
public interface DailyPlanRepository {

    /**
     * Save or update a daily plan.
     *
     * @param entity DailyPlan to persist
     * @return persisted DailyPlan
     */
    DailyPlan save(DailyPlan entity);

    /**
     * Delete a daily plan.
     *
     * @param entity DailyPlan to delete
     */
    void delete(DailyPlan entity);

    /**
     * Find a daily plan by its identifier.
     *
     * @param id daily plan UUID
     * @return optional containing the daily plan when found
     */
    Optional<DailyPlan> findById(UUID id);

    /**
     * Finds all daily plans associated with a specific infrastructure plan.
     *
     * @param infrastructurePlanId the parent infrastructure plan ID
     * @return a list of associated daily plans
     */
    List<DailyPlan> findByInfrastructurePlanId(UUID infrastructurePlanId);
}
