// DeleteContainerUseCase.ts
// Use case contract for deleting a container

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { DeleteContainerCommand } from './delete-container-command';
import type { DeleteContainerResult } from './delete-container-result';

/**
 * Use case for deleting a container from the system.
 * Input: containerId
 * Output: success confirmation (boolean)
 */

/**
 * Use case contract for deleting a container
 */
export interface DeleteContainerUseCase {
    /**
     * Handles the deletion of a container
     * @param command Data needed to delete the container
     * @returns Either a DataError or a success confirmation
     */
    execute(command: DeleteContainerCommand): Promise<Either<DataError, DeleteContainerResult>>;
}

// Re-export types for convenience
export type { DeleteContainerCommand } from './delete-container-command';
export type { DeleteContainerResult } from './delete-container-result';
