package es.ull.project.adapter.rest.deserialization.vehicle;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.deserialization.bulk.AbstractBulkPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.request.vehicle.VehicleBulkPostRequestBody;
import es.ull.project.adapter.rest.request.vehicle.VehiclePostRequestBody;

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
