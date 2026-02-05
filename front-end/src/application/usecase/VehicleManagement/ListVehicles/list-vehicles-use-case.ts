// Re-export types for convenience
export type { ListVehiclesCommand } from './list-vehicles-command';
export type { ListVehiclesResult } from './list-vehicles-result';
// ListVehiclesUseCase.ts
// Use case contract for listing all vehicles

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { ListVehiclesCommand } from './list-vehicles-command';
import type { ListVehiclesResult } from './list-vehicles-result';


/**
 * Use case for listing all vehicles in the system.
 * Input: none (optionally pagination params)
 * Output: Vehicle[] (entity array)
 */
export interface ListVehiclesUseCase {
    /**
     * Handles listing all vehicles
     *
     * @param command Optional pagination parameters
     * @returns Either a DataError or a list of Vehicle entities
     */
    execute(command?: ListVehiclesCommand): Promise<Either<DataError, ListVehiclesResult>>;
}
