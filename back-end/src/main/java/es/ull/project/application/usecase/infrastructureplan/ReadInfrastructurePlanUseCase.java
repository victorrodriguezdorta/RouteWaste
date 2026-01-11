package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.entity.InfrastructurePlan;
import java.util.List;
import java.util.UUID;

public interface ReadInfrastructurePlanUseCase {
    InfrastructurePlan fetch(UUID id);
    List<InfrastructurePlan> fetchAll();
}
