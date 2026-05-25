package es.ull.project.application.service.container;

import es.ull.project.application.common.EntityTypeBreakdownBuilder;
import es.ull.project.application.query.ContainerSearchCriteria;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.usecase.container.ReadContainerUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.readmodel.EntityTypeBreakdown;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

/**
 * Service implementation for reading containers.
 * This service handles the business logic for container retrieval operations.
 */
public class ReadContainerService implements ReadContainerUseCase {

    private final ContainerRepository repository;

    /**
     * Constructs a new ReadContainerService with the specified repository.
     * @param repository the container repository for persistence operations
     */
    public ReadContainerService(ContainerRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetches a container by its unique identifier.
     * @param id the unique identifier of the container to fetch
     * @return the container with the specified identifier
     * @throws NoSuchElementException if no container is found with the given id
     */
    @Override
    public Container fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Container not found"));
    }

    /**
     * Fetches all containers from the repository.
     * @return a list of all containers
     */
    @Override
    public List<Container> fetchAll() {
        return this.repository.findAll();
    }

    /**
     * Fetches containers from the repository using pagination.
     *
     * @param pageable pagination information
     * @return a page of containers
     */
    @Override
    public Page<Container> fetchAll(@NonNull Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    /**
     * Fetches containers from the repository using pagination and an optional waste type filter.
     *
     * @param pageable pagination and sort information
     * @param wasteType optional waste type filter
     * @return a page of matching containers
     */
    @Override
    public Page<Container> fetchAll(@NonNull Pageable pageable, WasteType wasteType) {
        return this.repository.findAll(pageable, wasteType);
    }

    /**
     * Fetches containers from the repository using pagination and advanced search criteria.
     *
     * @param pageable pagination and sort information
     * @param criteria search criteria with optional filters
     * @return a page of matching containers
     */
    @Override
    public Page<Container> fetchAll(@NonNull Pageable pageable, @NonNull ContainerSearchCriteria criteria) {
        return this.repository.findAll(pageable, criteria);
    }

    /**
     * Returns global container statistics (total and per {@link WasteType}).
     *
     * @return unfiltered type breakdown
     */
    @Override
    public EntityTypeBreakdown fetchStatistics() {
        return EntityTypeBreakdownBuilder.fromCounts(
                this.repository.count(),
                this.repository.countByWasteType(),
                WasteType.class);
    }
}
