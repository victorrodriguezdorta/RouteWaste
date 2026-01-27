// DeleteVehicleUseCase.ts
// Use case contract for deleting a vehicle

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for deleting a vehicle from the system.
 * Input: vehicleId
 * Output: success confirmation (boolean)
 */

// Command object for input data
export interface DeleteVehicleCommand {
    vehicleId: string;
}

// Result type for the use case
export type DeleteVehicleResult = boolean;

// Use case contract
export interface DeleteVehicleUseCase {
    /**
     * Handles the deletion of a vehicle
     *
     * @param command Data needed to delete the vehicle
     * @returns Either a DataError or a success confirmation
     */
    execute(command: DeleteVehicleCommand): Promise<Either<DataError, DeleteVehicleResult>>;
}
