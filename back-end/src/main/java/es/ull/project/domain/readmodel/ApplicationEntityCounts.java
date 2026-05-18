package es.ull.project.domain.readmodel;

import es.ull.project.domain.valueobject.page.TotalElements;

/**
 * Entity totals shown in the application overview.
 */
public record ApplicationEntityCounts(
        TotalElements containers,
        TotalElements vehicles,
        TotalElements facilities,
        TotalElements infrastructurePlans) {
}
