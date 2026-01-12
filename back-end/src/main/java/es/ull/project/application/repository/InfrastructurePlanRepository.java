package es.ull.project.application.repository;

import es.ull.project.domain.entity.InfrastructurePlan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InfrastructurePlanRepository {
    InfrastructurePlan save(InfrastructurePlan plan);
    Optional<InfrastructurePlan> findById(UUID id);
    List<InfrastructurePlan> findAll();
    void delete(InfrastructurePlan plan);
}
