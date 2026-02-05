// Re-export types for convenience
export type { CreateFacilityCommand } from './create-facility-command';
export type { CreateFacilityResult } from './create-facility-result';
// CreateFacilityUseCase.ts
// Use case contract for creating a new facility

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { CreateFacilityCommand } from './create-facility-command';
import type { CreateFacilityResult } from './create-facility-result';

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
