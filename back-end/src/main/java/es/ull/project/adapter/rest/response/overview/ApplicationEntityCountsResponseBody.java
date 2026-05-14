package es.ull.project.adapter.rest.response.overview;

/**
 * Persisted entity totals exposed on the application overview endpoint.
 */
public class ApplicationEntityCountsResponseBody {

    /**
     * Number of containers stored.
     */
    public long containers;

    /**
     * Number of vehicles stored.
     */
    public long vehicles;

    /**
     * Number of facilities stored.
     */
    public long facilities;

    /**
     * Number of infrastructure plans stored.
     */
    public long infrastructurePlans;
}
