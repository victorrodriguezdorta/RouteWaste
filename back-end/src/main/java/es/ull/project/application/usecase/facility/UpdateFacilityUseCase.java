package es.ull.project.application.usecase.facility;

import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
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
     * @param newName the new facility name, or null to keep current
     * @param newFacilityType the new facility type, or null to keep current
     * @param newLocation the new location, or null to keep current
     * @param newStorageCapacity the new storage capacity in kilograms, or null to keep current
     * @param newProcessingCapacity the new processing capacity in kg/day, or null to keep current
     * @param newUnloadingTime the new unloading time in minutes, or null to keep current
     * @param newOpeningFixedCost the new opening cost, or null to keep current
     * @param newStatus the new status, or null to keep current
     * @return the updated facility
     */
    Facility update(
        UUID id,
        Name newName,
        FacilityType newFacilityType,
        Location newLocation,
        StorageCapacityKilograms newStorageCapacity,
        ProcessingCapacityKilogramsPerDay newProcessingCapacity,
        UnloadingTime newUnloadingTime,
        OpeningFixedCost newOpeningFixedCost,
        FacilityStatus newStatus
    );
}
