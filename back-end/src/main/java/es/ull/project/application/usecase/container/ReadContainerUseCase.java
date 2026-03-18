package es.ull.project.application.usecase.container;

import java.util.List;
import java.util.UUID;

import es.ull.project.domain.entity.Container;

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
}
