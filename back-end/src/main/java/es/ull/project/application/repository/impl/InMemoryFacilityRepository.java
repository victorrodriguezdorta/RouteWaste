package es.ull.project.application.repository.impl;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.domain.entity.Facility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

/**
 * In-memory FacilityRepository for tests and local runs.
 */
@Repository
public class InMemoryFacilityRepository implements FacilityRepository {

    private final Map<UUID, Facility> store = new LinkedHashMap<>();

    /**
     * Deletes a facility from the repository.
     *
     * @param entity the facility to delete
     */
    @Override
    public void delete(Facility entity) {
        if (entity == null) {
            return;
        }
        store.remove(entity.getId());
    }

    /**
     * Fetches all facilities from the repository.
     *
     * @return a list of all facilities
     */
    @Override
    public List<Facility> fetchAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Finds all facilities in the repository.
     *
     * @return a list of all facilities
     */
    @Override
    public List<Facility> findAll() {
        return fetchAll();
    }

    /**
     * Saves a facility to the repository.
     *
     * @param entity the facility to save
     * @return the saved facility, or null if the entity was null
     */
    @Override
    public Facility save(Facility entity) {
        if (entity == null) {
            return null;
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    /**
     * Finds a facility by its unique identifier.
     *
     * @param id the unique identifier of the facility
     * @return an Optional containing the facility if found, or empty if not found
     */
    @Override
    public Optional<Facility> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
