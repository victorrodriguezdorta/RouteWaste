// Re-export types for convenience
export type { GetFacilityCommand } from './get-facility-command';
export type { GetFacilityResult } from './get-facility-result';
// GetFacilityUseCase.ts
// Use case contract for getting a single facility by id

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { GetFacilityCommand } from './get-facility-command';
import type { GetFacilityResult } from './get-facility-result';


/**
 * Use case for obtaining a single facility by its identifier.
 * Input: facilityId
 * Output: Facility (entity)
 */




// Use case contract
export interface GetFacilityUseCase {
    /**
     * Handles retrieving a single facility by id
     *
     * @param command Data needed to retrieve the facility
     * @returns Either a DataError or the Facility entity
     */
    execute(command: GetFacilityCommand): Promise<Either<DataError, GetFacilityResult>>;
}
