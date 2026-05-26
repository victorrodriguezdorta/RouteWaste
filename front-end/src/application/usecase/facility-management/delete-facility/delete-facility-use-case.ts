import type { DeleteFacilityCommand } from '@/application/model/facility-management/delete-facility/delete-facility-command';
import type { DeleteFacilityResult } from '@/application/model/facility-management/delete-facility/delete-facility-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for deleting a facility from the system.
 */
export interface DeleteFacilityUseCase {
    /**
     * Handles the deletion of a facility
     *
     * @param command Data needed to delete the facility
     * @returns Either a DataError or a success confirmation
     */
    execute(command: DeleteFacilityCommand): Promise<Either<DataError, DeleteFacilityResult>>;
}

export type { DeleteFacilityCommand } from '@/application/model/facility-management/delete-facility/delete-facility-command';
export type { DeleteFacilityResult } from '@/application/model/facility-management/delete-facility/delete-facility-result';
