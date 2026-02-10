// Re-export types for convenience
export type { DeleteVehicleCommand } from './delete-vehicle-command';
export type { DeleteVehicleResult } from './delete-vehicle-result';
// DeleteVehicleUseCase.ts
// Use case contract for deleting a vehicle

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { DeleteVehicleCommand } from './delete-vehicle-command';
import type { DeleteVehicleResult } from './delete-vehicle-result';

/**
 * Use case for deleting a vehicle from the system.
 * Input: vehicleId
 * Output: success confirmation (boolean)
 */
export interface DeleteVehicleUseCase {
    /**
     * Handles the deletion of a vehicle
     *
     * @param command Data needed to delete the vehicle
     * @returns Either a DataError or a success confirmation
     */
    execute(command: DeleteVehicleCommand): Promise<Either<DataError, DeleteVehicleResult>>;
}
