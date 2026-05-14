package es.ull.project.adapter.rest.response.overview;

import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanListResponseBody;
import java.util.List;

/**
 * JSON payload for GET /api/v1/application-overview: counts plus a short list of recent plans.
 */
public class ApplicationOverviewResponseBody {

    /**
     * Totals per aggregate type.
     */
    public ApplicationEntityCountsResponseBody entityCounts;

    /**
     * Up to three infrastructure plans with the latest {@code executedAt} (descending); may be empty.
     */
    public List<InfrastructurePlanListResponseBody> recentInfrastructurePlans;
}
