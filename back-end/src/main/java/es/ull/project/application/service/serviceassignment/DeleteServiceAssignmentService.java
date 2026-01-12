package es.ull.project.application.service.serviceassignment;

import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.serviceassignment.DeleteServiceAssignmentUseCase;
import es.ull.project.domain.entity.ServiceAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DeleteServiceAssignmentService implements DeleteServiceAssignmentUseCase {

    @Autowired
    private ServiceAssignmentRepository repository;

    @Override
    public ServiceAssignment delete(UUID id) {
        ServiceAssignment existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("ServiceAssignment not found"));
        this.repository.delete(existing);
        return existing;
    }
}
