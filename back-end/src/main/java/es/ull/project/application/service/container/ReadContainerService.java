package es.ull.project.application.service.container;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.usecase.container.ReadContainerUseCase;
import es.ull.project.domain.entity.Container;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service implementation for reading containers.
 * This service handles the business logic for container retrieval operations.
 */
public class ReadContainerService implements ReadContainerUseCase {

    private final ContainerRepository repository;

    /**
     * Constructs a new ReadContainerService with the specified repository.
     * @param repository the container repository for persistence operations
     */
    public ReadContainerService(ContainerRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetches a container by its unique identifier.
     * @param id the unique identifier of the container to fetch
     * @return the container with the specified identifier
     * @throws NoSuchElementException if no container is found with the given id
     */
    @Override
    public Container fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Container not found"));
    }

    /**
     * Fetches all containers from the repository.
     * @return a list of all containers
     */
    @Override
    public List<Container> fetchAll() {
        return this.repository.findAll();
    }
}
