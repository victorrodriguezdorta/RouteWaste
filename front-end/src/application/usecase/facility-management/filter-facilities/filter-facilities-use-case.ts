import type { FilterFacilitiesCommand } from '@/application/model/facility-management/filter-facilities/filter-facilities-command';
import type { FilterFacilitiesResult } from '@/application/model/facility-management/filter-facilities/filter-facilities-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for filtering facilities based on criteria.
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

export type { FilterFacilitiesCommand } from '@/application/model/facility-management/filter-facilities/filter-facilities-command';
export type { FilterFacilitiesResult } from '@/application/model/facility-management/filter-facilities/filter-facilities-result';
