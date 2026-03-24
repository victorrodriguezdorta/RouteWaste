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
    /**
     * Repositorio utilizado para consultar vehículos por su identificador.
     */
    private readonly vehicleRepository: VehicleRepository;

    /**
     * @brief Construct the service with a repository.
     * @param vehicleRepository Repository used to query vehicles by id.
     */
    constructor(vehicleRepository: VehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Ejecuta el caso de uso para obtener un vehículo.
     * @param command Datos que contienen el id del vehículo a recuperar.
     * @returns Una promesa que resuelve en un Either con un DataError o el resultado del vehículo recuperado.
     */
    async execute(command: GetVehicleCommand): Promise<Either<DataError, GetVehicleResult>> {
        return this.vehicleRepository.getById(command);
    }
}
