import type { CreateContainerCommand } from './create-container-command';
import type { CreateContainerResult } from './create-container-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { CreateContainerCommand } from './create-container-command';
export type { CreateContainerResult } from './create-container-result';

// CreateContainerUseCase.ts
// Use case contract for creating a new container

/**
 * Use case for registering a new container in the system.
 * Input: location, waste type, waste demand, service zone
 * Output: Container (entity)
 */

/**
 * use case contract for creating a container
 */
export interface CreateContainerUseCase {
    /**
     * Handles the creation of a new container
     * @param command Data needed to create the container
     * @returns Either a DataError or the created Container
     */
    execute(command: CreateContainerCommand): Promise<Either<DataError, CreateContainerResult>>;
}
