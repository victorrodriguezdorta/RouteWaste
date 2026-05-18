package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.valueobject.infrastructureplan.PlanReferencePresence;
import java.util.UUID;

/**
 * Use case for marking infrastructure plans obsolete after referenced entities are edited.
 */
public interface InvalidateInfrastructurePlansOnEntityEditUseCase {

    /**
     * Checks whether any infrastructure plan references the given entity.
     *
     * @param entityId facility, vehicle, or container identifier
     * @return value object indicating whether a plan references the entity
     */
    PlanReferencePresence existsAnyPlanReferencingEntity(UUID entityId);

    /**
     * Marks valid plans that reference the edited entity as obsolete.
     *
     * @param editedEntityId edited facility, vehicle, or container identifier
     */
    void invalidateValidPlansReferencingEntity(UUID editedEntityId);
}
