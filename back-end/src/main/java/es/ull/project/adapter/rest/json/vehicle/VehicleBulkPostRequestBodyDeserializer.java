package es.ull.project.adapter.rest.json.vehicle;

import es.ull.project.adapter.rest.json.JsonFields;
import es.ull.project.adapter.rest.request.vehicle.VehicleBulkPostRequestBody;
import es.ull.project.adapter.rest.request.vehicle.VehiclePostRequestBody;
import es.ull.project.adapter.rest.support.AbstractBulkPostRequestBodyDeserializer;

/**
 * Deserializes bulk vehicle creation payloads.
 */
public class VehicleBulkPostRequestBodyDeserializer
        extends AbstractBulkPostRequestBodyDeserializer<VehiclePostRequestBody, VehicleBulkPostRequestBody> {

    /**
     * Registers the single-item vehicle deserializer for each array element.
     */
    public VehicleBulkPostRequestBodyDeserializer() {
        super(
                new VehiclePostRequestBodyDeserializer(),
                VehicleBulkPostRequestBody::new,
                JsonFields.VEHICLES,
                (wrapper, items) -> wrapper.vehicles = items);
    }
}
