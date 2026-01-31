package es.ull.project.application.usecase.container;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;

/**
 * Use case for creating a new container.
 */
public interface CreateContainerUseCase {
    /**
     * Creates a new container with the specified attributes.
     *
     * @param location    the location of the container
     * @param wasteType   the type of waste the container handles
     * @param wasteDemand the waste demand of the container
     * @param serviceZone the service zone of the container
     * @return the created container
     */
    Container create(Location location, WasteType wasteType, WasteDemand wasteDemand, ServiceZone serviceZone);
}
