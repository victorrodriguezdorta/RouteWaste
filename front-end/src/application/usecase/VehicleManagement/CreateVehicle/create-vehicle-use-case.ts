// Re-export types for convenience
export type { CreateVehicleCommand } from './create-vehicle-command';
export type { CreateVehicleResult } from './create-vehicle-result';
// CreateVehicleUseCase.ts
// Use case contract for creating a new vehicle

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CreateVehicleCommand } from './create-vehicle-command';
import type { CreateVehicleResult } from './create-vehicle-result';


// Use case contract
export interface CreateVehicleUseCase {
    /**
     * Handles the creation of a new vehicle
     *
     * @param command Data needed to create the vehicle
     * @returns Either a DataError or the created Vehicle
     */
    execute(command: CreateVehicleCommand): Promise<Either<DataError, CreateVehicleResult>>;
}
