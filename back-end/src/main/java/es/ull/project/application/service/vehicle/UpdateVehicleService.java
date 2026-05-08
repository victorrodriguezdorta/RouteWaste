package es.ull.project.application.service.vehicle;

import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.application.usecase.vehicle.UpdateVehicleUseCase;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service responsible for updating existing vehicles in the system.
 * This service implements the {@link UpdateVehicleUseCase} interface and provides
 * the business logic for vehicle modification operations.
 */
public class UpdateVehicleService implements UpdateVehicleUseCase {

    private final VehicleRepository repository;

    /**
     * Constructs a new UpdateVehicleService with the specified repository.
     *
     * @param repository the vehicle repository used for persistence operations
     */
    public UpdateVehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    /**
     * Updates an existing vehicle with the specified parameters.
     * <p>
     * Only non-null parameters will be updated. Null values are ignored,
     * allowing for partial updates.
     * </p>
     *
     * @param id the unique identifier of the vehicle to update
     * @param newVehicleType the new vehicle type, or null to keep the current value
     * @param newCapacityKilograms the new capacity in kilograms, or null to keep the current value
     * @param newCapacityLiters the new capacity in liters, or null to keep the current value
     * @param newCostPerKilometer the new cost per kilometer, or null to keep the current value
     * @return the updated vehicle
     * @throws NoSuchElementException if no vehicle is found with the given identifier
     */
    @Override
    public Vehicle update(UUID id, VehicleType newVehicleType, VehicleCapacityKilograms newCapacityKilograms, VehicleCapacityLiters newCapacityLiters, TransportationVariableCost newCostPerKilometer) {
        Vehicle existing = this.repository.findById(id).orElseThrow(() -> new NoSuchElementException("Vehicle not found"));
        if (newVehicleType != null) {
            existing.updateVehicleType(newVehicleType);
        }
        if (newCapacityKilograms != null) {
            existing.updateCapacityKilograms(newCapacityKilograms);
        }
        if (newCapacityLiters != null) {
            existing.updateCapacityLiters(newCapacityLiters);
        }
        if (newCostPerKilometer != null) {
            existing.updateCostPerKilometer(newCostPerKilometer);
        }
        Vehicle saved = this.repository.save(existing);
        return saved;
    }
}
