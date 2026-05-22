package es.ull.project.domain.readmodel;

import es.ull.project.domain.valueobject.page.TotalElements;
import es.ull.project.domain.valueobject.statistics.EntityTypeName;

/**
 * Count for a single enumerated type (e.g. vehicle or waste type).
 */
public record EntityTypeCount(EntityTypeName type, TotalElements count) {
}
