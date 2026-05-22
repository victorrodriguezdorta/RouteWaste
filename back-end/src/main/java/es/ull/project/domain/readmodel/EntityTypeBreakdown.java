package es.ull.project.domain.readmodel;

import es.ull.project.domain.valueobject.page.TotalElements;
import java.util.List;

/**
 * Aggregate counts for a primary entity: total and per-type breakdown.
 */
public record EntityTypeBreakdown(
        TotalElements total,
        List<EntityTypeCount> byType) {

    /**
     * Canonical constructor ensuring a non-null, immutable list of type counts.
     */
    public EntityTypeBreakdown {
        byType = byType == null ? List.of() : List.copyOf(byType);
    }
}
