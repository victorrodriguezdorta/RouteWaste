import type { UpdateServiceAssignmentCommand } from './update-service-assignment-command';
import type { UpdateServiceAssignmentResult } from './update-service-assignment-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { UpdateServiceAssignmentCommand } from './update-service-assignment-command';
export type { UpdateServiceAssignmentResult } from './update-service-assignment-result';

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
