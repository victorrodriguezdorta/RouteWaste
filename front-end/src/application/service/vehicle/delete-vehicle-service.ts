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
    /**
     * Repositorio utilizado para realizar operaciones de borrado de vehículos.
     */
    private readonly vehicleRepository: VehicleRepository;

    /**
     * @brief Construct the service with a repository.
     * @param vehicleRepository Repository used to perform delete operations.
     */
    constructor(vehicleRepository: VehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Ejecuta el caso de uso para eliminar un vehículo.
     * @param command Datos que contienen el id del vehículo a eliminar.
     * @returns Un Either con un DataError o un booleano indicando el éxito.
     */
    async execute(command: DeleteVehicleCommand): Promise<Either<DataError, DeleteVehicleResult>> {
        return this.vehicleRepository.delete(command);
    }
}
