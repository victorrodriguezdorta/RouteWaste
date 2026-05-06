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
 * Includes complete facility and container information in the response
 */
public class InfrastructurePlanResponseBodySerializer extends StdSerializer<InfrastructurePlanResponseBody> {

    private static final String FIELD_EXECUTED_AT = "executedAt";
    private static final String FIELD_TOTAL_COLLECTED_KILOGRAMS = "totalCollectedKilograms";
    private static final String FIELD_TOTAL_COLLECTED_LITERS = "totalCollectedLiters";
    private static final String FIELD_TOTAL_DISTANCE_METERS = "totalDistanceMeters";
    private static final String FIELD_CLUSTERS = "clusters";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_DAILY_PLANS = "dailyPlans";

    /**
     * Default constructor for the serializer
     * Calls the parent constructor with the class type
     */
    public InfrastructurePlanResponseBodySerializer() {
        super(InfrastructurePlanResponseBody.class);
    }

    /**
     * Serializes an InfrastructurePlanResponseBody object into JSON format
     * Extracts primitives from domain value objects during serialization
     *
     * @param value    The InfrastructurePlanResponseBody object to serialize
     * @param gen      JsonGenerator to write JSON content
     * @param provider SerializerProvider for accessing serializers
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(InfrastructurePlanResponseBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        
        // Timestamp and metrics first (matching algorithm output structure)
        if (value.executedAt != null) {
            gen.writeStringField(FIELD_EXECUTED_AT, value.executedAt);
        }
        gen.writeNumberField(FIELD_TOTAL_COLLECTED_KILOGRAMS, value.totalCollectedKilograms);
        gen.writeNumberField(FIELD_TOTAL_COLLECTED_LITERS, value.totalCollectedLiters);
        
        // maxBudget with currency
        gen.writeObjectFieldStart(JsonFields.MAX_BUDGET);
        gen.writeNumberField(JsonFields.AMOUNT, value.maxBudget.getAmount());
        if (value.maxBudget.getCurrency().isPresent()) {
            gen.writeStringField(JsonFields.CURRENCY, value.maxBudget.getCurrency().get().getCode());
        }
        gen.writeEndObject();
        
        gen.writeNumberField(FIELD_TOTAL_DISTANCE_METERS, value.totalDistanceMeters);
        
        // clusters as serviceAssignments (with full objects)
        gen.writeArrayFieldStart(FIELD_CLUSTERS);
        for (var assignment : value.serviceAssignments) {
            gen.writeObject(assignment);
        }
        gen.writeEndArray();
        
        if (value.status != null) {
            gen.writeStringField(FIELD_STATUS, value.status);
        }
        
        // daily plans array (with full nested objects)
        if (value.dailyPlans != null && !value.dailyPlans.isEmpty()) {
            gen.writeArrayFieldStart(FIELD_DAILY_PLANS);
            for (var dp : value.dailyPlans) {
                gen.writeObject(dp);
            }
            gen.writeEndArray();
        }
        
        gen.writeEndObject();
    }
}
