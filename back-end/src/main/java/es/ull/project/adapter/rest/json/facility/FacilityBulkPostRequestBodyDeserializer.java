package es.ull.project.adapter.rest.json.facility;

import es.ull.project.adapter.rest.json.JsonFields;
import es.ull.project.adapter.rest.request.facility.FacilityBulkPostRequestBody;
import es.ull.project.adapter.rest.request.facility.FacilityPostRequestBody;
import es.ull.project.adapter.rest.support.AbstractBulkPostRequestBodyDeserializer;

/**
 * Deserializes bulk facility creation payloads.
 */
public class FacilityBulkPostRequestBodyDeserializer
        extends AbstractBulkPostRequestBodyDeserializer<FacilityPostRequestBody, FacilityBulkPostRequestBody> {

    /**
     * Registers the single-item facility deserializer for each array element.
     */
    public FacilityBulkPostRequestBodyDeserializer() {
        super(
                new FacilityPostRequestBodyDeserializer(),
                FacilityBulkPostRequestBody::new,
                JsonFields.FACILITIES,
                (wrapper, items) -> wrapper.facilities = items);
    }
}
