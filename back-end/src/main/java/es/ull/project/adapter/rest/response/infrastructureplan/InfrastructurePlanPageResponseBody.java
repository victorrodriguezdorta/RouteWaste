package es.ull.project.adapter.rest.response.infrastructureplan;

import es.ull.project.domain.valueobject.page.NumberOfElements;
import es.ull.project.domain.valueobject.page.PageFlag;
import es.ull.project.domain.valueobject.page.PageNumber;
import es.ull.project.domain.valueobject.page.PageSize;
import es.ull.project.domain.valueobject.page.TotalElements;
import es.ull.project.domain.valueobject.page.TotalPages;
import java.util.List;

/**
 * Paginated response payload for infrastructure plans endpoint.
 */
public class InfrastructurePlanPageResponseBody {

    /**
     * List of infrastructure plan summaries on the current page.
     */
    public List<InfrastructurePlanListResponseBody> content;

    /**
     * Total number of elements across all pages.
     */
    public TotalElements totalElements;

    /**
     * Total number of pages.
     */
    public TotalPages totalPages;

    /**
     * Current zero-based page index.
     */
    public PageNumber page;

    /**
     * Number of elements per page.
     */
    public PageSize size;

    /**
     * Number of elements on the current page.
     */
    public NumberOfElements numberOfElements;

    /**
     * Whether this is the first page.
     */
    public PageFlag first;

    /**
     * Whether this is the last page.
     */
    public PageFlag last;
}