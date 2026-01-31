package es.ull.project.application.usecase.facility;

import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.location.Location;

import java.util.UUID;

/**
 * Use case interface for updating facilities.
 *
 * @see Facility
 */
public interface UpdateFacilityUseCase {

    /**
     * Updates an existing facility with the specified parameters.
     *
     * @param id the unique identifier of the facility to update
     * @param newFacilityType the new facility type, or null to keep current
     * @param newLocation the new location, or null to keep current
     * @param newCapacity the new capacity, or null to keep current
     * @param newOpeningFixedCost the new opening cost, or null to keep current
     * @param newStatus the new status, or null to keep current
     * @return the updated facility
     */
    Facility update(UUID id, FacilityType newFacilityType, Location newLocation, Capacity newCapacity, OpeningFixedCost newOpeningFixedCost, FacilityStatus newStatus);
}
