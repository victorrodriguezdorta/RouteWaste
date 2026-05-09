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

    private static final String FIELD_INFRASTRUCTURE_PLAN_ID = "infrastructurePlanId";
    private static final String FIELD_ASSIGNED_CONTAINERS = "assignedContainers";

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
        gen.writeStringField(FIELD_INFRASTRUCTURE_PLAN_ID, value.infrastructurePlanId != null ? value.infrastructurePlanId.toString() : null);
        gen.writeStringField(JsonFields.FACILITY, value.facilityId != null ? value.facilityId.toString() : null);
        gen.writeObjectField(FIELD_ASSIGNED_CONTAINERS, value.assignedContainers);
        gen.writeEndObject();
    }
}
