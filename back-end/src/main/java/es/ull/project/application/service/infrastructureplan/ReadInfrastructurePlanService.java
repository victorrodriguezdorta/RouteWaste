package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.ReadInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReadInfrastructurePlanService implements ReadInfrastructurePlanUseCase {

    @Autowired
    private InfrastructurePlanRepository repository;

    @Override
    public InfrastructurePlan fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("InfrastructurePlan not found"));
    }

    @Override
    public List<InfrastructurePlan> fetchAll() {
        return this.repository.findAll();
    }
}
