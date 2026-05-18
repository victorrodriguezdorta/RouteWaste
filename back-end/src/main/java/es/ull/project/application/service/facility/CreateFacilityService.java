package es.ull.project.application.service.facility;

import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.usecase.facility.CreateFacilityUseCase;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;

/**
 * Service implementation for creating facilities.
 * This service handles the business logic for facility creation operations.
 */
public class CreateFacilityService implements CreateFacilityUseCase {

    private final FacilityRepository repository;

    /**
     * Constructs a new CreateFacilityService with the specified repository.
     * @param repository the facility repository for persistence operations
     */
    public CreateFacilityService(FacilityRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new facility with the specified attributes.
     * @param name the facility name
     * @param facilityType the type of facility
     * @param location the geographic location of the facility
     * @param storageCapacity the storage capacity of the facility in kilograms
     * @param processingCapacity the processing capacity of the facility in kg/day
     * @param unloadingTime the truck unloading time in minutes
     * @param openingFixedCost the fixed cost for opening the facility
     * @param status the initial status of the facility
     * @return the created and persisted facility
     */
    @Override
    public Facility create(
        Name name,
        FacilityType facilityType,
        Location location,
        StorageCapacityKilograms storageCapacity,
        ProcessingCapacityKilogramsPerDay processingCapacity,
        UnloadingTime unloadingTime,
        OpeningFixedCost openingFixedCost,
        FacilityStatus status
    ) {
        Facility newFacility = new Facility(
            name,
            facilityType,
            location,
            storageCapacity,
            processingCapacity,
            unloadingTime,
            openingFixedCost,
            status
        );
        Facility saved = this.repository.save(newFacility);
        return saved;
    }
}
