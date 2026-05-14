package es.ull.project.application.service.container;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.service.infrastructureplan.DeleteInfrastructurePlansReferencingEntityService;
import es.ull.project.application.usecase.container.DeleteContainerUseCase;
import es.ull.project.domain.entity.Container;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service implementation for deleting containers.
 * This service handles the business logic for container deletion operations.
 */
public class DeleteContainerService implements DeleteContainerUseCase {

    private final ContainerRepository repository;
    private final DeleteInfrastructurePlansReferencingEntityService deleteInfrastructurePlansReferencingEntityService;

    /**
     * Constructs a new DeleteContainerService with the specified repository.
     * @param repository the container repository for persistence operations
     * @param deleteInfrastructurePlansReferencingEntityService removes plans that referenced this container in their execution snapshot
     */
    public DeleteContainerService(
            ContainerRepository repository,
            DeleteInfrastructurePlansReferencingEntityService deleteInfrastructurePlansReferencingEntityService) {
        this.repository = repository;
        this.deleteInfrastructurePlansReferencingEntityService = deleteInfrastructurePlansReferencingEntityService;
    }

    /**
     * Deletes a container by its unique identifier.
     * @param id the unique identifier of the container to delete
     * @return the deleted container
     * @throws NoSuchElementException if no container is found with the given id
     */
    @Override
    public Container delete(UUID id) {
        Container existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Container not found"));
        this.deleteInfrastructurePlansReferencingEntityService.deletePlansWhoseExecutionRequestReferencesEntity(id);
        this.repository.delete(existing);
        return existing;
    }
}
