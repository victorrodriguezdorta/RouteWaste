import type { UpdateContainerCommand } from './update-container-command';
import type { UpdateContainerResult } from './update-container-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { UpdateContainerCommand } from './update-container-command';
export type { UpdateContainerResult } from './update-container-result';

// UpdateContainerUseCase.ts
// Use case contract for updating a container

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


