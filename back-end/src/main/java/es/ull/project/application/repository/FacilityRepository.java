package es.ull.project.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import es.ull.project.domain.entity.Facility;

/**
 * Repository interface for the Facility aggregate.
 *
 * Provides an abstraction to persist and retrieve Facility entities
 * so that application services are not coupled to any particular
 * storage implementation.
 */
public interface FacilityRepository {

    /**
     * Delete a facility from the repository.
     *
     * @param entity Facility to remove
     */
    public abstract void delete(Facility entity);

    /**
     * Fetch all facilities (domain-preferred name).
     *
     * @return list of facilities
     */
    public abstract List<Facility> fetchAll();

    /**
     * Find all facilities (alias for service compatibility).
     *
     * @return list of facilities
     */
    public abstract List<Facility> findAll();

    /**
     * Save or update a facility.
     *
     * @param entity Facility to persist
     * @return persisted Facility
     */
    public abstract Facility save(Facility entity);

    /**
     * Find a facility by its UUID.
     *
     * @param id facility UUID
     * @return optional containing the facility when present
     */
    public abstract Optional<Facility> findById(UUID id);
}
