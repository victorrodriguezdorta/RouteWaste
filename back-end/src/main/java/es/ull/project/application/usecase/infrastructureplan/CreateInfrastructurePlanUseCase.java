package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.entity.InfrastructurePlan;

public interface CreateInfrastructurePlanUseCase {
    InfrastructurePlan create(InfrastructurePlan infrastructurePlan);
}
