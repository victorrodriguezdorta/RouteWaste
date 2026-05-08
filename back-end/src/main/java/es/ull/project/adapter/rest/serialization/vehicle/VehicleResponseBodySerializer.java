package es.ull.project.adapter.rest.serialization.vehicle;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.response.vehicle.VehicleResponseBody;

/**
 * Custom JSON serializer for VehicleResponseBody
 * This serializer manually controls how Vehicle response data is converted to JSON format
 * It follows the Jackson serialization pattern using JsonGenerator
 */
public class VehicleResponseBodySerializer extends StdSerializer<VehicleResponseBody> {

    /**
     * Default constructor for the serializer
     * Calls the parent constructor with the class type
     */
    public VehicleResponseBodySerializer() {
        super(VehicleResponseBody.class);
    }

    /**
     * Serializes a VehicleResponseBody object into JSON format
     * This method writes each field of the vehicle to the JSON output stream
     *
     * @param value    The VehicleResponseBody object to serialize
     * @param gen      JsonGenerator to write JSON content
     * @param provider SerializerProvider for accessing serializers
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(VehicleResponseBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(JsonFields.ID, value.id.toString());
        gen.writeStringField(JsonFields.VEHICLE_TYPE, value.vehicleType.name());
        gen.writeObjectFieldStart("capacityKilograms");
        gen.writeNumberField("Kilograms", value.capacityKilograms.getKilograms());
        gen.writeEndObject();
        gen.writeObjectFieldStart("capacityLiters");
        gen.writeNumberField("liters", value.capacityLiters.getLiters());
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.COST_PER_KILOMETER);
        gen.writeNumberField(JsonFields.AMOUNT, value.costPerKilometer.getAmount());
        if (value.costPerKilometer.getCurrency().isPresent()) {
            gen.writeStringField(JsonFields.CURRENCY, value.costPerKilometer.getCurrency().get().getCode());
        }
        gen.writeEndObject();
        gen.writeEndObject();
    }
}
