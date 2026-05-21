package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.common.EntityStatisticsResponseBody;
import es.ull.project.adapter.rest.response.common.EntityTypeCountResponseBody;
import es.ull.project.domain.readmodel.EntityTypeBreakdown;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps domain type breakdown read models to REST response bodies.
 */
public final class EntityStatisticsResponseMapper {

    private EntityStatisticsResponseMapper() {
    }

    /**
     * Converts a domain breakdown into the statistics section of a page response.
     *
     * @param breakdown domain read model; must not be null
     * @return REST statistics payload
     */
    public static EntityStatisticsResponseBody toResponseBody(EntityTypeBreakdown breakdown) {
        EntityStatisticsResponseBody body = new EntityStatisticsResponseBody();
        body.total = breakdown.total();
        List<EntityTypeCountResponseBody> byType = new ArrayList<>(breakdown.byType().size());
        for (EntityTypeBreakdown.TypeCount typeCount : breakdown.byType()) {
            EntityTypeCountResponseBody entry = new EntityTypeCountResponseBody();
            entry.type = typeCount.type();
            entry.count = typeCount.count();
            byType.add(entry);
        }
        body.byType = byType;
        return body;
    }
}
