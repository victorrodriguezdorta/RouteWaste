// GetVehicleUseCase.ts
// Use case contract for getting a single vehicle by id

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Vehicle } from '../../../domain/entity/vehicle';

/**
 * Use case for obtaining a single vehicle by its identifier.
 * Input: vehicleId
 * Output: Vehicle (entity)
 */

// Command object for input data
export interface GetVehicleCommand {
    vehicleId: string;
}

// Result type for the use case
export type GetVehicleResult = Vehicle;

// Use case contract
export interface GetVehicleUseCase {
    /**
     * Handles retrieving a single vehicle by id
     *
     * @param command Data needed to retrieve the vehicle
     * @returns Either a DataError or the Vehicle entity
     */
    execute(command: GetVehicleCommand): Promise<Either<DataError, GetVehicleResult>>;
}
