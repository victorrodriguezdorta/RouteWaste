package es.ull.project.application.usecase.facility;

import es.ull.project.domain.entity.Facility;

import java.util.UUID;

/**
 * Use case interface for deleting facilities.
 *
 * @see Facility
 */
public interface DeleteFacilityUseCase {

    /**
     * Deletes a facility by its unique identifier.
     *
     * @param id the unique identifier of the facility to delete
     * @return the deleted facility
     */
    Facility delete(UUID id);
}
