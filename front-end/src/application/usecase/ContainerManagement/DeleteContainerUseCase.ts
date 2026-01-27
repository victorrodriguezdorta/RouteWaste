// DeleteContainerUseCase.ts
// Use case contract for deleting a container

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for deleting a container from the system.
 * Input: containerId
 * Output: success confirmation (boolean)
 */

// Command object for input data
export interface DeleteContainerCommand {
    containerId: string;
}

// Result type for the use case
export type DeleteContainerResult = boolean;

// Use case contract
export interface DeleteContainerUseCase {
    /**
     * Handles the deletion of a container
     *
     * @param command Data needed to delete the container
     * @returns Either a DataError or a success confirmation
     */
    execute(command: DeleteContainerCommand): Promise<Either<DataError, DeleteContainerResult>>;
}
