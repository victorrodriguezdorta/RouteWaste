package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.domain.entity.InfrastructurePlan;

/**
 * Mapper class to convert InfrastructurePlan domain entities to InfrastructurePlanResponseBody DTOs
 * This class handles the transformation between the domain layer and the REST adapter layer
 */
public class InfrastructurePlanResponseMapper {

    private InfrastructurePlanResponseMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Converts an InfrastructurePlan domain entity to an InfrastructurePlanResponseBody DTO
     * Maps all the infrastructure plan properties including nested objects (budget, cost, policies)
     *
     * @param plan The InfrastructurePlan domain entity to convert
     * @return InfrastructurePlanResponseBody DTO ready to be serialized as JSON
     */
    public static InfrastructurePlanResponseBody toResponseBody(InfrastructurePlan plan) {
        InfrastructurePlanResponseBody responseBody = new InfrastructurePlanResponseBody();
        responseBody.id = plan.getId();
        responseBody.period = plan.getPeriod().getValue();
        responseBody.maxBudget = new InfrastructurePlanResponseBody.MaximumBudgetData();
        responseBody.maxBudget.amount = plan.getMaxBudget().getAmount();
        if (plan.getMaxBudget().getCurrency().isPresent()) {
            responseBody.maxBudget.currency = plan.getMaxBudget().getCurrency().get().getCode();
        }
        responseBody.estimatedTotalCost = new InfrastructurePlanResponseBody.TotalCostData();
        responseBody.estimatedTotalCost.amount = plan.getEstimatedTotalCost().getAmount();
        if (plan.getEstimatedTotalCost().getCurrency().isPresent()) {
            responseBody.estimatedTotalCost.currency = plan.getEstimatedTotalCost().getCurrency().get().getCode();
        }
        responseBody.servicePolicies = new InfrastructurePlanResponseBody.ServicePoliciesData();
        if (plan.getServicePolicies().getMaxServiceDistance().isPresent()) {
            responseBody.servicePolicies.maxServiceDistance = plan.getServicePolicies().getMaxServiceDistance().get();
        }
        if (plan.getServicePolicies().getMaxServiceTime().isPresent()) {
            responseBody.servicePolicies.maxServiceTime = plan.getServicePolicies().getMaxServiceTime().get();
        }
        if (plan.getServicePolicies().getMaxInfrastructureCount().isPresent()) {
            responseBody.servicePolicies.maxInfrastructureCount = plan.getServicePolicies().getMaxInfrastructureCount().get();
        }
        if (plan.getServicePolicies().getMaxEmissions().isPresent()) {
            responseBody.servicePolicies.maxEmissions = plan.getServicePolicies().getMaxEmissions().get();
        }
        return responseBody;
    }
}
