package es.ull.project.adapter.rest.serialization.serviceassignment;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.response.serviceassignment.ServiceAssignmentResponseBody;

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
     * Serializes a ServiceAssignmentResponseBody object into JSON format
     * This method writes each field of the service assignment to the JSON output stream,
     * including complete container and facility entities that will be automatically
     * serialized by their respective serializers.
     *
     * @param value    The ServiceAssignmentResponseBody object to serialize
     * @param gen      JsonGenerator to write JSON content
     * @param provider SerializerProvider for accessing serializers
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(ServiceAssignmentResponseBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        // Write service assignment ID as string (UUID)
        gen.writeStringField(JsonFields.ID, value.id.toString());

        // Write complete container entity as nested object
        // Jackson will automatically use ContainerResponseBodySerializer
        gen.writeObjectField(JsonFields.CONTAINER, value.container);

        // Write complete facility entity as nested object
        // Jackson will automatically use FacilityResponseBodySerializer
        gen.writeObjectField(JsonFields.FACILITY, value.facility);

        // Write waste demand as nested object
        gen.writeObjectFieldStart(JsonFields.WASTE_DEMAND);
        gen.writeNumberField(JsonFields.CAPACITY_VALUE, value.wasteDemand.value);
        gen.writeStringField(JsonFields.QUANTITY_UNIT, value.wasteDemand.quantityUnit);
        gen.writeStringField(JsonFields.TIME_UNIT, value.wasteDemand.timeUnit);
        gen.writeEndObject();

        // Write distance as nested object
        gen.writeObjectFieldStart(JsonFields.DISTANCE);
        gen.writeNumberField(JsonFields.METERS, value.distance.meters);
        gen.writeNumberField(JsonFields.KILOMETERS, value.distance.kilometers);
        gen.writeEndObject();

        // Write service time as nested object
        gen.writeObjectFieldStart(JsonFields.SERVICE_TIME);
        gen.writeNumberField(JsonFields.MINUTES, value.serviceTime.minutes);
        gen.writeEndObject();

        // Write transport cost as nested object
        gen.writeObjectFieldStart(JsonFields.TRANSPORT_COST);
        gen.writeNumberField(JsonFields.AMOUNT, value.transportCost.amount);
        
        // Write currency only if it is not null (optional field)
        if (value.transportCost.currency != null) {
            gen.writeStringField(JsonFields.CURRENCY, value.transportCost.currency);
        }
        
        gen.writeEndObject();

        gen.writeEndObject();
    }
}
