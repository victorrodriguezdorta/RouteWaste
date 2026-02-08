package es.ull.project.application.repository;

import es.ull.project.domain.entity.Container;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for the Container aggregate.
 *
 * The repository defines an abstraction for storing and retrieving 
 * Container entities. Use this interface from application services
 * (use cases) to decouple business logic from persistence details.
 */
public interface ContainerRepository {

    /**
     * Delete a container from the repository.
     *
     * @param entity Container to remove
     */
    public abstract void delete(Container entity);

    /**
     * Fetch all containers using the application's preferred naming.
     *
     * @return list of all containers
     */
    public abstract List<Container> fetchAll();

    /**
     * Find all containers (alias commonly used by services).
     *
     * @return list of all containers
     */
    public abstract List<Container> findAll();

    /**
     * Save or update a container.
     *
     * @param entity Container to persist
     * @return persisted Container instance
     */
    public abstract Container save(Container entity);

    /**
     * Find a container by its identifier.
     *
     * @param id container UUID
     * @return optional containing the container when found
     */
    public abstract Optional<Container> findById(UUID id);
}
