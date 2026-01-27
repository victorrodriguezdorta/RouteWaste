// ListFacilitiesUseCase.ts
// Use case contract for listing all facilities

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Facility } from '../../../domain/entity/Facility';

/**
 * Use case for listing all facilities in the system.
 * Input: none (optionally pagination params)
 * Output: Facility[] (entity array)
 */

// Command object for input data (optional pagination)
export interface ListFacilitiesCommand {
    page?: number;
    pageSize?: number;
}

// Result type for the use case
export type ListFacilitiesResult = Facility[];

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
