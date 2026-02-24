package es.ull.project.adapter.rest.serialization.container;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.response.container.ContainerResponseBody;

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
     * This method writes each field of the container to the JSON output stream
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
        gen.writeObjectFieldStart(JsonFields.LOCATION);
        gen.writeNumberField(JsonFields.LATITUDE, value.location.latitude);
        gen.writeNumberField(JsonFields.LONGITUDE, value.location.longitude);
        if (value.location.postalAddress != null) {
            gen.writeStringField(JsonFields.POSTAL_ADDRESS, value.location.postalAddress);
        }
        if (value.location.gisReference != null) {
            gen.writeStringField(JsonFields.GIS_REFERENCE, value.location.gisReference);
        }
        gen.writeEndObject();
        gen.writeStringField(JsonFields.WASTE_TYPE, value.wasteType);
        gen.writeObjectFieldStart(JsonFields.WASTE_DEMAND);
        gen.writeNumberField(JsonFields.CAPACITY_VALUE, value.wasteDemand.value);
        gen.writeStringField(JsonFields.QUANTITY_UNIT, value.wasteDemand.quantityUnit);
        gen.writeStringField(JsonFields.TIME_UNIT, value.wasteDemand.timeUnit);
        gen.writeEndObject();
        if (value.serviceZone != null) {
            gen.writeStringField(JsonFields.SERVICE_ZONE, value.serviceZone);
        }
        gen.writeEndObject();
    }
}
