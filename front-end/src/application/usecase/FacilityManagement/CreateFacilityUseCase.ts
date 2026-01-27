// CreateFacilityUseCase.ts
// Use case contract for creating a new facility

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Facility } from '../../../domain/entity/Facility';
import { FacilityType } from '../../../domain/enumerate/FacilityType';
import { FacilityStatus } from '../../../domain/enumerate/FacilityStatus';
import { Location } from '../../../domain/valueobject/location/Location';
import { Capacity } from '../../../domain/valueobject/demand/Capacity';
import { OpeningFixedCost } from '../../../domain/valueobject/cost/OpeningFixedCost';

/**
 * Use case for registering a new facility in the system.
 * Input: facilityType, location, capacity, openingFixedCost, status
 * Output: Facility (entity)
 */

// Command object for input data
export interface CreateFacilityCommand {
    facilityType: FacilityType;
    location: Location;
    capacity: Capacity;
    openingFixedCost: OpeningFixedCost;
    status: FacilityStatus;
}

// Result type for the use case
export type CreateFacilityResult = Facility;

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
