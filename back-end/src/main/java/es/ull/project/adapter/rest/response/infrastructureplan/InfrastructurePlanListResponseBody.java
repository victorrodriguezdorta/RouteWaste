package es.ull.project.adapter.rest.response.infrastructureplan;

import java.util.UUID;

import es.ull.project.domain.valueobject.cost.TotalCost;

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
    public String executedAt;

    /**
     * Estimated total cost of the plan.
     */
    public TotalCost estimatedTotalCost;

    /**
     * Number of days covered by the plan.
     */
    public Integer numberOfDays;

    /**
     * Average pickup time in minutes.
     */
    public Integer averagePickupTimeMinutes;
}