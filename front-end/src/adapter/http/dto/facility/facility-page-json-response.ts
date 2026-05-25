import type { FacilityJsonResponse } from './facility-json-response';
import type { EntityStatisticsJsonResponse } from '@/adapter/http/dto/common/entity-statistics-json-response';

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
  statistics?: EntityStatisticsJsonResponse;
}
