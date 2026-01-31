package es.ull.project.application.service.infrastructureplan;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.usecase.infrastructureplan.ReadInfrastructurePlanUseCase;
import es.ull.project.domain.entity.InfrastructurePlan;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service responsible for reading infrastructure plan data from the system.
 * This service implements the {@link ReadInfrastructurePlanUseCase} interface and provides
 * the business logic for infrastructure plan retrieval operations.

 */
public class ReadInfrastructurePlanService implements ReadInfrastructurePlanUseCase {

    private final InfrastructurePlanRepository repository;

    /**
     * Constructs a new ReadInfrastructurePlanService with the specified repository.
     *
     * @param repository the infrastructure plan repository used for persistence operations
     */
    public ReadInfrastructurePlanService(InfrastructurePlanRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves an infrastructure plan by its unique identifier.
     *
     * @param id the unique identifier of the infrastructure plan to retrieve
     * @return the infrastructure plan with the specified identifier
     * @throws NoSuchElementException if no infrastructure plan is found with the given identifier
     */
    @Override
    public InfrastructurePlan fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("InfrastructurePlan not found"));
    }

    /**
     * Retrieves all infrastructure plans from the system.
     *
     * @return a list containing all infrastructure plans
     */
    @Override
    public List<InfrastructurePlan> fetchAll() {
        return this.repository.findAll();
    }
}
