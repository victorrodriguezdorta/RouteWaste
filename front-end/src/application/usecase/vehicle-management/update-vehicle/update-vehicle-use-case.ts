// Re-export types for convenience
export type { UpdateVehicleCommand } from './update-vehicle-command';
export type { UpdateVehicleResult } from './update-vehicle-result';
// UpdateVehicleUseCase.ts
// Use case contract for updating a vehicle

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { UpdateVehicleCommand } from './update-vehicle-command';
import type { UpdateVehicleResult } from './update-vehicle-result';

// Use case contract
export interface UpdateVehicleUseCase {
    /**
     * Handles the update of an existing vehicle
     *
     * @param command Data needed to update the vehicle
     * @returns Either a DataError or the updated Vehicle
     */
    execute(command: UpdateVehicleCommand): Promise<Either<DataError, UpdateVehicleResult>>;
}
