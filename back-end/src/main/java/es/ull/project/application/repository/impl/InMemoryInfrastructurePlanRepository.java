package es.ull.project.application.repository.impl;

import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.domain.entity.InfrastructurePlan;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

/**
 * In-memory InfrastructurePlanRepository for tests and local runs.
 */
@Repository
public class InMemoryInfrastructurePlanRepository implements InfrastructurePlanRepository {

    private final Map<UUID, InfrastructurePlan> store = new LinkedHashMap<>();

    /**
     * Deletes an infrastructure plan from the repository.
     *
     * @param entity the infrastructure plan to delete
     */
    @Override
    public void delete(InfrastructurePlan entity) {
        if (entity == null) {
            return;
        }
        store.remove(entity.getId());
    }

    /**
     * Fetches all infrastructure plans from the repository.
     *
     * @return a list of all infrastructure plans
     */
    @Override
    public List<InfrastructurePlan> fetchAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Finds all infrastructure plans in the repository.
     *
     * @return a list of all infrastructure plans
     */
    @Override
    public List<InfrastructurePlan> findAll() {
        return fetchAll();
    }

    /**
     * Saves an infrastructure plan to the repository.
     *
     * @param entity the infrastructure plan to save
     * @return the saved infrastructure plan, or null if the entity was null
     */
    @Override
    public InfrastructurePlan save(InfrastructurePlan entity) {
        if (entity == null) {
            return null;
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    /**
     * Finds an infrastructure plan by its unique identifier.
     *
     * @param id the unique identifier of the infrastructure plan
     * @return an Optional containing the infrastructure plan if found, or empty if not found
     */
    @Override
    public Optional<InfrastructurePlan> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
