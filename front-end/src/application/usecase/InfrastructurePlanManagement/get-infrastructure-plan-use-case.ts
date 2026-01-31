// GetInfrastructurePlanUseCase.ts
// Use case contract for getting a single infrastructure plan by id

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { InfrastructurePlan } from '../../../domain/entity/infrastructure-plan';

/**
 * Use case for obtaining a single infrastructure plan by its identifier.
 * Input: planId
 * Output: InfrastructurePlan (entity)
 */

// Command object for input data
export interface GetInfrastructurePlanCommand {
    planId: string;
}

// Result type for the use case
export type GetInfrastructurePlanResult = InfrastructurePlan;

// Use case contract
export interface GetInfrastructurePlanUseCase {
    /**
     * Handles retrieving a single infrastructure plan by id
     *
     * @param command Data needed to retrieve the infrastructure plan
     * @returns Either a DataError or the InfrastructurePlan entity
     */
    execute(command: GetInfrastructurePlanCommand): Promise<Either<DataError, GetInfrastructurePlanResult>>;
}
