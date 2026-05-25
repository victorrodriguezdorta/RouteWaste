import type { CreateVehicleCommand } from '@/application/model/vehicle-management/create-vehicle/create-vehicle-command';
import type { CreateVehicleResult } from '@/application/model/vehicle-management/create-vehicle/create-vehicle-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case contract for creating a new vehicle
 */
export interface CreateVehicleUseCase {
    /**
     * Handles the creation of a new vehicle
     *
     * @param command Data needed to create the vehicle
     * @returns Either a DataError or the created Vehicle
     */
    execute(command: CreateVehicleCommand): Promise<Either<DataError, CreateVehicleResult>>;
}

export type { CreateVehicleCommand } from '@/application/model/vehicle-management/create-vehicle/create-vehicle-command';
export type { CreateVehicleResult } from '@/application/model/vehicle-management/create-vehicle/create-vehicle-result';
