package es.ull.project.application.usecase.facility;

import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.enumerate.FacilityStatus;

public interface CreateFacilityUseCase {
    Facility create(FacilityType facilityType, Location location, Capacity capacity, OpeningFixedCost openingFixedCost, FacilityStatus status);
}
