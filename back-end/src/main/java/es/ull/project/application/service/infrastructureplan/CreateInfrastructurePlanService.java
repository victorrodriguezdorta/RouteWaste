package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.CreateInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateInfrastructurePlanService implements CreateInfrastructurePlanUseCase {

    @Autowired
    private InfrastructurePlanRepository repository;

    @Override
    public InfrastructurePlan create(PlanningPeriod period, MaximumBudget maxBudget, ServicePolicies servicePolicies) {
        InfrastructurePlan plan = new InfrastructurePlan(period, maxBudget, servicePolicies);
        InfrastructurePlan saved = this.repository.save(plan);
        return saved;
    }
}
