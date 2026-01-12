package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.DeleteInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DeleteInfrastructurePlanService implements DeleteInfrastructurePlanUseCase {

    @Autowired
    private InfrastructurePlanRepository repository;

    @Override
    public InfrastructurePlan delete(UUID id) {
        InfrastructurePlan existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("InfrastructurePlan not found"));
        this.repository.delete(existing);
        return existing;
    }
}
