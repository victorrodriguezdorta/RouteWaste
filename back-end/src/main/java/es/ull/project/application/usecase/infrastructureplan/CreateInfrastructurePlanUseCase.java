package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;

public interface CreateInfrastructurePlanUseCase {
    InfrastructurePlan create(PlanningPeriod period, MaximumBudget maxBudget, ServicePolicies servicePolicies);
}
