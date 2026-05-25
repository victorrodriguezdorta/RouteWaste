package es.ull.project.application.usecase.facility;

import es.ull.project.application.model.BulkCreateOutcome;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.util.List;

/**
 * Use case for creating multiple facilities in a single operation.
 */
public interface BulkCreateFacilitiesUseCase {

    /**
     * Creates facilities from the provided attribute tuples.
     *
     * @param names               facility names
     * @param facilityTypes       facility types
     * @param locations           locations
     * @param storageCapacities   storage capacities
     * @param processingCapacities processing capacities
     * @param unloadingTimes      unloading times
     * @param openingFixedCosts   opening fixed costs
     * @param statuses            facility statuses
     * @return outcome with created and failed counts
     */
    BulkCreateOutcome createAll(
            List<Name> names,
            List<FacilityType> facilityTypes,
            List<Location> locations,
            List<StorageCapacityKilograms> storageCapacities,
            List<ProcessingCapacityKilogramsPerDay> processingCapacities,
            List<UnloadingTime> unloadingTimes,
            List<OpeningFixedCost> openingFixedCosts,
            List<FacilityStatus> statuses);
}
