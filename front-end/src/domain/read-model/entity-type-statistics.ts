/**
 * Global entity counts returned in paginated list API responses.
 */
export interface EntityTypeCount {
  type: string;
  count: number;
}

export interface EntityTypeStatistics {
  total: number;
  byType: EntityTypeCount[];
}
