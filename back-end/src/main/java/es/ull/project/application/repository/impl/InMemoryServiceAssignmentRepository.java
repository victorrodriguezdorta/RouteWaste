package es.ull.project.application.repository.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.domain.entity.ServiceAssignment;

/**
 * In-memory ServiceAssignmentRepository for tests and local runs.
 */
@Repository
public class InMemoryServiceAssignmentRepository implements ServiceAssignmentRepository {

    private final Map<UUID, ServiceAssignment> store = new LinkedHashMap<>();

    /**
     * Deletes the specified service assignment from the repository.
     *
     * @param entity the service assignment to delete
     */
    @Override
    public void delete(ServiceAssignment entity) {
        if (entity == null) {
            return;
        }
        store.remove(entity.getServiceAssignmentId());
    }

    /**
     * Retrieves all service assignments from the repository.
     *
     * @return a list of all service assignments
     */
    @Override
    public List<ServiceAssignment> fetchAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Finds all service assignments in the repository.
     *
     * @return a list of all service assignments
     */
    @Override
    public List<ServiceAssignment> findAll() {
        return fetchAll();
    }

    /**
     * Saves a service assignment to the repository.
     *
     * @param entity the service assignment to save
     * @return the saved service assignment, or null if entity is null
     */
    @Override
    public ServiceAssignment save(ServiceAssignment entity) {
        if (entity == null) {
            return null;
        }
        store.put(entity.getServiceAssignmentId(), entity);
        return entity;
    }

    /**
     * Finds a service assignment by its unique identifier.
     *
     * @param id the unique identifier of the service assignment
     * @return an Optional containing the service assignment if found, or empty otherwise
     */
    @Override
    public Optional<ServiceAssignment> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
