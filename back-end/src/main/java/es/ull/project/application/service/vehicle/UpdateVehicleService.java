package es.ull.project.application.service.vehicle;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.vehicle.UpdateVehicleUseCase;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UpdateVehicleService implements UpdateVehicleUseCase {

    @Autowired
    private VehicleRepository repository;

    @Override
    public Vehicle update(UUID id, VehicleType newVehicleType, Capacity newTransportCapacity, TransportationVariableCost newCostPerKilometer) {
        Vehicle existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Vehicle not found"));

        if (newVehicleType != null) {
            existing.updateVehicleType(newVehicleType);
        }
        if (newTransportCapacity != null) {
            existing.updateTransportCapacity(newTransportCapacity);
        }
        if (newCostPerKilometer != null) {
            existing.updateCostPerKilometer(newCostPerKilometer);
        }

        Vehicle saved = this.repository.save(existing);
        return saved;
    }
}
