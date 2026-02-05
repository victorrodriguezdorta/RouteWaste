import { FacilityType } from '../../../../domain/enumerate/facility-type';
import { FacilityStatus } from '../../../../domain/enumerate/facility-status';

// Command object for input data
export interface FilterFacilitiesCommand {
    facilityType?: FacilityType;
    status?: FacilityStatus;
    minCapacity?: number;
    maxCapacity?: number;
}
