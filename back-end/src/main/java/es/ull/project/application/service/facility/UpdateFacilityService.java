package es.ull.project.application.service.facility;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.usecase.facility.UpdateFacilityUseCase;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UpdateFacilityService implements UpdateFacilityUseCase {

    @Autowired
    private FacilityRepository repository;

    @Override
    public Facility update(UUID id, FacilityType newFacilityType, Location newLocation, Capacity newCapacity, OpeningFixedCost newOpeningFixedCost, FacilityStatus newStatus) {
        Facility existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Facility not found"));

        if (newFacilityType != null) {
            existing.updateFacilityType(newFacilityType);
        }
        if (newLocation != null) {
            existing.updateLocation(newLocation);
        }
        if (newCapacity != null) {
            existing.updateCapacity(newCapacity);
        }
        if (newOpeningFixedCost != null) {
            existing.updateOpeningFixedCost(newOpeningFixedCost);
        }
        if (newStatus != null) {
            existing.updateStatus(newStatus);
        }

        Facility saved = this.repository.save(existing);
        return saved;
    }
}
