import type { VehicleJsonResponse } from './vehicle-json-response';

/**
 * Paginated response DTO for vehicles endpoint.
 */
export interface VehiclePageJsonResponse {
  content: VehicleJsonResponse[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
}
