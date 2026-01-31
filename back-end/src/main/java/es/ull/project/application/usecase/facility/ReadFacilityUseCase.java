package es.ull.project.application.usecase.facility;

import es.ull.project.domain.entity.Facility;
import java.util.List;
import java.util.UUID;

/**
 * Use case for reading facilities.
 */
public interface ReadFacilityUseCase {
    /**
     * Retrieves a facility by its unique identifier.
     *
     * @param id the unique identifier of the facility
     * @return the facility
     */
    Facility fetch(UUID id);

    /**
     * Retrieves all facilities.
     *
     * @return a list of all facilities
     */
    List<Facility> fetchAll();
}
