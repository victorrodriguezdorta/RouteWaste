package es.ull.project.application.service.facility;

import es.ull.project.application.common.EntityTypeBreakdownBuilder;
import es.ull.project.application.query.FacilitySearchCriteria;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.usecase.facility.ReadFacilityUseCase;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.readmodel.EntityTypeBreakdown;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

/**
 * Service implementation for reading facilities.
 * This service handles the business logic for facility retrieval operations.
 */
public class ReadFacilityService implements ReadFacilityUseCase {

    private final FacilityRepository repository;

    /**
     * Constructs a new ReadFacilityService with the specified repository.
     * @param repository the facility repository for persistence operations
     */
    public ReadFacilityService(FacilityRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetches a facility by its unique identifier.
     * @param id the unique identifier of the facility to fetch
     * @return the facility with the specified identifier
     * @throws NoSuchElementException if no facility is found with the given id
     */
    @Override
    public Facility fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Facility not found"));
    }

    /**
     * Fetches all facilities from the repository.
     * @return a list of all facilities
     */
    @Override
    public List<Facility> fetchAll() {
        return this.repository.findAll();
    }

    /**
     * Fetches facilities from the repository using pagination.
     *
     * @param pageable pagination information
     * @return a page of facilities
     */
    @Override
    public Page<Facility> fetchAll(@NonNull Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    /**
     * Fetches facilities from the repository using pagination and an optional facility type filter.
     *
     * @param pageable pagination and sort information
     * @param facilityType optional facility type filter
     * @return a page of matching facilities
     */
    @Override
    public Page<Facility> fetchAll(@NonNull Pageable pageable, FacilityType facilityType) {
        return this.repository.findAll(pageable, facilityType, null);
    }

    /**
     * Fetches facilities from the repository with advanced search criteria and pagination.
     * Supports filtering by multiple attributes dynamically.
     *
     * @param pageable pagination and sort information
     * @param criteria search criteria with optional filters
     * @return a page of matching facilities
     */
    @Override
    public Page<Facility> fetchAll(@NonNull Pageable pageable, @NonNull FacilitySearchCriteria criteria) {
        return this.repository.findAll(pageable, criteria);
    }

    /**
     * Returns global facility statistics (total and per {@link FacilityType}).
     *
     * @return unfiltered type breakdown
     */
    @Override
    public EntityTypeBreakdown fetchStatistics() {
        return EntityTypeBreakdownBuilder.fromCounts(
                this.repository.count(),
                this.repository.countByFacilityType(),
                FacilityType.class);
    }
}
