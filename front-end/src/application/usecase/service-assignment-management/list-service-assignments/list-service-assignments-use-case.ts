import type { ListServiceAssignmentsCommand } from './list-service-assignments-command';
import type { ListServiceAssignmentsResult } from './list-service-assignments-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { ListServiceAssignmentsCommand } from './list-service-assignments-command';
export type { ListServiceAssignmentsResult } from './list-service-assignments-result';

// ListServiceAssignmentsUseCase.ts
// Use case contract for listing all service assignments


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
