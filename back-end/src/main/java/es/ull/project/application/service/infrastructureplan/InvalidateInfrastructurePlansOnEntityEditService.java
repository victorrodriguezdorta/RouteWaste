package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.InvalidateInfrastructurePlansOnEntityEditUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.infrastructureplan.PlanReferencePresence;
import java.util.List;
import java.util.UUID;

/**
 * Marks infrastructure plans obsolete when a referenced master entity is edited.
 */
public class InvalidateInfrastructurePlansOnEntityEditService implements InvalidateInfrastructurePlansOnEntityEditUseCase {

    private final InfrastructurePlanRepository infrastructurePlanRepository;

    /**
     * Creates the service that invalidates infrastructure plans after entity edits.
     *
     * @param infrastructurePlanRepository repository used to query and update plans
     */
    public InvalidateInfrastructurePlansOnEntityEditService(InfrastructurePlanRepository infrastructurePlanRepository) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * Convenience check: whether any plan references the entity in any persisted association.
     *
     * @param entityId facility, vehicle, or container id
     * @return value object indicating whether at least one plan is linked to that id
     */
    @Override
    public PlanReferencePresence existsAnyPlanReferencingEntity(UUID entityId) {
        return new PlanReferencePresence(
                this.infrastructurePlanRepository.existsAnyPlanReferencingEntityInExecutionRequest(entityId));
    }

    /**
     * Marks every still-valid plan that references the entity (snapshot JSON, selected facilities,
     * service assignments, or daily plans) as obsolete.
     *
     * @param editedEntityId id of the facility, vehicle, or container that was updated
     */
    @Override
    public void invalidateValidPlansReferencingEntity(UUID editedEntityId) {
        List<InfrastructurePlan> affected = this.infrastructurePlanRepository
                .findValidPlansReferencingEntityInExecutionRequest(editedEntityId);
        for (InfrastructurePlan plan : affected) {
            plan.markObsoleteIfStillValid();
            this.infrastructurePlanRepository.save(plan);
        }
    }
}
