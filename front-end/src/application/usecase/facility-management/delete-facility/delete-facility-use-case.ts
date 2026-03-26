import type { DeleteFacilityCommand } from './delete-facility-command';
import type { DeleteFacilityResult } from './delete-facility-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { DeleteFacilityCommand } from './delete-facility-command';
export type { DeleteFacilityResult } from './delete-facility-result';

// DeleteFacilityUseCase.ts
// Use case contract for deleting a facility


/**
 * Use case for deleting a facility from the system.
 * Input: facilityId
 * Output: success confirmation (boolean)
 */


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
