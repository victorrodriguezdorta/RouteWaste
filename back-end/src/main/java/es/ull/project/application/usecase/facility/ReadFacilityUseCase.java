package es.ull.project.application.usecase.facility;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityType;

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

    /**
     * Retrieves facilities using pagination.
     *
     * @param pageable pagination information
     * @return a page of facilities
     */
    Page<Facility> fetchAll(Pageable pageable);

    /**
     * Retrieves facilities with pagination and an optional facility type filter.
     *
     * @param pageable pagination and sort information
     * @param facilityType optional facility type to filter by (null means no filter)
     * @return a page of facilities
     */
    Page<Facility> fetchAll(Pageable pageable, FacilityType facilityType);
}
