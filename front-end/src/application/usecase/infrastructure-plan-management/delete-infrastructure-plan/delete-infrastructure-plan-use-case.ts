import type { DeleteInfrastructurePlanCommand } from './delete-infrastructure-plan-command';
import type { DeleteInfrastructurePlanResult } from './delete-infrastructure-plan-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { DeleteInfrastructurePlanCommand } from './delete-infrastructure-plan-command';
export type { DeleteInfrastructurePlanResult } from './delete-infrastructure-plan-result';

// Use case contract for deleting an infrastructure plan

/**
 * Use case contract for deleting an infrastructure plan
 */
export interface DeleteInfrastructurePlanUseCase {
    /**
     * Handles the deletion of an infrastructure plan
     * @param command Data needed to delete the infrastructure plan
     * @returns Either a DataError or a success confirmation
     */
    execute(command: DeleteInfrastructurePlanCommand): Promise<Either<DataError, DeleteInfrastructurePlanResult>>;
}

