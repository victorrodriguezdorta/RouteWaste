// DeleteFacilityUseCase.ts
// Use case contract for deleting a facility

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for deleting a facility from the system.
 * Input: facilityId
 * Output: success confirmation (boolean)
 */

// Command object for input data
export interface DeleteFacilityCommand {
    facilityId: string;
}

// Result type for the use case
export type DeleteFacilityResult = boolean;

// Use case contract
export interface DeleteFacilityUseCase {
    /**
     * Handles the deletion of a facility
     *
     * @param command Data needed to delete the facility
     * @returns Either a DataError or a success confirmation
     */
    execute(command: DeleteFacilityCommand): Promise<Either<DataError, DeleteFacilityResult>>;
}
