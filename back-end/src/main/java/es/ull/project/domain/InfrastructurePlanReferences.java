package es.ull.project.domain;

import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.infrastructureplan.InfrastructurePlanFailureReason;
import es.ull.project.domain.valueobject.time.ExecutedAt;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Factory helpers for lightweight {@link InfrastructurePlan} references used by persistence adapters.
 */
public final class InfrastructurePlanReferences {

    private static final String UTILITY_CLASS_EXCEPTION_MESSAGE =
            "This is a utility class and cannot be instantiated.";

    /**
     * Prevents instantiation of this utility class.
     */
    private InfrastructurePlanReferences() {
        throw new UnsupportedOperationException(UTILITY_CLASS_EXCEPTION_MESSAGE);
    }

    /**
     * Minimal aggregate reference used when loading nested documents (for example a {@link DailyPlan})
     * where only the infrastructure plan identifier is required.
     *
     * @param infrastructurePlanId parent plan id
     * @return placeholder plan carrying only the id and default scalar placeholders
     */
    public static InfrastructurePlan forIdReferenceOnly(UUID infrastructurePlanId) {
        return new InfrastructurePlan(
                infrastructurePlanId,
                new PlanningPeriod(String.valueOf(LocalDate.now().getYear())),
                null,
                new MaximumBudget(1.0),
                null,
                null,
                null,
                InfrastructurePlanValidityState.VALID,
                InfrastructurePlanExecutionState.COMPLETED,
                null,
                null);
    }

    /**
     * Lightweight aggregate for paginated list reads without hydrating related entities.
     *
     * @param id                       plan identifier
     * @param estimatedTotalCost       persisted estimated total cost, may be null while running
     * @param numberOfDays             planning horizon length in days
     * @param averagePickupTimeMinutes average pickup time in minutes
     * @param executedAt               algorithm execution timestamp
     * @param validityState            persisted validity state
     * @param executionState           persisted execution lifecycle state
     * @param failureReason            optional failure description
     * @return infrastructure plan carrying only list-summary fields
     */
    public static InfrastructurePlan forListSummary(
            UUID id,
            TotalCost estimatedTotalCost,
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            ExecutedAt executedAt,
            InfrastructurePlanValidityState validityState,
            InfrastructurePlanExecutionState executionState,
            String failureReason) {
        PlanningPeriod placeholderPeriod = new PlanningPeriod(String.valueOf(LocalDate.now().getYear()));
        MaximumBudget placeholderBudget = new MaximumBudget(0.0);
        InfrastructurePlan plan = new InfrastructurePlan(
                id,
                placeholderPeriod,
                null,
                placeholderBudget,
                numberOfDays,
                averagePickupTimeMinutes,
                executedAt,
                validityState,
                executionState,
                InfrastructurePlanFailureReason.fromNullable(failureReason),
                null);
        plan.restoreComputedMetrics(estimatedTotalCost, null, null, null);
        return plan;
    }
}
