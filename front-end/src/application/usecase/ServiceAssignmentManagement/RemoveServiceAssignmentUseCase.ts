// RemoveServiceAssignmentUseCase.ts
// Use case contract for removing a service assignment

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for removing a service assignment from the system.
 * Input: assignmentId
 * Output: success confirmation (boolean)
 */

// Command object for input data
export interface RemoveServiceAssignmentCommand {
    assignmentId: string;
}

// Result type for the use case
export type RemoveServiceAssignmentResult = boolean;

// Use case contract
export interface RemoveServiceAssignmentUseCase {
    /**
     * Handles the removal of a service assignment
     *
     * @param command Data needed to remove the service assignment
     * @returns Either a DataError or a success confirmation
     */
    execute(command: RemoveServiceAssignmentCommand): Promise<Either<DataError, RemoveServiceAssignmentResult>>;
}
