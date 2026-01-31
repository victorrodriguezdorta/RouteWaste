// ListVehiclesUseCase.ts
// Use case contract for listing all vehicles

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Vehicle } from '../../../domain/entity/vehicle';

/**
 * Use case for listing all vehicles in the system.
 * Input: none (optionally pagination params)
 * Output: Vehicle[] (entity array)
 */

// Command object for input data (optional pagination)
export interface ListVehiclesCommand {
    page?: number;
    pageSize?: number;
}

// Result type for the use case
export type ListVehiclesResult = Vehicle[];

// Use case contract
export interface ListVehiclesUseCase {
    /**
     * Handles listing all vehicles
     *
     * @param command Optional pagination parameters
     * @returns Either a DataError or a list of Vehicle entities
     */
    execute(command?: ListVehiclesCommand): Promise<Either<DataError, ListVehiclesResult>>;
}
