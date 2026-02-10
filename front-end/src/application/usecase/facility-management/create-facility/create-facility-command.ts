import { FacilityType } from '../../../../domain/enumerate/facility-type';
import { FacilityStatus } from '../../../../domain/enumerate/facility-status';
import { Location } from '../../../../domain/valueobject/location/location';
import { Capacity } from '../../../../domain/valueobject/demand/capacity';
import { OpeningFixedCost } from '../../../../domain/valueobject/cost/opening-fixed-cost';

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