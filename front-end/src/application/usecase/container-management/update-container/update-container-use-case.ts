// UpdateContainerUseCase.ts
// Use case contract for updating a container

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { UpdateContainerCommand } from './update-container-command';
import type { UpdateContainerResult } from './update-container-result';

/**
 * Use case for updating an existing container in the system.
 * Input: containerId and fields to update
 * Output: Container (entity)
 */

/**
 * Use case contract for updating a container
 */
export interface UpdateContainerUseCase {
    /**
     * Handles the update of an existing container
     * @param command Data needed to update the container
     * @returns Either a DataError or the updated Container
     */
    execute(command: UpdateContainerCommand): Promise<Either<DataError, UpdateContainerResult>>;
}

// Re-export types for convenience
export type { UpdateContainerCommand } from './update-container-command';
export type { UpdateContainerResult } from './update-container-result';
