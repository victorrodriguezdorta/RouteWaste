package es.ull.project.application.usecase.serviceassignment;

import es.ull.project.domain.entity.ServiceAssignment;
import java.util.List;
import java.util.UUID;

public interface ReadServiceAssignmentUseCase {
    ServiceAssignment fetch(UUID id);
    List<ServiceAssignment> fetchAll();
}
