package es.ull.project.application.usecase.container;

import es.ull.project.domain.entity.Container;
import java.util.UUID;

/**
 * Use case for deleting a container.
 */
public interface DeleteContainerUseCase {
    /**
     * Deletes a container by its unique identifier.
     *
     * @param id the unique identifier of the container to delete
     * @return the deleted container
     */
    Container delete(UUID id);
}
