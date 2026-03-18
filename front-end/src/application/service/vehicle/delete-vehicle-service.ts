import type { VehicleRepository } from '@/application/repository/vehicle-repository';
import type { DeleteVehicleCommand, DeleteVehicleResult, DeleteVehicleUseCase } from '@/application/usecase/vehicle-management/delete-vehicle/delete-vehicle-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the DeleteVehicle use case.
 *
 * Delegates deletion to `VehicleRepository`. Repository should return a boolean
 * indicating success or an error wrapped in `Either`.
 */
export class DeleteVehicleService implements DeleteVehicleUseCase {
    private readonly vehicleRepository: VehicleRepository;

    /**
     * @brief Construct the service with a repository.
     * @param vehicleRepository Repository used to perform delete operations.
     */
    constructor(vehicleRepository: VehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * @brief Execute the delete vehicle use case.
     * @param command Data containing the id of the vehicle to delete.
     * @return Either a `DataError` or a boolean indicating success.
     */
    async execute(command: DeleteVehicleCommand): Promise<Either<DataError, DeleteVehicleResult>> {
        return this.vehicleRepository.delete(command);
    }
}
