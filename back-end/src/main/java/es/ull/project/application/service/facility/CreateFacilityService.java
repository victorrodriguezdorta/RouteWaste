package es.ull.project.application.service.facility;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.usecase.facility.CreateFacilityUseCase;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateFacilityService implements CreateFacilityUseCase {

    @Autowired
    private FacilityRepository repository;

    @Override
    public Facility create(FacilityType facilityType, Location location, Capacity capacity, OpeningFixedCost openingFixedCost, FacilityStatus status) {
        Facility newFacility = new Facility(facilityType, location, capacity, openingFixedCost, status);
        Facility saved = this.repository.save(newFacility);
        return saved;
    }
}
