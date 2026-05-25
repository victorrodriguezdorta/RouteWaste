import type { ListInfrastructurePlansCommand } from '@/application/model/infrastructure-plan-management/list-infrastructure-plans/list-infrastructure-plans-command';
import type { ListInfrastructurePlansResult } from '@/application/model/infrastructure-plan-management/list-infrastructure-plans/list-infrastructure-plans-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for listing all infrastructure plans in the system.
 */
export interface ListInfrastructurePlansUseCase {
    /**
     * Handles listing all infrastructure plans
     * @param command Optional pagination parameters
     * @returns Either a DataError or a list of InfrastructurePlan entities
     */
    execute(command?: ListInfrastructurePlansCommand): Promise<Either<DataError, ListInfrastructurePlansResult>>;
}

export type { ListInfrastructurePlansCommand } from '@/application/model/infrastructure-plan-management/list-infrastructure-plans/list-infrastructure-plans-command';
export type { ListInfrastructurePlansResult } from '@/application/model/infrastructure-plan-management/list-infrastructure-plans/list-infrastructure-plans-result';
