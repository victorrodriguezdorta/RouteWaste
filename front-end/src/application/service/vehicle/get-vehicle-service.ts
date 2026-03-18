import type { VehicleRepository } from '@/application/repository/vehicle-repository';
import type { GetVehicleCommand, GetVehicleResult, GetVehicleUseCase } from '@/application/usecase/vehicle-management/get-vehicle/get-vehicle-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the GetVehicle use case.
 *
 * Delegates retrieval to `VehicleRepository` which is expected to provide a
 * `getById` method returning an `Either<DataError, Vehicle>`.
 */
export class GetVehicleService implements GetVehicleUseCase {
    private readonly vehicleRepository: VehicleRepository;

    /**
     * @brief Construct the service with a repository.
     * @param vehicleRepository Repository used to query vehicles by id.
     */
    constructor(vehicleRepository: VehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * @brief Execute the get vehicle use case.
     * @param command Data containing the id of the vehicle to retrieve.
     * @return Either a `DataError` or the `Vehicle` entity.
     */
    async execute(command: GetVehicleCommand): Promise<Either<DataError, GetVehicleResult>> {
        return this.vehicleRepository.getById(command);
    }
}
