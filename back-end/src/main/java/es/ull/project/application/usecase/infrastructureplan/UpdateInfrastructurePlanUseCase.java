package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.entity.InfrastructurePlan;
import java.util.UUID;

public interface UpdateInfrastructurePlanUseCase {
    InfrastructurePlan update(UUID id, InfrastructurePlan newInfrastructurePlan);
}
