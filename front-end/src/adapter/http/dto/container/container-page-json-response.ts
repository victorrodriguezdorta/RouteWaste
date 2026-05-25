import type { ContainerJsonResponse } from './container-json-response';
import type { EntityStatisticsJsonResponse } from '@/adapter/http/dto/common/entity-statistics-json-response';

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
