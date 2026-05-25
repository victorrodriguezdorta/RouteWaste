package es.ull.project.adapter.rest.request.facility;

import java.util.List;

/**
 * Request body for bulk facility creation.
 * Accepts either a JSON array of facilities or an object with a {@code facilities} field.
 */
public class FacilityBulkPostRequestBody {

    /**
     * Facilities to create, in submission order.
     */
    public List<FacilityPostRequestBody> facilities;
}
