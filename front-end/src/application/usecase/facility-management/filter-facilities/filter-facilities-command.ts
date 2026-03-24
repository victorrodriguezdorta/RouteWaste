import { FacilityStatus } from '@/domain/enumerate/facility-status';
import { FacilityType } from '@/domain/enumerate/facility-type';

// Command object for input data
export interface FilterFacilitiesCommand {
    facilityType?: FacilityType;
    status?: FacilityStatus;
    minCapacity?: number;
    maxCapacity?: number;
}
