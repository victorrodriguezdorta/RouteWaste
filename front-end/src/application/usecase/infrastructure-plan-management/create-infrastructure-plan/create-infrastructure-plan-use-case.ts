import type { CreateInfrastructurePlanCommand } from './create-infrastructure-plan-command';
import type { CreateInfrastructurePlanResult } from './create-infrastructure-plan-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { CreateInfrastructurePlanCommand } from './create-infrastructure-plan-command';
export type { CreateInfrastructurePlanResult } from './create-infrastructure-plan-result';

// CreateInfrastructurePlanUseCase.ts
// Use case contract for creating a new infrastructure plan

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

