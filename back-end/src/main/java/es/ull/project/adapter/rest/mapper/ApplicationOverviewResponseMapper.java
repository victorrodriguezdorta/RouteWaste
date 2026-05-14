package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanListResponseBody;
import es.ull.project.adapter.rest.response.overview.ApplicationEntityCountsResponseBody;
import es.ull.project.adapter.rest.response.overview.ApplicationOverviewResponseBody;
import es.ull.project.domain.readmodel.ApplicationOverview;
import java.util.List;

/**
 * Maps {@link ApplicationOverview} to REST response DTOs.
 */
public class ApplicationOverviewResponseMapper {

    private static final String UTILITY_CLASS_EXCEPTION_MESSAGE = "Utility class cannot be instantiated";

    private ApplicationOverviewResponseMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_EXCEPTION_MESSAGE);
    }

    /**
     * Converts a domain overview into the HTTP response body.
     *
     * @param overview domain read model
     * @return response body ready for JSON serialization
     */
    public static ApplicationOverviewResponseBody toResponseBody(ApplicationOverview overview) {
        ApplicationEntityCountsResponseBody counts = new ApplicationEntityCountsResponseBody();
        counts.containers = overview.containerCount();
        counts.vehicles = overview.vehicleCount();
        counts.facilities = overview.facilityCount();
        counts.infrastructurePlans = overview.infrastructurePlanCount();

        List<InfrastructurePlanListResponseBody> recent = overview.recentInfrastructurePlans().stream()
                .map(InfrastructurePlanListResponseMapper::toResponseBody)
                .toList();

        ApplicationOverviewResponseBody body = new ApplicationOverviewResponseBody();
        body.entityCounts = counts;
        body.recentInfrastructurePlans = recent;
        return body;
    }
}
