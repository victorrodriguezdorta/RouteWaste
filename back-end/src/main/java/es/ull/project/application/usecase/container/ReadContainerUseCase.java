package es.ull.project.application.usecase.container;

import es.ull.project.application.repository.query.ContainerSearchCriteria;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.readmodel.EntityTypeBreakdown;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

/**
 * Use case for reading containers.
 */
public interface ReadContainerUseCase {
    /**
     * Retrieves a container by its unique identifier.
     *
     * @param id the unique identifier of the container
     * @return the container
     */
    Container fetch(UUID id);

    /**
     * Retrieves all containers.
     *
     * @return a list of all containers
     */
    List<Container> fetchAll();

    /**
     * Retrieves containers using pagination parameters.
     *
     * @param pageable pagination information
     * @return a page of containers
     */
    Page<Container> fetchAll(@NonNull Pageable pageable);

    /**
     * Retrieves containers with pagination and an optional waste type filter.
     *
     * @param pageable pagination and sort information
     * @param wasteType optional waste type to filter by (null means no filter)
     * @return a page of containers
     */
    Page<Container> fetchAll(@NonNull Pageable pageable, WasteType wasteType);

    /**
     * Retrieves containers with advanced search criteria and pagination.
     * Supports multiple filters (waste type, capacity, demand, location, etc.)
     *
     * @param pageable pagination and sort information
     * @param criteria search criteria with optional filters
     * @return a page of matching containers
     */
    Page<Container> fetchAll(@NonNull Pageable pageable, @NonNull ContainerSearchCriteria criteria);

    /**
     * Returns global container statistics: total count and count per {@link WasteType}.
     *
     * @return unfiltered type breakdown
     */
    EntityTypeBreakdown fetchStatistics();
}
