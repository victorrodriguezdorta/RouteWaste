/**
 * Statistics block in paginated list responses (matches backend EntityStatisticsResponseBody).
 */
export interface EntityTypeCountJsonResponse {
  type: string;
  count: number;
}

export interface EntityStatisticsJsonResponse {
  total: number;
  byType: EntityTypeCountJsonResponse[];
}
