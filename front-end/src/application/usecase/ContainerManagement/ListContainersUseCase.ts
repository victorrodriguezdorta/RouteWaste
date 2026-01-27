// ListContainersUseCase.ts
// Use case contract for listing all containers

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Container } from '../../../domain/entity/Container';

/**
 * Use case for listing all containers in the system.
 * Input: none (optionally pagination params)
 * Output: Container[] (entity array)
 */

// Command object for input data (optional pagination)
export interface ListContainersCommand {
    page?: number;
    pageSize?: number;
}

// Result type for the use case
export type ListContainersResult = Container[];

// Use case contract
export interface ListContainersUseCase {
    /**
     * Handles listing all containers
     *
     * @param command Optional pagination parameters
     * @returns Either a DataError or a list of Container entities
     */
    execute(command?: ListContainersCommand): Promise<Either<DataError, ListContainersResult>>;
}
