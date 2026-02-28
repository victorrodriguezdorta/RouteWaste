package es.ull.project.adapter.rest.request.infrastructureplan;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import es.ull.project.adapter.rest.deserialization.infrastructureplan.InfrastructurePlanPutRequestBodyDeserializer;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
@JsonDeserialize(using = InfrastructurePlanPutRequestBodyDeserializer.class)
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
     * List of facility IDs to be selected for this plan.
     * Optional field - defaults to empty list.
     */
    public List<UUID> selectedFacilityIds;

    /**
     * List of service assignment IDs to be included in this plan.
     * Optional field - defaults to empty list.
     */
    public List<UUID> serviceAssignmentIds;

    /**
     * Returns a string representation of this request body.
     * 
     * @return formatted string containing all attributes
     */
    @Override
    public String toString() {
        return String.format(
                "InfrastructurePlanPutRequestBody={period=%s, maxBudget=%s, servicePolicies=%s, selectedFacilityIds=%s, serviceAssignmentIds=%s}",
                this.period,
                this.maxBudget,
                this.servicePolicies,
                this.selectedFacilityIds != null ? this.selectedFacilityIds : Collections.emptyList(),
                this.serviceAssignmentIds != null ? this.serviceAssignmentIds : Collections.emptyList());
    }
}
