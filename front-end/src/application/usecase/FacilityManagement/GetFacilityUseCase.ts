// GetFacilityUseCase.ts
// Use case contract for getting a single facility by id

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Facility } from '../../../domain/entity/Facility';

/**
 * Use case for obtaining a single facility by its identifier.
 * Input: facilityId
 * Output: Facility (entity)
 */

// Command object for input data
export interface GetFacilityCommand {
    facilityId: string;
}

// Result type for the use case
export type GetFacilityResult = Facility;

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
