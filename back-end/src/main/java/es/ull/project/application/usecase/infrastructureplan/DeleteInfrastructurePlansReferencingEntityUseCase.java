package es.ull.project.application.usecase.infrastructureplan;

import java.util.UUID;

/**
 * Use case for deleting infrastructure plans that reference a deleted master entity.
 */
public interface DeleteInfrastructurePlansReferencingEntityUseCase {

    /**
     * Deletes every infrastructure plan that references the given entity.
     *
     * @param deletedEntityId deleted facility, vehicle, or container identifier
     */
    void deletePlansWhoseExecutionRequestReferencesEntity(UUID deletedEntityId);
}
