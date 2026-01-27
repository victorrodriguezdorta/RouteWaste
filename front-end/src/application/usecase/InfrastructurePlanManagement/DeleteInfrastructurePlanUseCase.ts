// DeleteInfrastructurePlanUseCase.ts
// Use case contract for deleting an infrastructure plan

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for deleting an infrastructure plan from the system.
 * Input: planId
 * Output: success confirmation (boolean)
 */

// Command object for input data
export interface DeleteInfrastructurePlanCommand {
    planId: string;
}

// Result type for the use case
export type DeleteInfrastructurePlanResult = boolean;

// Use case contract
export interface DeleteInfrastructurePlanUseCase {
    /**
     * Handles the deletion of an infrastructure plan
     *
     * @param command Data needed to delete the infrastructure plan
     * @returns Either a DataError or a success confirmation
     */
    execute(command: DeleteInfrastructurePlanCommand): Promise<Either<DataError, DeleteInfrastructurePlanResult>>;
}
