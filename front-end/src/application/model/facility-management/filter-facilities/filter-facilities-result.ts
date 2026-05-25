import type { Facility } from '@/domain/entity/facility';

/** Result type for the use case. */
export interface FilterFacilitiesResult {
    items: Facility[];
}
