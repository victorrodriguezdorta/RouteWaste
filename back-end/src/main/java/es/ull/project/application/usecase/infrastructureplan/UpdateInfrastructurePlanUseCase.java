package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import java.util.UUID;

public interface UpdateInfrastructurePlanUseCase {
    InfrastructurePlan update(UUID id, PlanningPeriod newPeriod, MaximumBudget newMaxBudget, ServicePolicies newServicePolicies);
}
