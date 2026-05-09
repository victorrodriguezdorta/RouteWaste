package es.ull.project.adapter.rest.serialization.algorithm;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.ull.project.adapter.rest.response.algorithm.AlgorithmExecutionResponseBody;
import java.io.IOException;

/**
 * Custom JSON serializer for AlgorithmExecutionResponseBody.
 * Keeps the external JSON contract stable while the response body uses domain value objects.
 */
public class AlgorithmExecutionResponseBodySerializer extends StdSerializer<AlgorithmExecutionResponseBody> {

    /**
     * Creates the serializer for AlgorithmExecutionResponseBody.
     */
    public AlgorithmExecutionResponseBodySerializer() {
        super(AlgorithmExecutionResponseBody.class);
    }

    /**
     * Serializes the response body into the JSON format expected by clients and the algorithm runner.
     *
     * @param value response body to serialize
     * @param gen JSON generator
     * @param provider serializer provider
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(AlgorithmExecutionResponseBody value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("facilitiesWithVehicles", value.facilitiesWithVehicles);
        gen.writeObjectField("selectedContainers", value.selectedContainers);
        if (value.numberOfDays != null) {
            gen.writeNumberField("numberOfDays", value.numberOfDays.getValue());
        }
        if (value.averagePickupTimeMinutes != null) {
            gen.writeNumberField("averagePickupTimeMinutes", value.averagePickupTimeMinutes.getValue());
        }
        if (value.maxBudget != null) {
            gen.writeObjectFieldStart("maxBudget");
            gen.writeNumberField("amount", value.maxBudget.getAmount());
            if (value.maxBudget.getCurrency().isPresent()) {
                gen.writeStringField("currency", value.maxBudget.getCurrency().get().getCode());
            }
            gen.writeEndObject();
        }
        gen.writeEndObject();
    }
}
