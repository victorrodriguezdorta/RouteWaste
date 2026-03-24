import type { VehicleRepository } from '@/application/repository/vehicle-repository';
import type { CreateVehicleCommand, CreateVehicleResult, CreateVehicleUseCase } from '@/application/usecase/vehicle-management/create-vehicle/create-vehicle-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the CreateVehicle use case.
 *
 * Implements the application-level use case by delegating persistence/communication
 * details to a provided `VehicleRepository`.
 */
export class CreateVehicleService implements CreateVehicleUseCase {
    /**
     * Repositorio utilizado para persistir o reenviar las solicitudes de creación de vehículos.
     */
    private readonly vehicleRepository: VehicleRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param vehicleRepository Repository used to persist or forward create requests.
     */
    constructor(vehicleRepository: VehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Ejecuta el caso de uso para crear un vehículo.
     * @param command Datos de entrada requeridos para crear un vehículo.
     * @returns Una promesa que resuelve en un Either con un DataError o el resultado de la creación del vehículo.
     */
    async execute(command: CreateVehicleCommand): Promise<Either<DataError, CreateVehicleResult>> {
        return this.vehicleRepository.create(command);
    }
}
