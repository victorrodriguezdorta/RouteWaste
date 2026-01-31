package es.ull.project.application.usecase.vehicle;

import es.ull.project.domain.entity.Vehicle;

import java.util.List;
import java.util.UUID;

/**
 * Use case interface for reading vehicles.
 *
 * @see Vehicle
 */
public interface ReadVehicleUseCase {

    /**
     * Retrieves a vehicle by its unique identifier.
     *
     * @param id the unique identifier of the vehicle
     * @return the vehicle with the specified identifier
     */
    Vehicle fetch(UUID id);

    /**
     * Retrieves all vehicles from the system.
     *
     * @return a list containing all vehicles
     */
    List<Vehicle> fetchAll();
}
