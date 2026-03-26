import type { ListFacilitiesCommand } from './list-facilities-command';
import type { ListFacilitiesResult } from './list-facilities-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { ListFacilitiesCommand } from './list-facilities-command';
export type { ListFacilitiesResult } from './list-facilities-result';

// ListFacilitiesUseCase.ts
// Use case contract for listing all facilities


/**
 * Use case for listing all facilities in the system.
 * Input: none (optionally pagination params)
 * Output: Facility[] (entity array)
 */



// Use case contract
export interface ListFacilitiesUseCase {
    /**
     * Handles listing all facilities
     *
     * @param command Optional pagination parameters
     * @returns Either a DataError or a list of Facility entities
     */
    execute(command?: ListFacilitiesCommand): Promise<Either<DataError, ListFacilitiesResult>>;
}
