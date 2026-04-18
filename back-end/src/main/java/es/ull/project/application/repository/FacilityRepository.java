package es.ull.project.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import es.ull.project.adapter.mongodb.query.FacilitySearchCriteria;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;

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

    /**
     * Finds all facilities with pagination support.
     *
     * @param pageable the pagination information
     * @return a Page containing the paginated facilities
     */
    public abstract Page<Facility> findAll(@NonNull Pageable pageable);

    /**
     * Finds all facilities with pagination, type filter, and status filter support.
     *
     * @param pageable the pagination information
     * @param type optional facility type filter
     * @param status optional facility status filter
     * @return a Page containing the paginated and filtered facilities
     */
    public abstract Page<Facility> findAll(@NonNull Pageable pageable, FacilityType type, FacilityStatus status);

    /**
     * Finds all facilities with advanced search criteria and pagination.
     * Supports filtering by multiple attributes dynamically.
     *
     * @param pageable pagination and sort information
     * @param criteria search criteria with optional filters
     * @return page of matching facilities
     */
    public abstract Page<Facility> findAll(@NonNull Pageable pageable, @NonNull FacilitySearchCriteria criteria);
}
