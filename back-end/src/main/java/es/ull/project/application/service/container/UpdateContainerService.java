package es.ull.project.application.service.container;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.service.infrastructureplan.InvalidateInfrastructurePlansOnEntityEditService;
import es.ull.project.application.usecase.container.UpdateContainerUseCase;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service implementation for updating containers.
 * This service handles the business logic for container update operations.
 */
public class UpdateContainerService implements UpdateContainerUseCase {

    private final ContainerRepository repository;
    private final InvalidateInfrastructurePlansOnEntityEditService infrastructurePlanInvalidation;

    /**
     * Constructs a new UpdateContainerService with the specified repository.
     * @param repository the container repository for persistence operations
     * @param infrastructurePlanInvalidation service that marks affected infrastructure plans obsolete
     */
    public UpdateContainerService(
            ContainerRepository repository,
            InvalidateInfrastructurePlansOnEntityEditService infrastructurePlanInvalidation) {
        this.repository = repository;
        this.infrastructurePlanInvalidation = infrastructurePlanInvalidation;
    }

    /**
     * Updates an existing container with new values.
     * @param id the unique identifier of the container to update
     * @param newLocation the new location, or null to keep the existing value
     * @param newWasteType the new waste type, or null to keep the existing value
     * @param newCapacityLiters the new container capacity in liters, or null to keep the existing value
     * @param newDailyDemandLitersPerDay the new daily demand in liters per day, or null to keep the existing value
     * @param newServiceZone the new service zone
     * @return the updated container
     * @throws NoSuchElementException if no container is found with the given id
     */
    @Override
    public Container update(UUID id, Name newName, Location newLocation, WasteType newWasteType, ContainerCapacityLiters newCapacityLiters, DailyWasteDemandLitersPerDay newDailyDemandLitersPerDay, ServiceZone newServiceZone) {
        Container existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Container not found"));
        if (newName != null) {
            existing.updateName(newName);
        }
        if (newLocation != null) {
            existing.updateLocation(newLocation);
        }
        if (newWasteType != null) {
            existing.updateWasteType(newWasteType);
        }
        if (newCapacityLiters != null) {
            existing.updateCapacityLiters(newCapacityLiters);
        }
        if (newDailyDemandLitersPerDay != null) {
            existing.updateDailyDemandLitersPerDay(newDailyDemandLitersPerDay);
        }
        existing.updateServiceZone(newServiceZone);
        Container saved = this.repository.save(existing);
        this.infrastructurePlanInvalidation.invalidateValidPlansReferencingEntity(id);
        return saved;
    }
}
