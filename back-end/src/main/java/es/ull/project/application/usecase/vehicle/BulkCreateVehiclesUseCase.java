package es.ull.project.application.usecase.vehicle;

import es.ull.project.application.message.BulkCreateOutcome;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.name.Name;
import java.util.List;

/**
 * Use case for creating multiple vehicles in a single operation.
 */
public interface BulkCreateVehiclesUseCase {

    /**
     * Creates vehicles from the provided attribute tuples.
     *
     * @param names              vehicle names
     * @param vehicleTypes       vehicle types
     * @param capacityKilograms  load capacities in kilograms
     * @param capacityLiters     load capacities in liters
     * @param costPerKilometer   transportation costs per kilometer
     * @return outcome with created and failed counts
     */
    BulkCreateOutcome createAll(
            List<Name> names,
            List<VehicleType> vehicleTypes,
            List<VehicleCapacityKilograms> capacityKilograms,
            List<VehicleCapacityLiters> capacityLiters,
            List<TransportationVariableCost> costPerKilometer);
}
