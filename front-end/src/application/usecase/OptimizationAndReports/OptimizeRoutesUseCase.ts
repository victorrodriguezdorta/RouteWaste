// OptimizeRoutesUseCase.ts
// Use case contract for optimizing collection routes

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Distance } from '../../../domain/valueobject/location/Distance';
import { ServiceTime } from '../../../domain/valueobject/location/ServiceTime';

/**
 * Represents an optimized route segment
 */
export interface OptimizedRoute {
    vehicleId: string;
    orderedContainerIds: string[];
    targetFacilityId: string;
    totalDistance: Distance;
    estimatedTime: ServiceTime;
}

/**
 * Use case for optimizing collection routes.
 * Input: containers, facilities, vehicles, optimization parameters
 * Output: OptimizedRoute[]
 */

// Command object for input data
export interface OptimizeRoutesCommand {
    containerIds: string[];
    facilityIds: string[];
    vehicleIds: string[];
    optimizationCriteria?: 'distance' | 'time' | 'cost';
}

// Result type for the use case
export type OptimizeRoutesResult = OptimizedRoute[];

// Use case contract
export interface OptimizeRoutesUseCase {
    /**
     * Handles the optimization of collection routes
     *
     * @param command Data needed to optimize routes
     * @returns Either a DataError or the optimized routes
     */
    execute(command: OptimizeRoutesCommand): Promise<Either<DataError, OptimizeRoutesResult>>;
}
