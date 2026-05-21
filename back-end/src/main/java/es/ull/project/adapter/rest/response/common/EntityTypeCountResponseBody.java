package es.ull.project.adapter.rest.response.common;

import es.ull.project.domain.valueobject.page.TotalElements;

/**
 * JSON representation of the count for one enumerated type.
 */
public class EntityTypeCountResponseBody {

    /**
     * Enum name of the type (e.g. COLLECTION_TRUCK, ORGANIC).
     */
    public String type;

    /**
     * Number of entities of this type.
     */
    public TotalElements count;
}
