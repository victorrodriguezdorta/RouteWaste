// Use case contract for listing all infrastructure plans

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { ListInfrastructurePlansCommand } from './list-infrastructure-plans-command';
import type { ListInfrastructurePlansResult } from './list-infrastructure-plans-result';

/**
 * Use case for listing all infrastructure plans in the system.
 * Input: none (optionally pagination params)
 * Output: InfrastructurePlan[] (entity array)
 */
export interface ListInfrastructurePlansUseCase {
    /**
     * Handles listing all infrastructure plans
     * @param command Optional pagination parameters
     * @returns Either a DataError or a list of InfrastructurePlan entities
     */
    execute(command?: ListInfrastructurePlansCommand): Promise<Either<DataError, ListInfrastructurePlansResult>>;
}

// Re-export types for convenience
export type { ListInfrastructurePlansCommand } from './list-infrastructure-plans-command';
export type { ListInfrastructurePlansResult } from './list-infrastructure-plans-result';
