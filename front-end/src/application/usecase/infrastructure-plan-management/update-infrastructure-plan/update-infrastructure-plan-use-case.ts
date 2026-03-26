// Use case contract for updating an infrastructure plan

import type { UpdateInfrastructurePlanCommand } from './update-infrastructure-plan-command';
import type { UpdateInfrastructurePlanResult } from './update-infrastructure-plan-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { UpdateInfrastructurePlanCommand } from './update-infrastructure-plan-command';
export type { UpdateInfrastructurePlanResult } from './update-infrastructure-plan-result';

/**
 * Use case for updating an existing infrastructure plan in the system.
 * Input: planId and fields to update
 * Output: InfrastructurePlan (entity)
 */
export interface UpdateInfrastructurePlanUseCase {
    /**
     * Handles the update of an existing infrastructure plan
     * @param command Data needed to update the infrastructure plan
     * @returns Either a DataError or the updated InfrastructurePlan
     */
    execute(command: UpdateInfrastructurePlanCommand): Promise<Either<DataError, UpdateInfrastructurePlanResult>>;
}

