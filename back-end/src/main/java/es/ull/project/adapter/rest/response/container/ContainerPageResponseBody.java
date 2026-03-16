package es.ull.project.adapter.rest.response.container;

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
    public long totalElements;

    /**
     * Total number of pages available.
     */
    public int totalPages;

    /**
     * Current page index (0-based).
     */
    public int page;

    /**
     * Requested page size.
     */
    public int size;

    /**
     * Number of elements returned in current page.
     */
    public int numberOfElements;

    /**
     * True when current page is the first one.
     */
    public boolean first;

    /**
     * True when current page is the last one.
     */
    public boolean last;
}
