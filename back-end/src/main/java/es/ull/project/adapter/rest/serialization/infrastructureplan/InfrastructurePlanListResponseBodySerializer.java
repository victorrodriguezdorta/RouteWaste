package es.ull.project.adapter.rest.serialization.infrastructureplan;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.ull.project.adapter.rest.json.JsonFields;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanListResponseBody;
import java.io.IOException;

/**
 * Custom JSON serializer for InfrastructurePlanListResponseBody.
 * This serializer keeps the list endpoint lightweight while still flattening value objects safely.
 */
public class InfrastructurePlanListResponseBodySerializer extends StdSerializer<InfrastructurePlanListResponseBody> {

    private static final String FIELD_EXECUTED_AT = "executedAt";
    private static final String FIELD_ESTIMATED_TOTAL_COST = "estimatedTotalCost";
    private static final String FIELD_NUMBER_OF_DAYS = "numberOfDays";
    private static final String FIELD_AVERAGE_PICKUP_TIME_MINUTES = "averagePickupTimeMinutes";
    private static final String FIELD_VALIDITY_STATE = "validityState";
    private static final String FIELD_EXECUTION_STATE = "executionState";
    private static final String FIELD_FAILURE_REASON = "failureReason";

    /**
     * Default constructor for the serializer.
     * Calls the parent constructor with the class type.
     */
    public InfrastructurePlanListResponseBodySerializer() {
        super(InfrastructurePlanListResponseBody.class);
    }

    /**
     * Serializes an InfrastructurePlanListResponseBody object into JSON format.
     *
     * @param value    the InfrastructurePlanListResponseBody object to serialize
     * @param gen      JsonGenerator to write JSON content
     * @param provider SerializerProvider for accessing serializers
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(InfrastructurePlanListResponseBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(JsonFields.ID, value.id.toString());
        if (value.executedAt != null) {
            gen.writeStringField(FIELD_EXECUTED_AT, value.executedAt.getTimestamp());
        }
        gen.writeObjectFieldStart(FIELD_ESTIMATED_TOTAL_COST);
        gen.writeNumberField(JsonFields.AMOUNT, value.estimatedTotalCost.getAmount());
        if (value.estimatedTotalCost.getCurrency().isPresent()) {
            gen.writeStringField(JsonFields.CURRENCY, value.estimatedTotalCost.getCurrency().get().getCode());
        }
        gen.writeEndObject();
        if (value.numberOfDays != null) {
            gen.writeNumberField(FIELD_NUMBER_OF_DAYS, value.numberOfDays.getValue());
        }
        if (value.averagePickupTimeMinutes != null) {
            gen.writeNumberField(FIELD_AVERAGE_PICKUP_TIME_MINUTES, value.averagePickupTimeMinutes.getValue());
        }
        if (value.validityState != null) {
            gen.writeStringField(FIELD_VALIDITY_STATE, value.validityState.name());
        }
        if (value.executionState != null) {
            gen.writeStringField(FIELD_EXECUTION_STATE, value.executionState.name());
        }
        if (value.failureReason != null) {
            gen.writeStringField(FIELD_FAILURE_REASON, value.failureReason.getValue());
        }
        gen.writeEndObject();
    }
}
