import type { GetFacilityCommand } from '@/application/model/facility-management/get-facility/get-facility-command';
import type { GetFacilityResult } from '@/application/model/facility-management/get-facility/get-facility-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case for obtaining a single facility by its identifier.
 */
export interface GetFacilityUseCase {
    /**
     * Handles retrieving a single facility by id
     *
     * @param command Data needed to retrieve the facility
     * @returns Either a DataError or the Facility entity
     */
    execute(command: GetFacilityCommand): Promise<Either<DataError, GetFacilityResult>>;
}

export type { GetFacilityCommand } from '@/application/model/facility-management/get-facility/get-facility-command';
export type { GetFacilityResult } from '@/application/model/facility-management/get-facility/get-facility-result';
