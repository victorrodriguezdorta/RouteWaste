package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.entity.InfrastructurePlan;
import java.util.List;
import java.util.UUID;

/**
 * Use case for reading infrastructure plans.
 */
public interface ReadInfrastructurePlanUseCase {
    /**
     * Retrieves an infrastructure plan by its unique identifier.
     *
     * @param id the unique identifier of the infrastructure plan
     * @return the infrastructure plan
     */
    InfrastructurePlan fetch(UUID id);

    /**
     * Retrieves all infrastructure plans.
     *
     * @return a list of all infrastructure plans
     */
    List<InfrastructurePlan> fetchAll();
}
