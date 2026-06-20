package es.ull.project.application.algorithm;

import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.domain.entity.DailyPlan;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory daily plan repository used by algorithm persistence tests.
 */
class TestInMemoryDailyPlanRepository implements DailyPlanRepository {

    private final Map<UUID, DailyPlan> saved = new LinkedHashMap<>();

    /**
     * Saves a daily plan in memory.
     *
     * @param entity daily plan to save.
     * @return saved daily plan.
     */
    @Override
    public DailyPlan save(DailyPlan entity) {
        this.saved.put(entity.getId(), entity);
        return entity;
    }

    /**
     * Deletes a daily plan from memory.
     *
     * @param entity daily plan to delete.
     */
    @Override
    public void delete(DailyPlan entity) {
        if (entity != null) {
            this.saved.remove(entity.getId());
        }
    }

    /**
     * Finds a daily plan by identifier.
     *
     * @param id daily plan identifier.
     * @return matching daily plan, if any.
     */
    @Override
    public Optional<DailyPlan> findById(UUID id) {
        return Optional.ofNullable(this.saved.get(id));
    }

    /**
     * Finds daily plans that belong to an infrastructure plan.
     *
     * @param infrastructurePlanId infrastructure plan identifier.
     * @return matching daily plans.
     */
    @Override
    public List<DailyPlan> findByInfrastructurePlanId(UUID infrastructurePlanId) {
        List<DailyPlan> plans = new ArrayList<>();
        for (DailyPlan plan : this.saved.values()) {
            if (plan.getInfrastructurePlan().getId().equals(infrastructurePlanId)) {
                plans.add(plan);
            }
        }
        return plans;
    }

    /**
     * Finds all saved daily plans.
     *
     * @return saved daily plans.
     */
    List<DailyPlan> findAll() {
        return new ArrayList<>(this.saved.values());
    }
}
