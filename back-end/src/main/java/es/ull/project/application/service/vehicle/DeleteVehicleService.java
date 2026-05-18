package es.ull.project.application.service.vehicle;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.infrastructureplan.DeleteInfrastructurePlansReferencingEntityUseCase;
import es.ull.project.application.usecase.vehicle.DeleteVehicleUseCase;
import es.ull.project.domain.entity.Vehicle;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service responsible for deleting vehicles from the system.
 * This service implements the {@link DeleteVehicleUseCase} interface and provides
 * the business logic for vehicle deletion operations.
 */
public class DeleteVehicleService implements DeleteVehicleUseCase {

    private final VehicleRepository repository;
    private final DeleteInfrastructurePlansReferencingEntityUseCase deleteInfrastructurePlansReferencingEntityUseCase;

    /**
     * Constructs a new DeleteVehicleService with the specified repository.
     *
     * @param repository the vehicle repository used for persistence operations
     * @param deleteInfrastructurePlansReferencingEntityUseCase removes plans that referenced this vehicle in their execution snapshot
     */
    public DeleteVehicleService(
            VehicleRepository repository,
            DeleteInfrastructurePlansReferencingEntityUseCase deleteInfrastructurePlansReferencingEntityUseCase) {
        this.repository = repository;
        this.deleteInfrastructurePlansReferencingEntityUseCase = deleteInfrastructurePlansReferencingEntityUseCase;
    }

    /**
     * Deletes a vehicle by its unique identifier.
     *
     * @param id the unique identifier of the vehicle to delete
     * @return the deleted vehicle
     * @throws NoSuchElementException if no vehicle is found with the given identifier
     */
    @Override
    public Vehicle delete(UUID id) {
        Vehicle existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Vehicle not found"));
        this.deleteInfrastructurePlansReferencingEntityUseCase.deletePlansWhoseExecutionRequestReferencesEntity(id);
        this.repository.delete(existing);
        return existing;
    }
}
