package es.ull.project.adapter.rest.serialization.infrastructureplan;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.adapter.rest.serialization.facility.FacilityResponseBodySerializer;

import java.io.IOException;

/**
 * Custom JSON serializer for InfrastructurePlanResponseBody
 * This serializer manually controls how InfrastructurePlan response data is converted to JSON format
 * Includes complete facility and container information in the response
 */
public class InfrastructurePlanResponseBodySerializer extends StdSerializer<InfrastructurePlanResponseBody> {

    private static final String FIELD_SELECTED_FACILITIES = "selectedFacilities";
    private static final String FIELD_SERVICE_ASSIGNMENTS = "serviceAssignments";
    private static final String FIELD_ESTIMATED_TOTAL_COST = "estimatedTotalCost";

    private final FacilityResponseBodySerializer facilitySerializer = new FacilityResponseBodySerializer();

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
        gen.writeStringField(JsonFields.ID, value.id.toString());
        gen.writeStringField(JsonFields.PERIOD, value.period.getValue());
        gen.writeArrayFieldStart(FIELD_SELECTED_FACILITIES);
        for (var facility : value.selectedFacilities) {
            facilitySerializer.serialize(facility, gen, provider);
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart(FIELD_SERVICE_ASSIGNMENTS);
        for (var assignment : value.serviceAssignments) {
            gen.writeObject(assignment);
        }
        gen.writeEndArray();
        gen.writeObjectFieldStart(JsonFields.MAX_BUDGET);
        gen.writeNumberField(JsonFields.AMOUNT, value.maxBudget.getAmount());
        if (value.maxBudget.getCurrency().isPresent()) {
            gen.writeStringField(JsonFields.CURRENCY, value.maxBudget.getCurrency().get().getCode());
        }
        gen.writeEndObject();
        gen.writeObjectFieldStart(FIELD_ESTIMATED_TOTAL_COST);
        gen.writeNumberField(JsonFields.AMOUNT, value.estimatedTotalCost.getAmount());
        if (value.estimatedTotalCost.getCurrency().isPresent()) {
            gen.writeStringField(JsonFields.CURRENCY, value.estimatedTotalCost.getCurrency().get().getCode());
        }
        gen.writeEndObject();
        gen.writeObjectFieldStart(JsonFields.SERVICE_POLICIES);
        if (value.servicePolicies.getMaxServiceDistance().isPresent()) {
            gen.writeNumberField(JsonFields.MAX_SERVICE_DISTANCE, value.servicePolicies.getMaxServiceDistance().get());
        }
        if (value.servicePolicies.getMaxServiceTime().isPresent()) {
            gen.writeNumberField(JsonFields.MAX_SERVICE_TIME, value.servicePolicies.getMaxServiceTime().get());
        }
        if (value.servicePolicies.getMaxInfrastructureCount().isPresent()) {
            gen.writeNumberField(JsonFields.MAX_INFRASTRUCTURE_COUNT, value.servicePolicies.getMaxInfrastructureCount().get());
        }
        if (value.servicePolicies.getMaxEmissions().isPresent()) {
            gen.writeNumberField(JsonFields.MAX_EMISSIONS, value.servicePolicies.getMaxEmissions().get());
        }
        gen.writeEndObject();
        gen.writeEndObject();
    }
}
