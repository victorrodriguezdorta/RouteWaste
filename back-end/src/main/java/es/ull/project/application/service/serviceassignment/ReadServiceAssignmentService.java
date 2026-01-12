package es.ull.project.application.service.serviceassignment;

import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.usecase.serviceassignment.ReadServiceAssignmentUseCase;
import es.ull.project.domain.entity.ServiceAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReadServiceAssignmentService implements ReadServiceAssignmentUseCase {

    @Autowired
    private ServiceAssignmentRepository repository;

    @Override
    public ServiceAssignment fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("ServiceAssignment not found"));
    }

    @Override
    public List<ServiceAssignment> fetchAll() {
        return this.repository.findAll();
    }
}
