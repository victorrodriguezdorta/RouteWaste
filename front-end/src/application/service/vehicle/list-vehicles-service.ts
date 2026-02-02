import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { ListVehiclesUseCase, ListVehiclesCommand, ListVehiclesResult } from '../../usecase/VehicleManagement/list-vehicles-use-case';
import type { VehicleRepository } from '../../repository/vehicle-repository';

/**
 * @brief Service implementing the ListVehicles use case.
 *
 * Delegates listing to `VehicleRepository`. Pagination parameters are forwarded
 * directly to the repository; repository must return an `Either<DataError, Vehicle[]>`.
 */
export class ListVehiclesService implements ListVehiclesUseCase {
    private readonly vehicleRepository: VehicleRepository;

    /**
     * @brief Construct the service with a repository.
     * @param vehicleRepository Repository used to list vehicles.
     */
    constructor(vehicleRepository: VehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * @brief Execute the list vehicles use case.
     * @param command Optional pagination parameters.
     * @return Either a `DataError` or an array of `Vehicle` entities.
     */
    async execute(command?: ListVehiclesCommand): Promise<Either<DataError, ListVehiclesResult>> {
        const page = command?.page;
        const pageSize = command?.pageSize;
        return this.vehicleRepository.list({ page, pageSize });
    }
}
