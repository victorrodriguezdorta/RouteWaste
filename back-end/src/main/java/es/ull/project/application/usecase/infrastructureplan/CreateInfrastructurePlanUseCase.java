package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;

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
     * @return the created infrastructure plan
     */
    InfrastructurePlan create(PlanningPeriod period, MaximumBudget maxBudget, ServicePolicies servicePolicies);
}
