package es.ull.project.application.service.vehicle;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.vehicle.CreateVehicleUseCase;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;

/**
 * Service responsible for creating new vehicles in the system.
 * This service implements the {@link CreateVehicleUseCase} interface and provides
 * the business logic for vehicle creation operations.
 */
public class CreateVehicleService implements CreateVehicleUseCase {

    private final VehicleRepository repository;

    /**
     * Constructs a new CreateVehicleService with the specified repository.
     *
     * @param repository the vehicle repository used for persistence operations
     */
    public CreateVehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new vehicle with the specified parameters.
     *
     * @param vehicleType the type of vehicle to create
     * @param capacityKilograms the transport capacity in kilograms
     * @param capacityLiters the transport capacity in liters
     * @param costPerKilometer the transportation cost per kilometer
     * @return the newly created and persisted vehicle
     */
    @Override
    public Vehicle create(VehicleType vehicleType, VehicleCapacityKilograms capacityKilograms, VehicleCapacityLiters capacityLiters, TransportationVariableCost costPerKilometer) {
        Vehicle newVehicle = new Vehicle(vehicleType, capacityKilograms, capacityLiters, costPerKilometer);
        Vehicle saved = this.repository.save(newVehicle);
        return saved;
    }
}
