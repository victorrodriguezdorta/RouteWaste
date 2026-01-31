// ValidateInfrastructurePlanUseCase.ts
// Use case contract for validating an infrastructure plan

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for validating an infrastructure plan.
 * Checks capacities, statuses and budget constraints.
 * Input: planId
 * Output: validation result (boolean)
 */

// Command object for input data
export interface ValidateInfrastructurePlanCommand {
    planId: string;
}

// Result type for the use case
export type ValidateInfrastructurePlanResult = boolean;

// Use case contract
export interface ValidateInfrastructurePlanUseCase {
    /**
     * Handles the validation of an infrastructure plan
     *
     * @param command Data needed to validate the infrastructure plan
     * @returns Either a DataError or a boolean indicating if the plan is valid
     */
    execute(command: ValidateInfrastructurePlanCommand): Promise<Either<DataError, ValidateInfrastructurePlanResult>>;
}
