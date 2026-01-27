// FilterFacilitiesUseCase.ts
// Use case contract for filtering facilities

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Facility } from '../../../domain/entity/Facility';
import { FacilityType } from '../../../domain/enumerate/FacilityType';
import { FacilityStatus } from '../../../domain/enumerate/FacilityStatus';

/**
 * Use case for filtering facilities based on criteria.
 * Input: filter parameters (facilityType, status, etc.)
 * Output: Facility[] (entity array)
 */

// Command object for input data
export interface FilterFacilitiesCommand {
    facilityType?: FacilityType;
    status?: FacilityStatus;
    minCapacity?: number;
    maxCapacity?: number;
}

// Result type for the use case
export type FilterFacilitiesResult = Facility[];

// Use case contract
export interface FilterFacilitiesUseCase {
    /**
     * Handles filtering facilities based on provided criteria
     *
     * @param command Filter parameters
     * @returns Either a DataError or a list of Facility entities matching the filters
     */
    execute(command: FilterFacilitiesCommand): Promise<Either<DataError, FilterFacilitiesResult>>;
}
