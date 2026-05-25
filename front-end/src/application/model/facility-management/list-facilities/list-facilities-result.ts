import type { Facility } from '@/domain/entity/facility';
import type { EntityTypeStatistics } from '@/domain/read-model/entity-type-statistics';

/** Result type for listing facilities. */
export interface ListFacilitiesResult {
    items: Facility[];
    totalElements: number;
    totalPages: number;
    page: number;
    size: number;
    statistics?: EntityTypeStatistics;
}
