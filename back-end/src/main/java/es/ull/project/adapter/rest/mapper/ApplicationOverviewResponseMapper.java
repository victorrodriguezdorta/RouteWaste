package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanListResponseBody;
import es.ull.project.adapter.rest.response.overview.ApplicationOverviewResponseBody;
import es.ull.project.domain.readmodel.ApplicationEntityCounts;
import es.ull.project.domain.readmodel.ApplicationOverview;
import es.ull.project.domain.valueobject.page.TotalElements;
import java.util.List;

/**
 * Maps {@link ApplicationOverview} to REST response DTOs.
 */
public class ApplicationOverviewResponseMapper {

    private static final String UTILITY_CLASS_EXCEPTION_MESSAGE = "Utility class cannot be instantiated";

    /**
     * Prevents instantiation of this utility mapper.
     */
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
        ApplicationEntityCounts counts = new ApplicationEntityCounts(
                new TotalElements(overview.containerCount()),
                new TotalElements(overview.vehicleCount()),
                new TotalElements(overview.facilityCount()),
                new TotalElements(overview.infrastructurePlanCount()));
        List<InfrastructurePlanListResponseBody> recent = overview.recentInfrastructurePlans().stream()
                .map(InfrastructurePlanListResponseMapper::toResponseBody)
                .toList();
        ApplicationOverviewResponseBody body = new ApplicationOverviewResponseBody();
        body.entityCounts = counts;
        body.recentInfrastructurePlans = recent;
        return body;
    }
}
