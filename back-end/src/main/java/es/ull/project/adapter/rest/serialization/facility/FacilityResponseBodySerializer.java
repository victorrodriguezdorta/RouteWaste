package es.ull.project.adapter.rest.serialization.facility;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.ull.project.adapter.rest.json.JsonFields;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import java.io.IOException;

/**
 * Custom JSON serializer for FacilityResponseBody
 * This serializer manually controls how Facility response data is converted to JSON format
 * It follows the Jackson serialization pattern using JsonGenerator
 */
public class FacilityResponseBodySerializer extends StdSerializer<FacilityResponseBody> {

    /**
     * Default constructor for the serializer
     * Calls the parent constructor with the class type
     */
    public FacilityResponseBodySerializer() {
        super(FacilityResponseBody.class);
    }

    /**
     * Serializes a FacilityResponseBody object into JSON format
     * Extracts primitives from domain value objects during serialization
     *
     * @param value    The FacilityResponseBody object to serialize
     * @param gen      JsonGenerator to write JSON content
     * @param provider SerializerProvider for accessing serializers
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(FacilityResponseBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(JsonFields.ID, value.id.toString());
        gen.writeStringField(JsonFields.NAME, value.name.getValue());
        gen.writeStringField(JsonFields.FACILITY_TYPE, value.facilityType.name());
        gen.writeObjectFieldStart(JsonFields.LOCATION);
        gen.writeNumberField(JsonFields.LATITUDE, value.location.getLatitude());
        gen.writeNumberField(JsonFields.LONGITUDE, value.location.getLongitude());
        gen.writeStringField(JsonFields.POSTAL_ADDRESS, value.location.getPostalAddress());
        gen.writeStringField(JsonFields.GIS_REFERENCE, value.location.getGISReference());
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.STORAGE_CAPACITY);
        gen.writeNumberField(JsonFields.CAPACITY_VALUE, value.storageCapacity.getKilograms());
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.PROCESSING_CAPACITY);
        gen.writeNumberField(JsonFields.CAPACITY_VALUE, value.processingCapacity.getKilogramsPerDay());
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.UNLOADING_TIME);
        gen.writeNumberField(JsonFields.TIME_VALUE, value.unloadingTime.getMinutes());
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.OPENING_FIXED_COST);
        gen.writeNumberField(JsonFields.AMOUNT, value.openingFixedCost.getAmount());
        if (value.openingFixedCost.getCurrency().isPresent()) {
            gen.writeStringField(JsonFields.CURRENCY, value.openingFixedCost.getCurrency().get().getCode());
        }
        gen.writeEndObject();
        gen.writeStringField(JsonFields.STATUS, value.status.name());
        gen.writeObjectFieldStart(JsonFields.CURRENT_FILLING_LEVEL);
        gen.writeNumberField(JsonFields.WASTE_DEMAND_VALUE, value.currentFillingLevel.getLitersPerDay());
        gen.writeEndObject();
        gen.writeEndObject();
    }
}
