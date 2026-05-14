package es.ull.project.application.service.facility;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.service.infrastructureplan.InvalidateInfrastructurePlansOnEntityEditService;
import es.ull.project.application.usecase.facility.UpdateFacilityUseCase;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service implementation for updating facilities.
 * This service handles the business logic for facility update operations.
 */
public class UpdateFacilityService implements UpdateFacilityUseCase {

    private final FacilityRepository repository;
    private final InvalidateInfrastructurePlansOnEntityEditService infrastructurePlanInvalidation;

    /**
     * Constructs a new UpdateFacilityService with the specified repository.
     * 
     * @param repository the facility repository for persistence operations
     * @param infrastructurePlanInvalidation service that marks affected infrastructure plans obsolete
     */
    public UpdateFacilityService(
            FacilityRepository repository,
            InvalidateInfrastructurePlansOnEntityEditService infrastructurePlanInvalidation) {
        this.repository = repository;
        this.infrastructurePlanInvalidation = infrastructurePlanInvalidation;
    }

    /**
     * Updates an existing facility with new values.
     * 
     * @param id                       the unique identifier of the facility to update
     * @param newFacilityType          the new facility type, or null to keep the existing value
     * @param newLocation              the new location, or null to keep the existing value
     * @param newStorageCapacity       the new storage capacity in kilograms, or null to keep the existing value
     * @param newProcessingCapacity    the new processing capacity in kg/day, or null to keep the existing value
     * @param newUnloadingTime         the new unloading time in minutes, or null to keep the existing value
     * @param newOpeningFixedCost      the new opening fixed cost, or null to keep the existing value
     * @param newStatus                the new status, or null to keep the existing value
     * @return the updated facility
     * @throws NoSuchElementException if no facility is found with the given id
     */
    @Override
    public Facility update(
        UUID id,
        Name newName,
        FacilityType newFacilityType,
        Location newLocation,
        StorageCapacityKilograms newStorageCapacity,
        ProcessingCapacityKilogramsPerDay newProcessingCapacity,
        UnloadingTime newUnloadingTime,
        OpeningFixedCost newOpeningFixedCost,
        FacilityStatus newStatus
    ) {
        Facility existing = this.repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Facility not found"));
        if (newName != null) {
            existing.updateName(newName);
        }
        if (newFacilityType != null) {
            existing.updateFacilityType(newFacilityType);
        }
        if (newLocation != null) {
            existing.updateLocation(newLocation);
        }
        if (newStorageCapacity != null) {
            existing.updateStorageCapacity(newStorageCapacity);
        }
        if (newProcessingCapacity != null) {
            existing.updateProcessingCapacity(newProcessingCapacity);
        }
        if (newUnloadingTime != null) {
            existing.updateUnloadingTime(newUnloadingTime);
        }
        if (newOpeningFixedCost != null) {
            existing.updateOpeningFixedCost(newOpeningFixedCost);
        }
        if (newStatus != null) {
            existing.updateStatus(newStatus);
        }
        Facility saved = this.repository.save(existing);
        this.infrastructurePlanInvalidation.invalidateValidPlansReferencingEntity(id);
        return saved;
    }
}
