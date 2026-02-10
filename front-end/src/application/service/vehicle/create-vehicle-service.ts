import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { VehicleRepository } from '../../repository/vehicle-repository';
import type { CreateVehicleCommand, CreateVehicleResult, CreateVehicleUseCase } from '../../usecase/vehicle-management/create-vehicle/create-vehicle-use-case';

/**
 * @brief Service implementing the CreateVehicle use case.
 *
 * Implements the application-level use case by delegating persistence/communication
 * details to a provided `VehicleRepository`.
 */
export class CreateVehicleService implements CreateVehicleUseCase {
    private readonly vehicleRepository: VehicleRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param vehicleRepository Repository used to persist or forward create requests.
     */
    constructor(vehicleRepository: VehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * @brief Execute the create vehicle use case.
     * @param command Input data required to create a vehicle.
     * @return Either a `DataError` or the created `Vehicle` entity.
     */
    async execute(command: CreateVehicleCommand): Promise<Either<DataError, CreateVehicleResult>> {
        return this.vehicleRepository.create(command);
    }
}
