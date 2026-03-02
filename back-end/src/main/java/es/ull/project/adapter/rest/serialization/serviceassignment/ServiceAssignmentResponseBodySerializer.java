package es.ull.project.adapter.rest.serialization.serviceassignment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.response.serviceassignment.ServiceAssignmentResponseBody;

import java.io.IOException;
/**
 * Custom JSON serializer for ServiceAssignmentResponseBody
 * This serializer manually controls how ServiceAssignment response data is converted to JSON format
 * It follows the Jackson serialization pattern using JsonGenerator
 */
public class ServiceAssignmentResponseBodySerializer extends StdSerializer<ServiceAssignmentResponseBody> {

    /**
     * Default constructor for the serializer
     * Calls the parent constructor with the class type
     */
    public ServiceAssignmentResponseBodySerializer() {
        super(ServiceAssignmentResponseBody.class);
    }

    /**
     * Serializes a ServiceAssignmentResponseBody object into JSON format.
     * 
     * This method extracts fields from domain value objects and writes them to JSON.
     * For each value object (WasteDemand, Distance, ServiceTime, TransportationVariableCost),
     * it calls the appropriate getter methods defined in the domain layer.
     * 
     * Container and facility entities are automatically serialized by their respective serializers.
     *
     * @param value    The ServiceAssignmentResponseBody object to serialize
     * @param gen      JsonGenerator to write JSON content
     * @param provider SerializerProvider for accessing serializers
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(ServiceAssignmentResponseBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(JsonFields.ID, value.id.toString());
        gen.writeObjectField(JsonFields.CONTAINER, value.container);
        gen.writeObjectField(JsonFields.FACILITY, value.facility);
        gen.writeObjectFieldStart(JsonFields.WASTE_DEMAND);
        gen.writeNumberField(JsonFields.CAPACITY_VALUE, value.wasteDemand.getValue());
        gen.writeStringField(JsonFields.QUANTITY_UNIT, value.wasteDemand.getQuantityUnit().getValue());
        gen.writeStringField(JsonFields.TIME_UNIT, value.wasteDemand.getTimeUnit().name());
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.DISTANCE);
        gen.writeNumberField(JsonFields.METERS, value.distance.toMeters());
        gen.writeNumberField(JsonFields.KILOMETERS, value.distance.toKilometers());
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.SERVICE_TIME);
        gen.writeNumberField(JsonFields.MINUTES, value.serviceTime.getValue());
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.TRANSPORT_COST);
        gen.writeNumberField(JsonFields.AMOUNT, value.transportCost.getAmount());
        if (value.transportCost.getCurrency().isPresent()) {
            gen.writeStringField(JsonFields.CURRENCY, value.transportCost.getCurrency().get().getCode());
        }
        gen.writeEndObject();
        gen.writeEndObject();
    }
}
