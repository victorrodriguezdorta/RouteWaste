package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.UpdateInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service responsible for updating existing infrastructure plans in the system.
 * This service implements the {@link UpdateInfrastructurePlanUseCase} interface and provides
 * the business logic for infrastructure plan modification operations.
 */
public class UpdateInfrastructurePlanService implements UpdateInfrastructurePlanUseCase {

    private final InfrastructurePlanRepository repository;

    /**
     * Constructs a new UpdateInfrastructurePlanService with the specified repository.
     *
     * @param repository the infrastructure plan repository used for persistence operations
     */
    public UpdateInfrastructurePlanService(InfrastructurePlanRepository repository) {
        this.repository = repository;
    }

    /**
     * Updates an existing infrastructure plan with the specified parameters.
     * <p>
     * Only non-null parameters will be updated. Null values are ignored,
     * allowing for partial updates.
     * </p>
     *
     * @param id the unique identifier of the infrastructure plan to update
     * @param newPeriod the new planning period, or null to keep the current value
     * @param newMaxBudget the new maximum budget, or null to keep the current value
     * @param newServicePolicies the new service policies, or null to keep the current value
     * @return the updated infrastructure plan
     * @throws NoSuchElementException if no infrastructure plan is found with the given identifier
     */
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
