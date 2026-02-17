package es.ull.project.adapter.rest.serialization.facility;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;

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
     * This method writes each field of the facility to the JSON output stream
     *
     * @param value    The FacilityResponseBody object to serialize
     * @param gen      JsonGenerator to write JSON content
     * @param provider SerializerProvider for accessing serializers
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(FacilityResponseBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        // Write facility ID as string (UUID)
        gen.writeStringField(JsonFields.ID, value.id.toString());

        // Write facility type as string
        gen.writeStringField(JsonFields.FACILITY_TYPE, value.facilityType);

        // Write location as nested object
        gen.writeObjectFieldStart(JsonFields.LOCATION);
        gen.writeNumberField(JsonFields.LATITUDE, value.location.latitude);
        gen.writeNumberField(JsonFields.LONGITUDE, value.location.longitude);
        gen.writeStringField(JsonFields.POSTAL_ADDRESS, value.location.postalAddress);
        gen.writeStringField(JsonFields.GIS_REFERENCE, value.location.gisReference);
        gen.writeEndObject();

        // Write capacity as nested object
        gen.writeObjectFieldStart(JsonFields.CAPACITY);
        gen.writeNumberField(JsonFields.CAPACITY_VALUE, value.capacity.value);
        gen.writeStringField(JsonFields.QUANTITY_UNIT, value.capacity.quantityUnit);
        gen.writeStringField(JsonFields.TIME_UNIT, value.capacity.timeUnit);
        gen.writeEndObject();

        // Write opening fixed cost as nested object
        gen.writeObjectFieldStart(JsonFields.OPENING_FIXED_COST);
        gen.writeNumberField(JsonFields.AMOUNT, value.openingFixedCost.amount);
        
        // Write currency only if it is not null (optional field)
        if (value.openingFixedCost.currency != null) {
            gen.writeStringField(JsonFields.CURRENCY, value.openingFixedCost.currency);
        }
        
        gen.writeEndObject();

        // Write facility status as string
        gen.writeStringField(JsonFields.STATUS, value.status);

        // Write assigned waste demand as nested object
        gen.writeObjectFieldStart(JsonFields.WASTE_DEMAND);
        gen.writeNumberField(JsonFields.CAPACITY_VALUE, value.assignedWasteDemand.value);
        gen.writeStringField(JsonFields.QUANTITY_UNIT, value.assignedWasteDemand.quantityUnit);
        gen.writeStringField(JsonFields.TIME_UNIT, value.assignedWasteDemand.timeUnit);
        gen.writeEndObject();

        gen.writeEndObject();
    }
}
