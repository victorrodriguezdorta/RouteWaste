package es.ull.project.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.ull.project.domain.entity.Container;

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

    /**
     * Find multiple containers by their identifiers (batch operation).
     * This method is optimized for retrieving multiple entities at once,
     * reducing the number of database queries (solving N+1 problem).
     *
     * @param ids collection of container UUIDs
     * @return list of found containers (may be less than requested if some don't exist)
     */
    public abstract List<Container> findAllById(List<UUID> ids);
}
