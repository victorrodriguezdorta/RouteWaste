import type { EntityStatisticsJsonResponse } from '../common/entity-statistics-json-response';
import type { ContainerJsonResponse } from './container-json-response';

/**
 * Paginated response DTO for containers endpoint.
 */
export interface ContainerPageJsonResponse {
  content: ContainerJsonResponse[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  statistics?: EntityStatisticsJsonResponse;
}
