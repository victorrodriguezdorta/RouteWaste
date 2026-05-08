package es.ull.project.application.usecase.infrastructureplan;

import es.ull.project.domain.entity.InfrastructurePlan;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Retrieves infrastructure plans using pagination.
     *
     * @param pageable pagination and sort information
     * @return a page of infrastructure plans
     */
    Page<InfrastructurePlan> fetchAll(Pageable pageable);

    /**
     * Retrieves all infrastructure plans.
     *
     * @return a list of all infrastructure plans
     */
    List<InfrastructurePlan> fetchAll();
}
