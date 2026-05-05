package es.ull.project.application.usecase.dailyplan;

import es.ull.project.domain.entity.DailyPlan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case for reading daily plans.
 */
public interface ReadDailyPlanUseCase {

    /**
     * Finds a daily plan by its unique identifier.
     *
     * @param id the unique identifier of the daily plan
     * @return an Optional containing the daily plan if found
     */
    Optional<DailyPlan> findById(UUID id);

    /**
     * Finds all daily plans associated with a specific infrastructure plan.
     *
     * @param infrastructurePlanId the unique identifier of the parent infrastructure plan
     * @return a list of daily plans belonging to the specified infrastructure plan
     */
    List<DailyPlan> findByInfrastructurePlanId(UUID infrastructurePlanId);
}
