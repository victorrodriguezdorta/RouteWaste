import type { EntityStatisticsJsonResponse } from '@/adapter/http/dto/common/entity-statistics-json-response';
import type { EntityTypeStatistics } from '@/domain/read-model/entity-type-statistics';

/**
 * Maps API statistics JSON to a domain read model.
 */
export function toEntityTypeStatistics(
  json: EntityStatisticsJsonResponse | undefined,
): EntityTypeStatistics | undefined {
  if (!json) {
    return undefined;
  }

  return {
    total: json.total,
    byType: (json.byType ?? []).map((entry) => ({
      type: entry.type,
      count: entry.count,
    })),
  };
}
