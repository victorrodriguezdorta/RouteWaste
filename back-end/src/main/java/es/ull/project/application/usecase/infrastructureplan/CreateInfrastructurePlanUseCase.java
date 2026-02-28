package es.ull.project.application.usecase.infrastructureplan;

import java.util.List;
import java.util.UUID;

import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

/**
 * Use case for creating a new infrastructure plan.
 */
public interface CreateInfrastructurePlanUseCase {
    /**
     * Creates a new infrastructure plan with the specified attributes.
     *
     * @param period          the planning period
     * @param maxBudget       the maximum budget
     * @param servicePolicies the service policies
     * @param selectedFacilityIds list of facility IDs to include in the plan
     * @param serviceAssignmentIds list of service assignment IDs to include in the plan
     * @return the created infrastructure plan
     */
    InfrastructurePlan create(
        PlanningPeriod period, 
        MaximumBudget maxBudget, 
        ServicePolicies servicePolicies,
        List<UUID> selectedFacilityIds,
        List<UUID> serviceAssignmentIds
    );
}
