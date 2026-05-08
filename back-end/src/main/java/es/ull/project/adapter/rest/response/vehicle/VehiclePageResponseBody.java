package es.ull.project.adapter.rest.response.vehicle;

import es.ull.project.domain.valueobject.page.NumberOfElements;
import es.ull.project.domain.valueobject.page.PageFlag;
import es.ull.project.domain.valueobject.page.PageNumber;
import es.ull.project.domain.valueobject.page.PageSize;
import es.ull.project.domain.valueobject.page.TotalElements;
import es.ull.project.domain.valueobject.page.TotalPages;
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
}
