// UpdateInfrastructurePlanUseCase.ts
// Use case contract for updating an infrastructure plan

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { InfrastructurePlan } from '../../../domain/entity/infrastructure-plan';
import { PlanningPeriod } from '../../../domain/valueobject/time/planning-period';
import { MaximumBudget } from '../../../domain/valueobject/cost/maximum-budget';
import { ServicePolicies } from '../../../domain/valueobject/policy/service-policies';

/**
 * Use case for updating an existing infrastructure plan in the system.
 * Input: planId and fields to update
 * Output: InfrastructurePlan (entity)
 */

// Command object for input data
export interface UpdateInfrastructurePlanCommand {
    planId: string;
    updatedFields: Partial<{
        period: PlanningPeriod;
        maxBudget: MaximumBudget;
        servicePolicies: ServicePolicies | null;
    }>;
}

// Result type for the use case
export type UpdateInfrastructurePlanResult = InfrastructurePlan;

// Use case contract
export interface UpdateInfrastructurePlanUseCase {
    /**
     * Handles the update of an existing infrastructure plan
     *
     * @param command Data needed to update the infrastructure plan
     * @returns Either a DataError or the updated InfrastructurePlan
     */
    execute(command: UpdateInfrastructurePlanCommand): Promise<Either<DataError, UpdateInfrastructurePlanResult>>;
}
