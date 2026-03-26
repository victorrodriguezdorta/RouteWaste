import type { GetContainerCommand } from './get-container-command';
import type { GetContainerResult } from './get-container-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { GetContainerCommand } from './get-container-command';
export type { GetContainerResult } from './get-container-result';

// GetContainerUseCase.ts
// Use case contract for getting a single container by id

/**
 * Use case for obtaining a single container by its identifier.
 * Input: containerId
 * Output: Container (entity)
 */

/**
 * Use case contract for getting a single container by id
 */
export interface GetContainerUseCase {
    /**
     * Handles retrieving a single container by id
     * @param command Data needed to retrieve the container
     * @returns Either a DataError or the Container entity
     */
    execute(command: GetContainerCommand): Promise<Either<DataError, GetContainerResult>>;
}



