package es.ull.project.application.service.vehicle;

import es.ull.project.application.repository.VehicleRepository;
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

    /**
     * Constructs a new DeleteVehicleService with the specified repository.
     *
     * @param repository the vehicle repository used for persistence operations
     */
    public DeleteVehicleService(VehicleRepository repository) {
        this.repository = repository;
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
        this.repository.delete(existing);
        return existing;
    }
}
