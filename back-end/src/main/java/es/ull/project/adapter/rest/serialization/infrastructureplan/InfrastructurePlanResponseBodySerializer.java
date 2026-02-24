package es.ull.project.adapter.rest.serialization.infrastructureplan;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;

/**
 * Custom JSON serializer for InfrastructurePlanResponseBody
 * This serializer manually controls how InfrastructurePlan response data is converted to JSON format
 * It follows the Jackson serialization pattern using JsonGenerator
 */
public class InfrastructurePlanResponseBodySerializer extends StdSerializer<InfrastructurePlanResponseBody> {

    /**
     * Default constructor for the serializer
     * Calls the parent constructor with the class type
     */
    public InfrastructurePlanResponseBodySerializer() {
        super(InfrastructurePlanResponseBody.class);
    }

    /**
     * Serializes an InfrastructurePlanResponseBody object into JSON format
     * This method writes each field of the infrastructure plan to the JSON output stream
     *
     * @param value    The InfrastructurePlanResponseBody object to serialize
     * @param gen      JsonGenerator to write JSON content
     * @param provider SerializerProvider for accessing serializers
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(InfrastructurePlanResponseBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(JsonFields.ID, value.id.toString());
        gen.writeStringField(JsonFields.PERIOD, value.period);
        gen.writeObjectFieldStart(JsonFields.MAX_BUDGET);
        gen.writeNumberField(JsonFields.AMOUNT, value.maxBudget.amount);
        if (value.maxBudget.currency != null) {
            gen.writeStringField(JsonFields.CURRENCY, value.maxBudget.currency);
        }
        gen.writeEndObject();
        gen.writeObjectFieldStart("estimatedTotalCost");
        gen.writeNumberField(JsonFields.AMOUNT, value.estimatedTotalCost.amount);
        if (value.estimatedTotalCost.currency != null) {
            gen.writeStringField(JsonFields.CURRENCY, value.estimatedTotalCost.currency);
        }
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.SERVICE_POLICIES);
        if (value.servicePolicies.maxServiceDistance != null) {
            gen.writeNumberField(JsonFields.MAX_SERVICE_DISTANCE, value.servicePolicies.maxServiceDistance);
        }
        if (value.servicePolicies.maxServiceTime != null) {
            gen.writeNumberField(JsonFields.MAX_SERVICE_TIME, value.servicePolicies.maxServiceTime);
        }
        if (value.servicePolicies.maxInfrastructureCount != null) {
            gen.writeNumberField(JsonFields.MAX_INFRASTRUCTURE_COUNT, value.servicePolicies.maxInfrastructureCount);
        }
        if (value.servicePolicies.maxEmissions != null) {
            gen.writeNumberField(JsonFields.MAX_EMISSIONS, value.servicePolicies.maxEmissions);
        }
        gen.writeEndObject();
        gen.writeEndObject();
    }
}
