package es.ull.project.application.usecase.facility;

import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.enumerate.FacilityStatus;
import java.util.UUID;

public interface UpdateFacilityUseCase {
    Facility update(UUID id, FacilityType newFacilityType, Location newLocation, Capacity newCapacity, OpeningFixedCost newOpeningFixedCost, FacilityStatus newStatus);
}
