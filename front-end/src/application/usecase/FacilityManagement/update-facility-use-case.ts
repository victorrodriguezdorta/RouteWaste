// UpdateFacilityUseCase.ts
// Use case contract for updating a facility

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Facility } from '../../../domain/entity/facility';
import { FacilityType } from '../../../domain/enumerate/facility-type';
import { FacilityStatus } from '../../../domain/enumerate/facility-status';
import { Location } from '../../../domain/valueobject/location/location';
import { Capacity } from '../../../domain/valueobject/demand/capacity';
import { OpeningFixedCost } from '../../../domain/valueobject/cost/opening-fixed-cost';

/**
 * Use case for updating an existing facility in the system.
 * Input: facilityId and fields to update
 * Output: Facility (entity)
 */

// Command object for input data
export interface UpdateFacilityCommand {
    facilityId: string;
    updatedFields: Partial<{
        facilityType: FacilityType;
        location: Location;
        capacity: Capacity;
        openingFixedCost: OpeningFixedCost;
        status: FacilityStatus;
    }>;
}

// Result type for the use case
export type UpdateFacilityResult = Facility;

// Use case contract
export interface UpdateFacilityUseCase {
    /**
     * Handles the update of an existing facility
     *
     * @param command Data needed to update the facility
     * @returns Either a DataError or the updated Facility
     */
    execute(command: UpdateFacilityCommand): Promise<Either<DataError, UpdateFacilityResult>>;
}
