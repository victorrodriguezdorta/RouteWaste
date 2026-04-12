package es.ull.project.application.usecase.facility;

import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.Capacity;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.location.Location;

/**
 * Use case for creating a new facility.
 */
public interface CreateFacilityUseCase {
    /**
     * Creates a new facility with the specified attributes.
     *
     * @param facilityType      the type of facility
     * @param location          the location of the facility
     * @param capacity          the capacity of the facility
     * @param openingFixedCost  the fixed cost to open the facility
     * @param status            the initial status of the facility
     * @return the created facility
     */
    Facility create(FacilityType facilityType, Location location, Capacity capacity, OpeningFixedCost openingFixedCost, FacilityStatus status);
}
