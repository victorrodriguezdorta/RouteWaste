// Re-export types for convenience
export type { ListServiceAssignmentsCommand } from './list-service-assignments-command';
export type { ListServiceAssignmentsResult } from './list-service-assignments-result';
// ListServiceAssignmentsUseCase.ts
// Use case contract for listing all service assignments

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { ListServiceAssignmentsCommand } from './list-service-assignments-command';
import type { ListServiceAssignmentsResult } from './list-service-assignments-result';


/**
 * Use case for listing all service assignments in the system.
 * Input: none (optionally pagination params or filters)
 * Output: ServiceAssignment[] (entity array)
 */
export interface ListServiceAssignmentsUseCase {
    /**
     * Handles listing all service assignments
     *
     * @param command Optional pagination and filter parameters
     * @returns Either a DataError or a list of ServiceAssignment entities
     */
    execute(command?: ListServiceAssignmentsCommand): Promise<Either<DataError, ListServiceAssignmentsResult>>;
}
