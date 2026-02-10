// Use case contract for getting an infrastructure plan by id

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { GetInfrastructurePlanCommand } from './get-infrastructure-plan-command';
import type { GetInfrastructurePlanResult } from './get-infrastructure-plan-result';

/**
 * Use case for obtaining a single infrastructure plan by its identifier.
 * Input: planId
 * Output: InfrastructurePlan (entity)
 */

export interface GetInfrastructurePlanUseCase {
    /**
     * Handles retrieving a single infrastructure plan by id
     * @param command Data needed to retrieve the infrastructure plan
     * @returns Either a DataError or the InfrastructurePlan entity
     */
    execute(command: GetInfrastructurePlanCommand): Promise<Either<DataError, GetInfrastructurePlanResult>>;
}

// Re-export types for convenience
export type { GetInfrastructurePlanCommand } from './get-infrastructure-plan-command';
export type { GetInfrastructurePlanResult } from './get-infrastructure-plan-result';
