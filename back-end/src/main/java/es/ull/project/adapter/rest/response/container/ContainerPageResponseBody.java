package es.ull.project.adapter.rest.response.container;

import es.ull.project.adapter.rest.response.common.EntityStatisticsResponseBody;
import es.ull.project.domain.valueobject.page.NumberOfElements;
import es.ull.project.domain.valueobject.page.PageFlag;
import es.ull.project.domain.valueobject.page.PageNumber;
import es.ull.project.domain.valueobject.page.PageSize;
import es.ull.project.domain.valueobject.page.TotalElements;
import es.ull.project.domain.valueobject.page.TotalPages;
import java.util.List;

/**
 * Paginated response payload for containers endpoint.
 */
public class ContainerPageResponseBody {

    /**
     * Containers in the current page.
     */
    public List<ContainerResponseBody> content;

    /**
     * Total number of containers available.
     */
    public TotalElements totalElements;

    /**
     * Total number of pages available.
     */
    public TotalPages totalPages;

    /**
     * Current page index (0-based).
     */
    public PageNumber page;

    /**
     * Requested page size.
     */
    public PageSize size;

    /**
     * Number of elements returned in current page.
     */
    public NumberOfElements numberOfElements;

    /**
     * True when current page is the first one.
     */
    public PageFlag first;

    /**
     * True when current page is the last one.
     */
    public PageFlag last;

    /**
     * Global statistics: total containers and count per {@link es.ull.project.domain.enumerate.WasteType}.
     */
    public EntityStatisticsResponseBody statistics;
}
