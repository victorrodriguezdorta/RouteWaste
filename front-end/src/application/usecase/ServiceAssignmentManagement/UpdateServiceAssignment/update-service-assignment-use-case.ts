// Re-export types for convenience
export type { UpdateServiceAssignmentCommand } from './update-service-assignment-command';
export type { UpdateServiceAssignmentResult } from './update-service-assignment-result';
// UpdateServiceAssignmentUseCase.ts
// Use case contract for updating a service assignment

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { UpdateServiceAssignmentCommand } from './update-service-assignment-command';
import type { UpdateServiceAssignmentResult } from './update-service-assignment-result';

// Use case contract
export interface UpdateServiceAssignmentUseCase {
    /**
     * Handles the update of an existing service assignment
     *
     * @param command Data needed to update the service assignment
     * @returns Either a DataError or the updated ServiceAssignment
     */
    execute(command: UpdateServiceAssignmentCommand): Promise<Either<DataError, UpdateServiceAssignmentResult>>;
}
