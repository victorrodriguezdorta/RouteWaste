package es.ull.project.application.usecase.vehicle;

import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;

/**
 * Use case for creating a new vehicle.
 */
public interface CreateVehicleUseCase {
    /**
     * Creates a new vehicle with the specified attributes.
     *
     * @param vehicleType        the type of vehicle
     * @param capacityKilograms the transport capacity in kilograms
     * @param CapacityLiters     the transport capacity in liters
     * @param costPerKilometer   the cost per kilometer for transportation
     * @return the created vehicle
     */
    Vehicle create(VehicleType vehicleType, VehicleCapacityKilograms capacityKilograms, VehicleCapacityLiters CapacityLiters, TransportationVariableCost costPerKilometer);
}
