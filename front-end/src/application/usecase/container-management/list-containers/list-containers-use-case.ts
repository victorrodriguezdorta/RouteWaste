import type { ListContainersCommand } from './list-containers-command';
import type { ListContainersResult } from './list-containers-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { ListContainersCommand } from './list-containers-command';
export type { ListContainersResult } from './list-containers-result';

// ListContainersUseCase.ts
// Use case contract for listing all containers

/**
 * Use case for listing all containers in the system.
 * Input: none (optionally pagination params)
 * Output: Container[] (entity array)
 */

/**
 * Use case contract for listing all containers
 */
export interface ListContainersUseCase {
    /**
     * Handles listing all containers
     * @param command Optional pagination parameters
     * @returns Either a DataError or a list of Container entities
     */
    execute(command?: ListContainersCommand): Promise<Either<DataError, ListContainersResult>>;
}



