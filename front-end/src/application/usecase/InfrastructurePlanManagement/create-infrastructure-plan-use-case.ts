// CreateInfrastructurePlanUseCase.ts
// Use case contract for creating a new infrastructure plan

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { InfrastructurePlan } from '../../../domain/entity/infrastructure-plan';
import { PlanningPeriod } from '../../../domain/valueobject/time/planning-period';
import { MaximumBudget } from '../../../domain/valueobject/cost/maximum-budget';
import { ServicePolicies } from '../../../domain/valueobject/policy/service-policies';

/**
 * Use case for creating a new infrastructure plan in the system.
 * Input: period, maxBudget, servicePolicies (optional)
 * Output: InfrastructurePlan (entity)
 */

// Command object for input data
export interface CreateInfrastructurePlanCommand {
    period: PlanningPeriod;
    maxBudget: MaximumBudget;
    servicePolicies?: ServicePolicies | null;
}

// Result type for the use case
export type CreateInfrastructurePlanResult = InfrastructurePlan;

// Use case contract
export interface CreateInfrastructurePlanUseCase {
    /**
     * Handles the creation of a new infrastructure plan
     *
     * @param command Data needed to create the infrastructure plan
     * @returns Either a DataError or the created InfrastructurePlan
     */
    execute(command: CreateInfrastructurePlanCommand): Promise<Either<DataError, CreateInfrastructurePlanResult>>;
}
