import type { EntityTypeCount } from './entity-type-count';

/**
 * Global entity counts returned in paginated list API responses.
 */
export interface EntityTypeStatistics {
  total: number;
  byType: EntityTypeCount[];
}
