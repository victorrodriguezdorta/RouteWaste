// ListContainersUseCase.ts
// Use case contract for listing all containers

import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { ListContainersCommand } from './list-containers-command';
import type { ListContainersResult } from './list-containers-result';

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

// Re-export types for convenience
export type { ListContainersCommand } from './list-containers-command';
export type { ListContainersResult } from './list-containers-result';

