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
 * Output: paginated container result
 */

/**
 * Use case contract for listing all containers
 */
export interface ListContainersUseCase {
    /**
     * Handles listing all containers
     * @param command Optional pagination parameters
     * @returns Either a DataError or a paginated list of Container entities
     */
    execute(command?: ListContainersCommand): Promise<Either<DataError, ListContainersResult>>;
}



