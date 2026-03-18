// Re-export types for convenience
export type { RemoveServiceAssignmentCommand } from './remove-service-assignment-command';
export type { RemoveServiceAssignmentResult } from './remove-service-assignment-result';
// RemoveServiceAssignmentUseCase.ts
// Use case contract for removing a service assignment

import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { RemoveServiceAssignmentCommand } from './remove-service-assignment-command';
import type { RemoveServiceAssignmentResult } from './remove-service-assignment-result';

/**
 * Use case for removing a service assignment from the system.
 * Input: assignmentId
 * Output: success confirmation (boolean)
 */
export interface RemoveServiceAssignmentUseCase {
    /**
     * Handles the removal of a service assignment
     *
     * @param command Data needed to remove the service assignment
     * @returns Either a DataError or a success confirmation
     */
    execute(command: RemoveServiceAssignmentCommand): Promise<Either<DataError, RemoveServiceAssignmentResult>>;
}
