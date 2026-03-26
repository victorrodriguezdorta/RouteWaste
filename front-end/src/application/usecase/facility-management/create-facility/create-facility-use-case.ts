import type { CreateFacilityCommand } from './create-facility-command';
import type { CreateFacilityResult } from './create-facility-result';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

export type { CreateFacilityCommand } from './create-facility-command';
export type { CreateFacilityResult } from './create-facility-result';

// CreateFacilityUseCase.ts
// Use case contract for creating a new facility

// Use case contract
export interface CreateFacilityUseCase {
    /**
     * Handles the creation of a new facility
     *
     * @param command Data needed to create the facility
     * @returns Either a DataError or the created Facility
     */
    execute(command: CreateFacilityCommand): Promise<Either<DataError, CreateFacilityResult>>;
}



