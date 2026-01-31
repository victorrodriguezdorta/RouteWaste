package es.ull.project.application.service.vehicle;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.vehicle.ReadVehicleUseCase;
import es.ull.project.domain.entity.Vehicle;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service responsible for reading vehicle data from the system.
 * This service implements the {@link ReadVehicleUseCase} interface and provides
 * the business logic for vehicle retrieval operations.
 */
public class ReadVehicleService implements ReadVehicleUseCase {

    private final VehicleRepository repository;

    /**
     * Constructs a new ReadVehicleService with the specified repository.
     *
     * @param repository the vehicle repository used for persistence operations
     */
    public ReadVehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a vehicle by its unique identifier.
     *
     * @param id the unique identifier of the vehicle to retrieve
     * @return the vehicle with the specified identifier
     * @throws NoSuchElementException if no vehicle is found with the given identifier
     */
    @Override
    public Vehicle fetch(UUID id) {
        return this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Vehicle not found"));
    }

    /**
     * Retrieves all vehicles from the system.
     *
     * @return a list containing all vehicles
     */
    @Override
    public List<Vehicle> fetchAll() {
        return this.repository.findAll();
    }
}
