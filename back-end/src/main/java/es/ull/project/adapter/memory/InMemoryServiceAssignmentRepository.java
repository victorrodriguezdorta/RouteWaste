package es.ull.project.adapter.memory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.domain.entity.ServiceAssignment;

/**
 * In-memory ServiceAssignmentRepository for tests and local runs.
 */
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
        store.remove(entity.getId());
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
        store.put(entity.getId(), entity);
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

    /**
     * Finds all service assignments that reference a specific container.
     *
     * @param containerId the container UUID
     * @return list of service assignments using this container
     */
    @Override
    public List<ServiceAssignment> findByContainerId(UUID containerId) {
        if (containerId == null) {
            return List.of();
        }
        return store.values().stream()
                .filter(sa -> sa.getContainer() != null && containerId.equals(sa.getContainer().getId()))
                .toList();
    }

    /**
     * Finds all service assignments that reference a specific facility.
     *
     * @param facilityId the facility UUID
     * @return list of service assignments using this facility
     */
    @Override
    public List<ServiceAssignment> findByFacilityId(UUID facilityId) {
        if (facilityId == null) {
            return List.of();
        }
        return store.values().stream()
                .filter(sa -> sa.getFacility() != null && facilityId.equals(sa.getFacility().getId()))
                .toList();
    }

    /**
     * Finds multiple service assignments by their identifiers (batch operation).
     *
     * @param ids collection of service assignment UUIDs
     * @return list of found service assignments
     */
    @Override
    public List<ServiceAssignment> findAllById(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream()
                .map(store::get)
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
