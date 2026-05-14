package es.ull.project.application.service.infrastructureplan;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.DeleteInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;

/**
 * Removes infrastructure plans whose persisted snapshot (execution request JSON, selected facilities,
 * service assignments, or daily plans) references a deleted master entity.
 */
public class DeleteInfrastructurePlansReferencingEntityService {

    private final InfrastructurePlanRepository infrastructurePlanRepository;
    private final DeleteInfrastructurePlanUseCase deleteInfrastructurePlanUseCase;

    /**
     * @param infrastructurePlanRepository repository to find affected plans
     * @param deleteInfrastructurePlanUseCase use case that cascades daily plans and service assignments
     */
    public DeleteInfrastructurePlansReferencingEntityService(
            InfrastructurePlanRepository infrastructurePlanRepository,
            DeleteInfrastructurePlanUseCase deleteInfrastructurePlanUseCase) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
        this.deleteInfrastructurePlanUseCase = deleteInfrastructurePlanUseCase;
    }

    /**
     * Deletes every infrastructure plan that references the given entity id in any persisted association.
     * Should be invoked before deleting the facility, vehicle, or container from persistence.
     *
     * @param deletedEntityId id of the entity about to be removed
     */
    public void deletePlansWhoseExecutionRequestReferencesEntity(UUID deletedEntityId) {
        if (deletedEntityId == null) {
            return;
        }
        List<InfrastructurePlan> affected = this.infrastructurePlanRepository.findPlansReferencingEntityInExecutionRequest(deletedEntityId);
        Set<UUID> planIds = new LinkedHashSet<>();
        for (InfrastructurePlan plan : affected) {
            planIds.add(plan.getId());
        }
        for (UUID planId : new ArrayList<>(planIds)) {
            this.deleteInfrastructurePlanUseCase.delete(planId);
        }
    }
}
