package es.ull.project.application.usecase.serviceassignment;

import es.ull.project.domain.entity.ServiceAssignment;

public interface CreateServiceAssignmentUseCase {
    ServiceAssignment create(ServiceAssignment serviceAssignment);
}
