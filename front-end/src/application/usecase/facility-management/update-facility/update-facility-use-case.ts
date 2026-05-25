import type { UpdateFacilityCommand } from '@/application/model/facility-management/update-facility/update-facility-command';
import type { UpdateFacilityResult } from '@/application/model/facility-management/update-facility/update-facility-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for updating an existing facility in the system.
 */
export interface UpdateFacilityUseCase {
    /**
     * Handles the update of an existing facility
     *
     * @param command Data needed to update the facility
     * @returns Either a DataError or the updated Facility
     */
    execute(command: UpdateFacilityCommand): Promise<Either<DataError, UpdateFacilityResult>>;
}

export type { UpdateFacilityCommand } from '@/application/model/facility-management/update-facility/update-facility-command';
export type { UpdateFacilityResult } from '@/application/model/facility-management/update-facility/update-facility-result';
