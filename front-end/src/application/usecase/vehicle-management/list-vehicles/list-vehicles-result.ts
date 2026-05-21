import { Vehicle } from '@/domain/entity/vehicle';
import type { EntityTypeStatistics } from '@/domain/read-model/entity-type-statistics';

// Result type for the use case
export interface ListVehiclesResult {
	items: Vehicle[];
	totalElements: number;
	totalPages: number;
	page: number;
	size: number;
	statistics?: EntityTypeStatistics;
}
