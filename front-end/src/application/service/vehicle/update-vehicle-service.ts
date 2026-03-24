import type { VehicleRepository } from '@/application/repository/vehicle-repository';
import type { UpdateVehicleCommand, UpdateVehicleResult, UpdateVehicleUseCase } from '@/application/usecase/vehicle-management/update-vehicle/update-vehicle-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the UpdateVehicle use case.
 *
 * Delegates update operation to a `VehicleRepository` implementation.
 */
export class UpdateVehicleService implements UpdateVehicleUseCase {
    /**
     * Repository used to perform update operations.
     */
    private readonly vehicleRepository: VehicleRepository;

    /**
     * @brief Construct the service with a repository.
     * @param vehicleRepository Repository used to perform update operations.
     */
    constructor(vehicleRepository: VehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * @brief Execute the update vehicle use case.
     * @param command Data required to update the vehicle (id + partial fields).
     * @returns Either a `DataError` or the updated `Vehicle` entity.
     */
    async execute(command: UpdateVehicleCommand): Promise<Either<DataError, UpdateVehicleResult>> {
        return this.vehicleRepository.update(command);
    }
}
