package es.ull.project.adapter.rest.response.overview;

import es.ull.project.domain.valueobject.page.TotalElements;

/**
 * Persisted entity totals exposed on the application overview endpoint.
 */
public class ApplicationEntityCountsResponseBody {

    /**
     * Number of containers stored.
     */
    public TotalElements containers;

    /**
     * Number of vehicles stored.
     */
    public TotalElements vehicles;

    /**
     * Number of facilities stored.
     */
    public TotalElements facilities;

    /**
     * Number of infrastructure plans stored.
     */
    public TotalElements infrastructurePlans;
}
