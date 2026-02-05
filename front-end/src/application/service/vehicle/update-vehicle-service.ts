import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { UpdateVehicleUseCase, UpdateVehicleCommand, UpdateVehicleResult } from '../../usecase/VehicleManagement/UpdateVehicle/update-vehicle-use-case';
import type { VehicleRepository } from '../../repository/vehicle-repository';

/**
 * @brief Service implementing the UpdateVehicle use case.
 *
 * Delegates update operation to a `VehicleRepository` implementation.
 */
export class UpdateVehicleService implements UpdateVehicleUseCase {
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
     * @return Either a `DataError` or the updated `Vehicle` entity.
     */
    async execute(command: UpdateVehicleCommand): Promise<Either<DataError, UpdateVehicleResult>> {
        return this.vehicleRepository.update(command);
    }
}
