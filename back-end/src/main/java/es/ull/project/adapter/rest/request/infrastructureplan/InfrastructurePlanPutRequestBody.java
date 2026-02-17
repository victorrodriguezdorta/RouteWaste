package es.ull.project.adapter.rest.request.infrastructureplan;

import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

/**
 * InfrastructurePlanPutRequestBody
 * 
 * Data Transfer Object representing the request body for updating an existing InfrastructurePlan.
 * This DTO is used in PUT requests to the infrastructure plan endpoint.
 * 
 * Public attributes are used to allow direct access without getters/setters,
 * reducing complexity and facilitating serialization/deserialization in the
 * REST API context. As a DTO designed exclusively for data transfer, it does
 * not require encapsulation like domain entities.
 * 
 * This class contains no business logic, only data representation.
 */
public class InfrastructurePlanPutRequestBody {

    /**
     * Planning period for the infrastructure plan.
     * Required field.
     */
    public PlanningPeriod period;

    /**
     * Maximum budget allowed for the plan.
     * Required field.
     */
    public MaximumBudget maxBudget;

    /**
     * Service policies to comply with.
     * Required field.
     */
    public ServicePolicies servicePolicies;

    /**
     * Returns a string representation of this request body.
     * 
     * @return formatted string containing all attributes
     */
    @Override
    public String toString() {
        return String.format(
                "InfrastructurePlanPutRequestBody={period=%s, maxBudget=%s, servicePolicies=%s}",
                this.period,
                this.maxBudget,
                this.servicePolicies);
    }
}
