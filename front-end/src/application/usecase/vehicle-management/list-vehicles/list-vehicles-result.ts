import { Vehicle } from '../../../../domain/entity/vehicle';

// Result type for the use case
export interface ListVehiclesResult {
	items: Vehicle[];
	totalElements: number;
	totalPages: number;
	page: number;
	size: number;
}
