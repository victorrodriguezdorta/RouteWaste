package es.ull.project.application.usecase.vehicle;

import java.util.UUID;

import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;

/**
 * Use case for updating a vehicle.
 */
public interface UpdateVehicleUseCase {
    /**
     * Updates a vehicle with the specified attributes.
     *
     * @param id                     the unique identifier of the vehicle to update
     * @param newVehicleType         the new vehicle type
     * @param newCapacityKilograms  the new capacity in kilograms
     * @param newCapacityLiters      the new capacity in liters
     * @param newCostPerKilometer    the new cost per kilometer
     * @return the updated vehicle
     */
    Vehicle update(UUID id, VehicleType newVehicleType, VehicleCapacityKilograms newCapacityKilograms, VehicleCapacityLiters newCapacityLiters, TransportationVariableCost newCostPerKilometer);
}
