// Re-export types for convenience
export type { AssignContainerToFacilityCommand } from './assign-container-to-facility-command';
export type { AssignContainerToFacilityResult } from './assign-container-to-facility-result';
// AssignContainerToFacilityUseCase.ts
// Use case contract for assigning a container to a facility

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import type { AssignContainerToFacilityCommand } from './assign-container-to-facility-command';
import type { AssignContainerToFacilityResult } from './assign-container-to-facility-result';


// Use case contract
export interface AssignContainerToFacilityUseCase {
    /**
     * Handles the assignment of a container to a facility
     *
     * @param command Data needed to create the service assignment
     * @returns Either a DataError or the created ServiceAssignment
     */
    execute(command: AssignContainerToFacilityCommand): Promise<Either<DataError, AssignContainerToFacilityResult>>;
}
