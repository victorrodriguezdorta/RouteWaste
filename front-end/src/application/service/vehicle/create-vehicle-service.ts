import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CreateVehicleUseCase, CreateVehicleCommand, CreateVehicleResult } from '../../usecase/VehicleManagement/CreateVehicle/create-vehicle-use-case';
import type { VehicleRepository } from '../../repository/vehicle-repository';

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
