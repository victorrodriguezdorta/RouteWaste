package es.ull.project.application.service.facility;

import java.util.NoSuchElementException;
import java.util.UUID;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.usecase.facility.UpdateFacilityUseCase;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.location.Location;

/**
 * Service implementation for updating facilities.
 * This service handles the business logic for facility update operations.
 */
public class UpdateFacilityService implements UpdateFacilityUseCase {

    private final FacilityRepository repository;

    /**
     * Constructs a new UpdateFacilityService with the specified repository.
     * @param repository the facility repository for persistence operations
     */
    public UpdateFacilityService(FacilityRepository repository) {
        this.repository = repository;
    }

    /**
     * Updates an existing facility with new values.
     * @param id the unique identifier of the facility to update
     * @param newFacilityType the new facility type, or null to keep the existing value
     * @param newLocation the new location, or null to keep the existing value
     * @param newCapacity the new capacity, or null to keep the existing value
     * @param newOpeningFixedCost the new opening fixed cost, or null to keep the existing value
     * @param newStatus the new status, or null to keep the existing value
     * @return the updated facility
     * @throws NoSuchElementException if no facility is found with the given id
     */
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
