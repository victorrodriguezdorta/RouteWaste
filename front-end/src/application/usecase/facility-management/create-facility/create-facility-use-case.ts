import type { CreateFacilityCommand } from '@/application/model/facility-management/create-facility/create-facility-command';
import type { CreateFacilityResult } from '@/application/model/facility-management/create-facility/create-facility-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * Use case contract for creating a new facility
 */
export interface CreateFacilityUseCase {
    /**
     * Handles the creation of a new facility
     *
     * @param command Data needed to create the facility
     * @returns Either a DataError or the created Facility
     */
    execute(command: CreateFacilityCommand): Promise<Either<DataError, CreateFacilityResult>>;
}

export type { CreateFacilityCommand } from '@/application/model/facility-management/create-facility/create-facility-command';
export type { CreateFacilityResult } from '@/application/model/facility-management/create-facility/create-facility-result';
