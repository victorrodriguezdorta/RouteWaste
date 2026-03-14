package es.ull.project.adapter.rest.response.vehicle;

import java.util.List;

/**
 * Paginated response payload for vehicles endpoint.
 */
public class VehiclePageResponseBody {

    /**
     * Vehicles in the current page.
     */
    public List<VehicleResponseBody> content;

    /**
     * Total number of vehicles available.
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
