// UpdateFacilityUseCase.ts
// Use case contract for updating a facility

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Facility } from '../../../domain/entity/Facility';
import { FacilityType } from '../../../domain/enumerate/FacilityType';
import { FacilityStatus } from '../../../domain/enumerate/FacilityStatus';
import { Location } from '../../../domain/valueobject/location/Location';
import { Capacity } from '../../../domain/valueobject/demand/Capacity';
import { OpeningFixedCost } from '../../../domain/valueobject/cost/OpeningFixedCost';

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
