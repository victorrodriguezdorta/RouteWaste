package es.ull.project.application.repository;

import es.ull.project.domain.entity.ServiceAssignment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceAssignmentRepository {
    ServiceAssignment save(ServiceAssignment assignment);
    Optional<ServiceAssignment> findById(UUID id);
    List<ServiceAssignment> findAll();
    void delete(ServiceAssignment assignment);
}
