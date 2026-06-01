package es.ull.project.adapter.rest.response.infrastructureplan;

import es.ull.project.adapter.rest.response.dailyplan.DailyPlanResponseBody;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.adapter.rest.response.serviceassignment.ServiceAssignmentResponseBody;
import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import es.ull.project.domain.enumerate.InfrastructurePlanStatus;
import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.ExecutedAt;
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
     * Container daily monitoring snapshots associated with this infrastructure plan.
     */
    public List<ContainerDailyStateResponseBody> containerStateMonitoring;

    /**
     * Total collected kilograms across all routes
     */
    public CollectedWeightKilograms totalCollectedKilograms;

    /**
     * Total collected liters across all routes
     */
    public CollectedVolumeLiters totalCollectedLiters;

    /**
     * Total distance in meters across all routes
     */
    public Distance totalDistanceMeters;
    /**
     * Number of days for the planning period
     */
    public NumberOfDays numberOfDays;

    /**
     * Average pickup time in minutes
     */
    public AveragePickupTimeMinutes averagePickupTimeMinutes;

    /**
     * Timestamp when the algorithm execution was performed (ISO 8601)
     */
    public ExecutedAt executedAt;

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

    /**
     * JSON snapshot of the client request used to execute the algorithm.
     */
    public AlgorithmJsonPayload executionRequestJson;

    /**
     * Computed or stored status for the plan (e.g. SUBOPTIMAL, OPTIMAL)
     */
    public InfrastructurePlanStatus status;
}
