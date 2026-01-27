// GetContainerUseCase.ts
// Use case contract for getting a single container by id

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Container } from '../../../domain/entity/Container';

/**
 * Use case for obtaining a single container by its identifier.
 * Input: containerId
 * Output: Container (entity)
 */

// Command object for input data
export interface GetContainerCommand {
    containerId: string;
}

// Result type for the use case
export type GetContainerResult = Container;

// Use case contract
export interface GetContainerUseCase {
    /**
     * Handles retrieving a single container by id
     *
     * @param command Data needed to retrieve the container
     * @returns Either a DataError or the Container entity
     */
    execute(command: GetContainerCommand): Promise<Either<DataError, GetContainerResult>>;
}
