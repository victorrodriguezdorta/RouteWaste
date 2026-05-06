package es.ull.project.adapter.rest.response.infrastructureplan;

import java.util.List;

/**
 * Paginated response payload for infrastructure plans endpoint.
 */
public class InfrastructurePlanPageResponseBody {

    public List<InfrastructurePlanListResponseBody> content;
    public long totalElements;
    public int totalPages;
    public int page;
    public int size;
    public int numberOfElements;
    public boolean first;
    public boolean last;
}