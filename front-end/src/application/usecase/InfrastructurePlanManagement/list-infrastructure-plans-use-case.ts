// ListInfrastructurePlansUseCase.ts
// Use case contract for listing all infrastructure plans

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { InfrastructurePlan } from '../../../domain/entity/infrastructure-plan';

/**
 * Use case for listing all infrastructure plans in the system.
 * Input: none (optionally pagination params)
 * Output: InfrastructurePlan[] (entity array)
 */

// Command object for input data (optional pagination)
export interface ListInfrastructurePlansCommand {
    page?: number;
    pageSize?: number;
}

// Result type for the use case
export type ListInfrastructurePlansResult = InfrastructurePlan[];

// Use case contract
export interface ListInfrastructurePlansUseCase {
    /**
     * Handles listing all infrastructure plans
     *
     * @param command Optional pagination parameters
     * @returns Either a DataError or a list of InfrastructurePlan entities
     */
    execute(command?: ListInfrastructurePlansCommand): Promise<Either<DataError, ListInfrastructurePlansResult>>;
}
