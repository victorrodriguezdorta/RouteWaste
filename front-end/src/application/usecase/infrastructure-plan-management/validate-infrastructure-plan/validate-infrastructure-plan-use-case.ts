// Use case contract for validating an infrastructure plan

import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';
import type { ValidateInfrastructurePlanCommand } from './validate-infrastructure-plan-command';
import type { ValidateInfrastructurePlanResult } from './validate-infrastructure-plan-result';

/**
 * Use case for validating an infrastructure plan.
 * Checks capacities, statuses and budget constraints.
 * Input: planId
 * Output: validation result (boolean)
 */
export interface ValidateInfrastructurePlanUseCase {
    /**
     * Handles validation of an infrastructure plan
     * @param command Data needed to validate the infrastructure plan
     * @returns Either a DataError or the validation result
     */
    execute(command: ValidateInfrastructurePlanCommand): Promise<Either<DataError, ValidateInfrastructurePlanResult>>;
}

// Re-export types for convenience
export type { ValidateInfrastructurePlanCommand } from './validate-infrastructure-plan-command';
export type { ValidateInfrastructurePlanResult } from './validate-infrastructure-plan-result';

