package es.ull.project.application.usecase.serviceassignment;

import es.ull.project.domain.entity.ServiceAssignment;
import java.util.UUID;

public interface UpdateServiceAssignmentUseCase {
    ServiceAssignment update(UUID id, ServiceAssignment newServiceAssignment);
}
