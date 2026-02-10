// Use case contract for creating a new infrastructure plan

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CreateInfrastructurePlanCommand } from './create-infrastructure-plan-command';
import type { CreateInfrastructurePlanResult } from './create-infrastructure-plan-result';

/**
 * Use case for creating a new infrastructure plan in the system.
 * Input: period, maxBudget, servicePolicies (optional)
 * Output: InfrastructurePlan (entity)
 */
export interface CreateInfrastructurePlanUseCase {
    /**
     * Handles the creation of a new infrastructure plan
     * @param command Data needed to create the infrastructure plan
     * @returns Either a DataError or the created InfrastructurePlan
     */
    execute(command: CreateInfrastructurePlanCommand): Promise<Either<DataError, CreateInfrastructurePlanResult>>;
}

// Re-export types for convenience
export type { CreateInfrastructurePlanCommand } from './create-infrastructure-plan-command';
export type { CreateInfrastructurePlanResult } from './create-infrastructure-plan-result';
