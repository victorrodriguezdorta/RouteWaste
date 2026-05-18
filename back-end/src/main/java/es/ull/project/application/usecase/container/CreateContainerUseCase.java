package es.ull.project.application.usecase.container;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;

/**
 * Use case for creating a new container.
 */
public interface CreateContainerUseCase {
    /**
     * Creates a new container with the specified attributes.
     *
     * @param name        the container name
     * @param location    the location of the container
     * @param wasteType   the type of waste the container handles
     * @param capacityLiters the container capacity in liters
     * @param dailyDemandLitersPerDay the daily waste demand in liters per day
     * @param serviceZone the service zone of the container
     * @return the created container
     */
    Container create(Name name, Location location, WasteType wasteType, ContainerCapacityLiters capacityLiters, DailyWasteDemandLitersPerDay dailyDemandLitersPerDay, ServiceZone serviceZone);
}
