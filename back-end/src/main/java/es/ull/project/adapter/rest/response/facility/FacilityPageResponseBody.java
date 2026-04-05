package es.ull.project.adapter.rest.response.facility;

import java.util.List;

/**
 * Paginated response payload for facilities endpoint.
 */
public class FacilityPageResponseBody {

    /**
     * Facilities in the current page.
     */
    public List<FacilityResponseBody> content;

    /**
     * Total number of facilities available.
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
