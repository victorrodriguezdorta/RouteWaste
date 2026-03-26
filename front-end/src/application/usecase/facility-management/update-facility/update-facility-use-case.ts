import type { UpdateFacilityCommand } from './update-facility-command';
import type { UpdateFacilityResult } from './update-facility-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { UpdateFacilityCommand } from './update-facility-command';
export type { UpdateFacilityResult } from './update-facility-result';

// UpdateFacilityUseCase.ts
// Use case contract for updating a facility
/**
 * Use case for updating an existing facility in the system.
 * Input: facilityId and fields to update
 * Output: Facility (entity)
 */
export interface UpdateFacilityUseCase {
    /**
     * Handles the update of an existing facility
     *
     * @param command Data needed to update the facility
     * @returns Either a DataError or the updated Facility
     */
    execute(command: UpdateFacilityCommand): Promise<Either<DataError, UpdateFacilityResult>>;
}
