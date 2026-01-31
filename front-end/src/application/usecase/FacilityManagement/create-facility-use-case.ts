// CreateFacilityUseCase.ts
// Use case contract for creating a new facility

import type { Either, DataError } from '@ull-tfg/ull-tfg-typescript';
import { Facility } from '../../../domain/entity/facility';
import { FacilityType } from '../../../domain/enumerate/facility-type';
import { FacilityStatus } from '../../../domain/enumerate/facility-status';
import { Location } from '../../../domain/valueobject/location/location';
import { Capacity } from '../../../domain/valueobject/demand/capacity';
import { OpeningFixedCost } from '../../../domain/valueobject/cost/opening-fixed-cost';

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
