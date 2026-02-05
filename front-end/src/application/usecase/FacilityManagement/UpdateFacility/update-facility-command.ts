import { FacilityType } from '../../../../domain/enumerate/facility-type';
import { FacilityStatus } from '../../../../domain/enumerate/facility-status';
import { Location } from '../../../../domain/valueobject/location/location';
import { Capacity } from '../../../../domain/valueobject/demand/capacity';
import { OpeningFixedCost } from '../../../../domain/valueobject/cost/opening-fixed-cost';
import type { UllUUID } from '@ull-tfg/ull-tfg-typescript';

// Command object for input data
export interface UpdateFacilityCommand {
    facilityId: UllUUID;
    updatedFields: Partial<{
        facilityType: FacilityType;
        location: Location;
        capacity: Capacity;
        openingFixedCost: OpeningFixedCost;
        status: FacilityStatus;
    }>;
}