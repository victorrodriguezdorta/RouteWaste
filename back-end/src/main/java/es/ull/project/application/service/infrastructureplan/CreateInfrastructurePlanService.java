package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.CreateInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

/**
 * Service responsible for creating new infrastructure plans in the system.
 * This service implements the {@link CreateInfrastructurePlanUseCase} interface and provides
 * the business logic for infrastructure plan creation operations.
 */
public class CreateInfrastructurePlanService implements CreateInfrastructurePlanUseCase {

    private final InfrastructurePlanRepository repository;

    /**
     * Constructs a new CreateInfrastructurePlanService with the specified repository.
     *
     * @param repository the infrastructure plan repository used for persistence operations
     */
    public CreateInfrastructurePlanService(InfrastructurePlanRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new infrastructure plan with the specified parameters.
     *
     * @param period the planning period for the infrastructure plan
     * @param maxBudget the maximum budget allocated for the plan
     * @param servicePolicies the service policies to be applied
     * @return the newly created and persisted infrastructure plan
     */
    @Override
    public InfrastructurePlan create(PlanningPeriod period, MaximumBudget maxBudget, ServicePolicies servicePolicies) {
        InfrastructurePlan plan = new InfrastructurePlan(period, maxBudget, servicePolicies);
        InfrastructurePlan saved = this.repository.save(plan);
        return saved;
    }
}
