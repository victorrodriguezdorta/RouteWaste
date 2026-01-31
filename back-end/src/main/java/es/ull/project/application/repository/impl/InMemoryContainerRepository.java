package es.ull.project.application.repository.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.domain.entity.Container;

/**
 * Simple in-memory implementation of {@link ContainerRepository} used for tests
 * and local runs. Not intended for production.
 */
@Repository
public class InMemoryContainerRepository implements ContainerRepository {

    private final Map<UUID, Container> store = new LinkedHashMap<>();

    /**
     * Deletes the specified container from the repository.
     *
     * @param entity the container to delete
     */
    @Override
    public void delete(Container entity) {
        if (entity == null) {
            return;
        }
        store.remove(entity.getId());
    }

    /**
     * Retrieves all containers from the repository.
     *
     * @return a list of all containers
     */
    @Override
    public List<Container> fetchAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Finds all containers in the repository.
     *
     * @return a list of all containers
     */
    @Override
    public List<Container> findAll() {
        return fetchAll();
    }

    /**
     * Saves a container to the repository.
     *
     * @param entity the container to save
     * @return the saved container, or null if entity is null
     */
    @Override
    public Container save(Container entity) {
        if (entity == null) {
            return null;
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    /**
     * Finds a container by its unique identifier.
     *
     * @param id the unique identifier of the container
     * @return an Optional containing the container if found, or empty otherwise
     */
    @Override
    public Optional<Container> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
