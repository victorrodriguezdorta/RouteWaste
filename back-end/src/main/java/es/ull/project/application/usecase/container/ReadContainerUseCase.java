package es.ull.project.application.usecase.container;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.WasteType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    Page<Container> fetchAll(Pageable pageable);

    /**
     * Retrieves containers with pagination and an optional waste type filter.
     *
     * @param pageable pagination and sort information
     * @param wasteType optional waste type to filter by (null means no filter)
     * @return a page of containers
     */
    Page<Container> fetchAll(Pageable pageable, WasteType wasteType);
}
