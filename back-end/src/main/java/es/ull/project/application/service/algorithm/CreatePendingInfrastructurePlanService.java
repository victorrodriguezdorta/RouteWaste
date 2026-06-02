package es.ull.project.application.service.algorithm;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.algorithm.CreatePendingInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.time.ExecutedAt;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Persists a lightweight infrastructure plan placeholder while the algorithm runs asynchronously.
 */
public class CreatePendingInfrastructurePlanService implements CreatePendingInfrastructurePlanUseCase {

    private static final String ERR_MAX_BUDGET = "Maximum budget is required to create a pending infrastructure plan";
    private static final String ERR_NUMBER_OF_DAYS = "Number of days is required to create a pending infrastructure plan";
    private static final String ERR_AVERAGE_PICKUP = "Average pickup time is required to create a pending infrastructure plan";

    private final InfrastructurePlanRepository infrastructurePlanRepository;

    /**
     * Creates the service.
     *
     * @param infrastructurePlanRepository repository used to persist the placeholder plan
     */
    public CreatePendingInfrastructurePlanService(InfrastructurePlanRepository infrastructurePlanRepository) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * Creates and stores an infrastructure plan in {@code RUNNING} state with request metadata only.
     *
     * @param numberOfDays               planning horizon from the client request
     * @param averagePickupTimeMinutes   average pickup time from the client request
     * @param maxBudget                  maximum budget from the client request (required)
     * @param executionRequestJson       JSON snapshot of the client execution request
     * @return the persisted placeholder plan
     */
    @Override
    public InfrastructurePlan createPending(
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            MaximumBudget maxBudget,
            AlgorithmJsonPayload executionRequestJson) {
        if (numberOfDays == null) {
            throw new IllegalArgumentException(ERR_NUMBER_OF_DAYS);
        }
        if (averagePickupTimeMinutes == null) {
            throw new IllegalArgumentException(ERR_AVERAGE_PICKUP);
        }
        if (maxBudget == null) {
            throw new IllegalArgumentException(ERR_MAX_BUDGET);
        }
        PlanningPeriod period = new PlanningPeriod(String.valueOf(ZonedDateTime.now(ZoneOffset.UTC).getYear()));
        ExecutedAt startedAt = new ExecutedAt(ZonedDateTime.now(ZoneOffset.UTC).toString());
        InfrastructurePlan plan = new InfrastructurePlan(
                period,
                maxBudget,
                null,
                numberOfDays,
                averagePickupTimeMinutes,
                startedAt,
                InfrastructurePlanValidityState.RUNNING,
                InfrastructurePlanExecutionState.RUNNING);
        plan.markExecutionRunning();
        if (executionRequestJson != null) {
            plan.assignExecutionRequestSnapshot(executionRequestJson.getJson());
        }
        return this.infrastructurePlanRepository.save(plan);
    }
}
