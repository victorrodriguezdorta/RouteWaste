import type { GetInfrastructurePlanCommand } from './get-infrastructure-plan-command';
import type { GetInfrastructurePlanResult } from './get-infrastructure-plan-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { GetInfrastructurePlanCommand } from './get-infrastructure-plan-command';
export type { GetInfrastructurePlanResult } from './get-infrastructure-plan-result';

// GetInfrastructurePlanUseCase.ts
// Use case contract for getting an infrastructure plan by id

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


