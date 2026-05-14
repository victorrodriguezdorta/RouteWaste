package es.ull.project.domain.readmodel;

import es.ull.project.domain.entity.InfrastructurePlan;
import java.util.List;

/**
 * Read-only aggregate returned for dashboard or home-screen summaries.
 */
public record ApplicationOverview(
        long containerCount,
        long vehicleCount,
        long facilityCount,
        long infrastructurePlanCount,
        List<InfrastructurePlan> recentInfrastructurePlans) {

    /**
     * Canonical constructor ensuring a non-null, immutable list of recent plans.
     */
    public ApplicationOverview {
        recentInfrastructurePlans = recentInfrastructurePlans == null ? List.of() : List.copyOf(recentInfrastructurePlans);
    }
}
