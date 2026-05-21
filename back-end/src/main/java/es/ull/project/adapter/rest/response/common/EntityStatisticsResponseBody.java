package es.ull.project.adapter.rest.response.common;

import es.ull.project.domain.valueobject.page.TotalElements;
import java.util.List;

/**
 * Statistical summary included in paginated list responses for primary entities.
 */
public class EntityStatisticsResponseBody {

    /**
     * Total number of entities in the system (unfiltered).
     */
    public TotalElements total;

    /**
     * Count per type; includes every enum value (zero when none exist).
     */
    public List<EntityTypeCountResponseBody> byType;
}
