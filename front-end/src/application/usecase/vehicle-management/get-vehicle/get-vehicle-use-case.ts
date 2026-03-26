import type { GetVehicleCommand } from './get-vehicle-command';
import type { GetVehicleResult } from './get-vehicle-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { GetVehicleCommand } from './get-vehicle-command';
export type { GetVehicleResult } from './get-vehicle-result';

// Use case contract for getting a vehicle by id

/**
 * Use case contract for getting a vehicle by id
 */
export interface GetVehicleUseCase {
    /**
     * Handles retrieving a single vehicle by id
     * @param command Data needed to retrieve the vehicle
     * @returns Either a DataError or the Vehicle entity
     */
    execute(command: GetVehicleCommand): Promise<Either<DataError, GetVehicleResult>>;
}