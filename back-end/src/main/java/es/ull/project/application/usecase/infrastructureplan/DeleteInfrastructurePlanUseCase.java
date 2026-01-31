package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.entity.InfrastructurePlan;
import java.util.UUID;

/**
 * Use case for deleting an infrastructure plan.
 */
public interface DeleteInfrastructurePlanUseCase {
    /**
     * Deletes an infrastructure plan by its identifier.
     *
     * @param id the unique identifier of the infrastructure plan to delete
     * @return the deleted infrastructure plan
     */
    InfrastructurePlan delete(UUID id);
}
