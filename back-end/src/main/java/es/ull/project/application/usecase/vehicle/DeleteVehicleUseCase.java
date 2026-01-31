package es.ull.project.application.usecase.vehicle;

import es.ull.project.domain.entity.Vehicle;

import java.util.UUID;

/**
 * Use case interface for deleting vehicles.
 *
 * @see Vehicle
 */
public interface DeleteVehicleUseCase {

    /**
     * Deletes a vehicle by its unique identifier.
     *
     * @param id the unique identifier of the vehicle to delete
     * @return the deleted vehicle
     */
    Vehicle delete(UUID id);
}
