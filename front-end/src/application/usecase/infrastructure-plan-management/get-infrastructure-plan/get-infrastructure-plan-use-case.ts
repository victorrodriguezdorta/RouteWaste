import type { GetInfrastructurePlanCommand } from '@/application/model/infrastructure-plan-management/get-infrastructure-plan/get-infrastructure-plan-command';
import type { GetInfrastructurePlanResult } from '@/application/model/infrastructure-plan-management/get-infrastructure-plan/get-infrastructure-plan-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for obtaining a single infrastructure plan by its identifier.
 */
export interface GetInfrastructurePlanUseCase {
    /**
     * Handles retrieving a single infrastructure plan by id
     * @param command Data needed to retrieve the infrastructure plan
     * @returns Either a DataError or the InfrastructurePlan entity
     */
    execute(command: GetInfrastructurePlanCommand): Promise<Either<DataError, GetInfrastructurePlanResult>>;
}

export type { GetInfrastructurePlanCommand } from '@/application/model/infrastructure-plan-management/get-infrastructure-plan/get-infrastructure-plan-command';
export type { GetInfrastructurePlanResult } from '@/application/model/infrastructure-plan-management/get-infrastructure-plan/get-infrastructure-plan-result';
