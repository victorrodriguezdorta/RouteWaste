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

    private static final String FIELD_FACILITIES_WITH_VEHICLES = "facilitiesWithVehicles";
    private static final String FIELD_SELECTED_CONTAINERS = "selectedContainers";
    private static final String FIELD_NUMBER_OF_DAYS = "numberOfDays";
    private static final String FIELD_AVERAGE_PICKUP_TIME_MINUTES = "averagePickupTimeMinutes";
    private static final String FIELD_COLLECTION_START_TIME = "collectionStartTime";
    private static final String FIELD_AVERAGE_TRANSFER_TIME_MINUTES = "averageTransferTimeMinutes";
    private static final String FIELD_DISTANCE_WEIGHT = "distanceWeight";
    private static final String FIELD_FILL_WEIGHT = "fillWeight";
    private static final String FIELD_MAX_BUDGET = "maxBudget";
    private static final String FIELD_AMOUNT = "amount";
    private static final String FIELD_CURRENCY = "currency";

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
        gen.writeObjectField(FIELD_FACILITIES_WITH_VEHICLES, value.facilitiesWithVehicles);
        gen.writeObjectField(FIELD_SELECTED_CONTAINERS, value.selectedContainers);
        if (value.numberOfDays != null) {
            gen.writeNumberField(FIELD_NUMBER_OF_DAYS, value.numberOfDays.getValue());
        }
        if (value.averagePickupTimeMinutes != null) {
            gen.writeNumberField(FIELD_AVERAGE_PICKUP_TIME_MINUTES, value.averagePickupTimeMinutes.getValue());
        }
        if (value.collectionStartTime != null) {
            gen.writeStringField(FIELD_COLLECTION_START_TIME, value.collectionStartTime.getFormatted());
        }
        if (value.averageTransferTimeMinutes != null) {
            gen.writeNumberField(FIELD_AVERAGE_TRANSFER_TIME_MINUTES, value.averageTransferTimeMinutes.getValue());
        }
        if (value.greedyWeights != null) {
            gen.writeNumberField(FIELD_DISTANCE_WEIGHT, value.greedyWeights.getDistanceWeight());
            gen.writeNumberField(FIELD_FILL_WEIGHT, value.greedyWeights.getFillWeight());
        }
        if (value.maxBudget != null) {
            gen.writeObjectFieldStart(FIELD_MAX_BUDGET);
            gen.writeNumberField(FIELD_AMOUNT, value.maxBudget.getAmount());
            if (value.maxBudget.getCurrency().isPresent()) {
                gen.writeStringField(FIELD_CURRENCY, value.maxBudget.getCurrency().get().getCode());
            }
            gen.writeEndObject();
        }
        gen.writeEndObject();
    }
}
