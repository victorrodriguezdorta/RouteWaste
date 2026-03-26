import type { FilterContainersCommand } from './filter-containers-command';
import type { FilterContainersResult } from './filter-containers-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { FilterContainersCommand } from './filter-containers-command';
export type { FilterContainersResult } from './filter-containers-result';

// FilterContainersUseCase.ts
// Use case contract for filtering containers

/**
 * Use case for filtering containers based on criteria.
 * Input: filter parameters (wasteType, serviceZone, etc.)
 * Output: Container[] (entity array)
 */

/**
 * Use case contract for filtering containers
 */
export interface FilterContainersUseCase {
    /**
     * Handles filtering containers based on provided criteria
     * @param command Filter parameters
     * @returns Either a DataError or a list of Container entities matching the filters
     */
    execute(command: FilterContainersCommand): Promise<Either<DataError, FilterContainersResult>>;
}



