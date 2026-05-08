package es.ull.project.adapter.rest.response.infrastructureplan;

import es.ull.project.adapter.rest.response.dailyplan.DailyPlanResponseBody;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.adapter.rest.response.serviceassignment.ServiceAssignmentResponseBody;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing an InfrastructurePlan response
 * This class is used to send InfrastructurePlan data in HTTP responses
 * Uses domain value objects directly and includes complete facility and container information
 */
public class InfrastructurePlanResponseBody {

    /**
     * Unique identifier of the infrastructure plan
     */
    public UUID id;

    /**
     * Planning period value object
     */
    public PlanningPeriod period;

    /**
     * Selected facilities with complete information
     */
    public List<FacilityResponseBody> selectedFacilities;

    /**
     * Service assignments with complete information
     */
    public List<ServiceAssignmentResponseBody> serviceAssignments;

    /**
     * Maximum budget allowed for the plan (value object)
     */
    public MaximumBudget maxBudget;

    /**
     * Estimated total cost of the plan (value object)
     */
    public TotalCost estimatedTotalCost;

    /**
     * Service policies to comply with (value object)
     */
    public ServicePolicies servicePolicies;

    /**
     * Daily plans (routes) associated with this infrastructure plan
     */
    public List<DailyPlanResponseBody> dailyPlans;

    /**
     * Total collected kilograms across all routes
     */
    public double totalCollectedKilograms;

    /**
     * Total collected liters across all routes
     */
    public double totalCollectedLiters;

    /**
     * Total distance in meters across all routes
     */
    public double totalDistanceMeters;
    /**
     * Number of days for the planning period
     */
    public Integer numberOfDays;

    /**
     * Average pickup time in minutes
     */
    public Integer averagePickupTimeMinutes;

    /**
     * Timestamp when the algorithm execution was performed (ISO 8601)
     */
    public String executedAt;

    /**
     * Computed or stored status for the plan (e.g. SUBOPTIMAL, OPTIMAL)
     */
    public String status;
}
