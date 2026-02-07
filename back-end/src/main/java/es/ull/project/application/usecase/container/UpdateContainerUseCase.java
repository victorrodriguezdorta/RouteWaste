package es.ull.project.application.usecase.container;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;
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
     * @param newWasteDemand the new waste demand
     * @param newServiceZone the new service zone
     * @return the updated container
     */
    Container update(UUID id, Location newLocation, WasteType newWasteType, WasteDemand newWasteDemand, ServiceZone newServiceZone);
}
