package es.ull.project.application.service.dailyplan;

import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.application.usecase.dailyplan.ReadDailyPlanUseCase;
import es.ull.project.domain.entity.DailyPlan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service responsible for reading daily plans from the system.
 * This service implements the {@link ReadDailyPlanUseCase} interface.
 */
public class ReadDailyPlanService implements ReadDailyPlanUseCase {

    private final DailyPlanRepository repository;

    /**
     * Constructs a new ReadDailyPlanService with the specified repository.
     *
     * @param repository the daily plan repository used for persistence operations
     */
    public ReadDailyPlanService(DailyPlanRepository repository) {
        this.repository = repository;
    }

    /**
     * Finds a daily plan by its unique identifier.
     *
     * @param id the UUID of the daily plan to find
     * @return an Optional containing the daily plan if found, or empty if not found
     */
    @Override
    public Optional<DailyPlan> findById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Finds all daily plans associated with a specific infrastructure plan.
     *
     * @param infrastructurePlanId the UUID of the parent infrastructure plan
     * @return a list of daily plans belonging to the specified infrastructure plan
     */
    @Override
    public List<DailyPlan> findByInfrastructurePlanId(UUID infrastructurePlanId) {
        return repository.findByInfrastructurePlanId(infrastructurePlanId);
    }
}
