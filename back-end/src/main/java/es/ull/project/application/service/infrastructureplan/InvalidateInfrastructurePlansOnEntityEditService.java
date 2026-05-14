package es.ull.project.application.service.infrastructureplan;

import java.util.List;
import java.util.UUID;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.domain.entity.InfrastructurePlan;

/**
 * Marks infrastructure plans obsolete when a referenced master entity is edited.
 */
public class InvalidateInfrastructurePlansOnEntityEditService {

    private final InfrastructurePlanRepository infrastructurePlanRepository;

    /**
     * @param infrastructurePlanRepository repository used to query and update plans
     */
    public InvalidateInfrastructurePlansOnEntityEditService(InfrastructurePlanRepository infrastructurePlanRepository) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * Convenience check: whether any stored execution-request snapshot references the entity.
     *
     * @param entityId facility, vehicle, or container id
     * @return true if at least one plan snapshot contains the id
     */
    public boolean existsAnyPlanReferencingEntity(UUID entityId) {
        return this.infrastructurePlanRepository.existsAnyPlanReferencingEntityInExecutionRequest(entityId);
    }

    /**
     * Marks every still-valid plan whose stored execution request JSON references the entity as obsolete.
     *
     * @param editedEntityId id of the facility, vehicle, or container that was updated
     */
    public void invalidateValidPlansReferencingEntity(UUID editedEntityId) {
        List<InfrastructurePlan> affected = this.infrastructurePlanRepository
                .findValidPlansReferencingEntityInExecutionRequest(editedEntityId);
        for (InfrastructurePlan plan : affected) {
            plan.markObsoleteIfStillValid();
            this.infrastructurePlanRepository.save(plan);
        }
    }
}
