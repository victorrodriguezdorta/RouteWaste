// FilterContainersUseCase.ts
// Use case contract for filtering containers

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Container } from '../../../domain/entity/Container';
import { WasteType } from '../../../domain/enumerate/WasteType';
import { ServiceZone } from '../../../domain/enumerate/ServiceZone';

/**
 * Use case for filtering containers based on criteria.
 * Input: filter parameters (wasteType, serviceZone, etc.)
 * Output: Container[] (entity array)
 */

// Command object for input data
export interface FilterContainersCommand {
    wasteType?: WasteType;
    serviceZone?: ServiceZone;
    minDemand?: number;
    maxDemand?: number;
}

// Result type for the use case
export type FilterContainersResult = Container[];

// Use case contract
export interface FilterContainersUseCase {
    /**
     * Handles filtering containers based on provided criteria
     *
     * @param command Filter parameters
     * @returns Either a DataError or a list of Container entities matching the filters
     */
    execute(command: FilterContainersCommand): Promise<Either<DataError, FilterContainersResult>>;
}
