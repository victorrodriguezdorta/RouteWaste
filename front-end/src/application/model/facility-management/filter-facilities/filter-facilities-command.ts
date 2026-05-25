import type { FacilityStatus } from '@/domain/enumerate/facility-status';
import type { FacilityType } from '@/domain/enumerate/facility-type';

/** Command object for filtering facilities. */
export interface FilterFacilitiesCommand {
    facilityType?: FacilityType;
    status?: FacilityStatus;
    minCapacity?: number;
    maxCapacity?: number;
}
