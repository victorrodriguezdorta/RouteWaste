package es.ull.project.application.usecase.container;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.util.UUID;

/**
 * Use case for updating a container.
 */
public interface UpdateContainerUseCase {
    /**
     * Updates an existing container with the specified attributes.
     *
     * @param id             the unique identifier of the container
     * @param newLocation    the new location
     * @param newWasteType   the new waste type
     * @param newCapacityLiters the new container capacity in liters
     * @param newDailyDemandLitersPerDay the new daily demand in liters per day
     * @param newServiceZone the new service zone
     * @return the updated container
     */
    Container update(UUID id, Name newName, Location newLocation, WasteType newWasteType, ContainerCapacityLiters newCapacityLiters, DailyWasteDemandLitersPerDay newDailyDemandLitersPerDay, ServiceZone newServiceZone);
}
