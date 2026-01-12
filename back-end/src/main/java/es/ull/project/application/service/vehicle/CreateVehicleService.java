package es.ull.project.application.service.vehicle;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.vehicle.CreateVehicleUseCase;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateVehicleService implements CreateVehicleUseCase {

    @Autowired
    private VehicleRepository repository;

    @Override
    public Vehicle create(VehicleType vehicleType, Capacity transportCapacity, TransportationVariableCost costPerKilometer) {
        Vehicle newVehicle = new Vehicle(vehicleType, transportCapacity, costPerKilometer);
        Vehicle saved = this.repository.save(newVehicle);
        return saved;
    }
}
