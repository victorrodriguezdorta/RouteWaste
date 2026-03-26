import type { FilterFacilitiesCommand } from './filter-facilities-command';
import type { FilterFacilitiesResult } from './filter-facilities-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { FilterFacilitiesCommand } from './filter-facilities-command';
export type { FilterFacilitiesResult } from './filter-facilities-result';

// FilterFacilitiesUseCase.ts
// Use case contract for filtering facilities
/**
 * Use case for filtering facilities based on criteria.
 * Input: filter parameters (facilityType, status, etc.)
 * Output: Facility[] (entity array)
 */
export interface FilterFacilitiesUseCase {
    /**
     * Handles filtering facilities based on provided criteria
     *
     * @param command Filter parameters
     * @returns Either a DataError or a list of Facility entities matching the filters
     */
    execute(command: FilterFacilitiesCommand): Promise<Either<DataError, FilterFacilitiesResult>>;
}
