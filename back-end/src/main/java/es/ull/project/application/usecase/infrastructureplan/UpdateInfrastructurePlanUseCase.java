package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.util.UUID;

/**
 * Use case for updating an infrastructure plan.
 */
public interface UpdateInfrastructurePlanUseCase {
    /**
     * Updates an existing infrastructure plan with the specified attributes.
     *
     * @param id                  the unique identifier of the infrastructure plan
     * @param newPeriod           the new planning period
     * @param newMaxBudget        the new maximum budget
     * @param newServicePolicies  the new service policies
     * @return the updated infrastructure plan
     */
    InfrastructurePlan update(UUID id, PlanningPeriod newPeriod, MaximumBudget newMaxBudget, ServicePolicies newServicePolicies);
}
