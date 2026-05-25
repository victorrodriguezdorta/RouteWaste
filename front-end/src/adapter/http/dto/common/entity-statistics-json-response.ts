import type { EntityTypeCountJsonResponse } from './entity-type-count-json-response';

/**
 * Statistics block in paginated list responses (matches backend EntityStatisticsResponseBody).
 */
export interface EntityStatisticsJsonResponse {
  total: number;
  byType: EntityTypeCountJsonResponse[];
}
