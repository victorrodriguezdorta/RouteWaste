package es.ull.project.application.usecase.container;

import es.ull.project.application.message.BulkCreateOutcome;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.util.List;

/**
 * Use case for creating multiple containers in a single operation.
 */
public interface BulkCreateContainersUseCase {

    /**
     * Creates containers from the provided attribute tuples.
     *
     * @param names                    container names (same order as other lists)
     * @param locations                container locations
     * @param wasteTypes               waste types
     * @param capacityLitersList       capacities in liters
     * @param dailyDemandLitersPerDay  daily demands
     * @param serviceZones             optional service zones (nullable entries allowed)
     * @return outcome with created and failed counts
     */
    BulkCreateOutcome createAll(
            List<Name> names,
            List<Location> locations,
            List<WasteType> wasteTypes,
            List<ContainerCapacityLiters> capacityLitersList,
            List<DailyWasteDemandLitersPerDay> dailyDemandLitersPerDay,
            List<ServiceZone> serviceZones);
}
