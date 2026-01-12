package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.UpdateInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UpdateInfrastructurePlanService implements UpdateInfrastructurePlanUseCase {

    @Autowired
    private InfrastructurePlanRepository repository;

    @Override
    public InfrastructurePlan update(UUID id, PlanningPeriod newPeriod, MaximumBudget newMaxBudget, ServicePolicies newServicePolicies) {
        InfrastructurePlan existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("InfrastructurePlan not found"));

        if (newPeriod != null) {
            existing.updatePeriod(newPeriod);
        }
        if (newMaxBudget != null) {
            existing.updateMaxBudget(newMaxBudget);
        }
        if (newServicePolicies != null) {
            existing.updateServicePolicies(newServicePolicies);
        }

        InfrastructurePlan saved = this.repository.save(existing);
        return saved;
    }
}
