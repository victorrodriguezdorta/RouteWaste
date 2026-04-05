import type { FacilityJsonResponse } from './facility-json-response';

/**
 * Paginated response DTO for facilities endpoint.
 */
export interface FacilityPageJsonResponse {
  content: FacilityJsonResponse[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
}
