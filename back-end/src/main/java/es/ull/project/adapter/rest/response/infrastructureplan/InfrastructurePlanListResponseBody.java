package es.ull.project.adapter.rest.response.infrastructureplan;

import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.time.ExecutedAt;
import java.util.UUID;

/**
 * Data Transfer Object representing the lightweight InfrastructurePlan list response.
 * This class exposes only the fields required to render the list view.
 */
public class InfrastructurePlanListResponseBody {

    /**
     * Unique identifier of the infrastructure plan.
     */
    public UUID id;

    /**
     * Timestamp when the plan was executed.
     */
    public ExecutedAt executedAt;

    /**
     * Estimated total cost of the plan.
     */
    public TotalCost estimatedTotalCost;

    /**
     * Number of days covered by the plan.
     */
    public NumberOfDays numberOfDays;

    /**
     * Average pickup time in minutes.
     */
    public AveragePickupTimeMinutes averagePickupTimeMinutes;

    /**
     * Whether the plan is still valid or obsolete because referenced entities were edited.
     */
    public InfrastructurePlanValidityState validityState;

    /**
     * Lifecycle state of the algorithm execution (RUNNING, COMPLETED, FAILED).
     */
    public InfrastructurePlanExecutionState executionState;

    /**
     * Optional failure description when executionState is FAILED.
     */
    public String failureReason;
}
