package es.ull.project.application.usecase.container;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.enumerate.ServiceZone;

public interface CreateContainerUseCase {
    Container create(Location location, WasteType wasteType, WasteDemand wasteDemand, ServiceZone serviceZone);
}
