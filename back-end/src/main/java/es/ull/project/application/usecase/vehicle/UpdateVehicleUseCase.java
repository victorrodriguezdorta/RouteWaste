package es.ull.project.application.usecase.vehicle;

import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import java.util.UUID;

public interface UpdateVehicleUseCase {
    Vehicle update(UUID id, VehicleType newVehicleType, Capacity newTransportCapacity, TransportationVariableCost newCostPerKilometer);
}
