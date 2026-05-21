package es.ull.project.domain.readmodel;

import es.ull.project.domain.valueobject.page.TotalElements;
import java.util.List;

/**
 * Aggregate counts for a primary entity: total and per-type breakdown.
 */
public record EntityTypeBreakdown(
        TotalElements total,
        List<TypeCount> byType) {

    /**
     * Count for a single enumerated type (e.g. vehicle or waste type).
     */
    public record TypeCount(String type, TotalElements count) {
    }

    /**
     * Canonical constructor ensuring a non-null, immutable list of type counts.
     */
    public EntityTypeBreakdown {
        byType = byType == null ? List.of() : List.copyOf(byType);
    }
}
