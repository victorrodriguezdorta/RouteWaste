package es.ull.project.adapter.rest.serialization.container;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.ull.project.adapter.rest.json.JsonFields;
import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import java.io.IOException;
/**
 * Custom JSON serializer for ContainerResponseBody
 * This serializer manually controls how Container response data is converted to JSON format
 * It follows the Jackson serialization pattern using JsonGenerator
 */
public class ContainerResponseBodySerializer extends StdSerializer<ContainerResponseBody> {

    /**
     * Default constructor for the serializer
     * Calls the parent constructor with the class type
     */
    public ContainerResponseBodySerializer() {
        super(ContainerResponseBody.class);
    }

    /**
     * Serializes a ContainerResponseBody object into JSON format
     * Extracts primitives from domain value objects during serialization
     *
     * @param value    The ContainerResponseBody object to serialize
     * @param gen      JsonGenerator to write JSON content
     * @param provider SerializerProvider for accessing serializers
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(ContainerResponseBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(JsonFields.ID, value.id.toString());
        gen.writeStringField(JsonFields.NAME, value.name.getValue());
        gen.writeObjectFieldStart(JsonFields.LOCATION);
        gen.writeNumberField(JsonFields.LATITUDE, value.location.getLatitude());
        gen.writeNumberField(JsonFields.LONGITUDE, value.location.getLongitude());
        if (value.location.getPostalAddress() != null) {
            gen.writeStringField(JsonFields.POSTAL_ADDRESS, value.location.getPostalAddress());
        }
        if (value.location.getGISReference() != null) {
            gen.writeStringField(JsonFields.GIS_REFERENCE, value.location.getGISReference());
        }
        gen.writeEndObject();
        gen.writeStringField(JsonFields.WASTE_TYPE, value.wasteType.name());
        gen.writeObjectFieldStart(JsonFields.CAPACITY_LITERS);
        gen.writeNumberField(JsonFields.LITERS, value.capacityLiters.getLiters());
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.DAILY_DEMAND_LITERS_PER_DAY);
        gen.writeNumberField(JsonFields.LITERS_PER_DAY, value.dailyDemandLitersPerDay.getLitersPerDay());
        gen.writeEndObject();
        if (value.serviceZone != null) {
            gen.writeStringField(JsonFields.SERVICE_ZONE, value.serviceZone.name());
        }
        gen.writeEndObject();
    }
}
