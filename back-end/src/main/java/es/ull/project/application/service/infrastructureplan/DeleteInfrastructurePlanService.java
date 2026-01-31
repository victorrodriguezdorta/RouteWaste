package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.DeleteInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service responsible for deleting infrastructure plans from the system.
 * This service implements the {@link DeleteInfrastructurePlanUseCase} interface and provides
 * the business logic for infrastructure plan deletion operations.

 */
public class DeleteInfrastructurePlanService implements DeleteInfrastructurePlanUseCase {

    private final InfrastructurePlanRepository repository;

    /**
     * Constructs a new DeleteInfrastructurePlanService with the specified repository.
     *
     * @param repository the infrastructure plan repository used for persistence operations
     */
    public DeleteInfrastructurePlanService(InfrastructurePlanRepository repository) {
        this.repository = repository;
    }

    /**
     * Deletes an infrastructure plan by its unique identifier.
     *
     * @param id the unique identifier of the infrastructure plan to delete
     * @return the deleted infrastructure plan
     * @throws NoSuchElementException if no infrastructure plan is found with the given identifier
     */
    @Override
    public InfrastructurePlan delete(UUID id) {
        InfrastructurePlan existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("InfrastructurePlan not found"));
        this.repository.delete(existing);
        return existing;
    }
}
