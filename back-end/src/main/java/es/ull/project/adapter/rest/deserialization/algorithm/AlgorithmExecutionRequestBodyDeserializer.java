package es.ull.project.adapter.rest.deserialization.algorithm;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import es.ull.project.adapter.rest.json.JsonFields;
import es.ull.project.adapter.rest.exception.FieldError;
import es.ull.project.adapter.rest.exception.ValidationException;
import es.ull.project.adapter.rest.request.algorithm.AlgorithmExecutionRequestBody;
import es.ull.project.adapter.rest.request.algorithm.FacilityVehiclesSelectionRequestBody;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.AverageTransferTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.CollectionStartTime;
import es.ull.project.domain.valueobject.algorithm.GreedyWeights;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Custom JSON deserializer for AlgorithmExecutionRequestBody.
 * Converts primitive JSON fields into domain value objects while preserving the external request contract.
 */
public class AlgorithmExecutionRequestBodyDeserializer extends JsonDeserializer<AlgorithmExecutionRequestBody> {

    private static final String FIELD_FACILITIES_WITH_VEHICLES = "facilitiesWithVehicles";
    private static final String FIELD_SELECTED_CONTAINER_IDS = "selectedContainerIds";
    private static final String FIELD_NUMBER_OF_DAYS = "numberOfDays";
    private static final String FIELD_AVERAGE_PICKUP_TIME_MINUTES = "averagePickupTimeMinutes";
    private static final String FIELD_COLLECTION_START_TIME = "collectionStartTime";
    private static final String FIELD_AVERAGE_TRANSFER_TIME_MINUTES = "averageTransferTimeMinutes";
    private static final String FIELD_DISTANCE_WEIGHT = "distanceWeight";
    private static final String FIELD_FILL_WEIGHT = "fillWeight";

    /**
     * Deserializes JSON content into an AlgorithmExecutionRequestBody.
     *
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized request body
     * @throws IOException if parsing or validation fails
     */
    @Override
    public AlgorithmExecutionRequestBody deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        List<FieldError> errors = new ArrayList<>();
        List<FacilityVehiclesSelectionRequestBody> facilitiesWithVehicles =
                parseFacilitySelections(rootNode, parser, errors);
        List<UUID> selectedContainerIds = parseSelectedContainerIds(rootNode, errors);
        NumberOfDays numberOfDays = parseNumberOfDays(rootNode, errors);
        AveragePickupTimeMinutes averagePickupTimeMinutes = parseAveragePickupTimeMinutes(rootNode, errors);
        CollectionStartTime collectionStartTime = parseCollectionStartTime(rootNode, errors);
        AverageTransferTimeMinutes averageTransferTimeMinutes = parseAverageTransferTimeMinutes(rootNode, errors);
        GreedyWeights greedyWeights = parseGreedyWeights(rootNode, errors);
        MaximumBudget maxBudget = parseMaximumBudget(rootNode, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        AlgorithmExecutionRequestBody requestBody = new AlgorithmExecutionRequestBody();
        requestBody.facilitiesWithVehicles = facilitiesWithVehicles;
        requestBody.selectedContainerIds = selectedContainerIds;
        requestBody.numberOfDays = numberOfDays;
        requestBody.averagePickupTimeMinutes = averagePickupTimeMinutes;
        requestBody.collectionStartTime = collectionStartTime;
        requestBody.averageTransferTimeMinutes = averageTransferTimeMinutes;
        requestBody.greedyWeights = greedyWeights;
        requestBody.maxBudget = maxBudget;
        return requestBody;
    }

    /**
     * Parses facility and vehicle selections from the request.
     *
     * @param rootNode the root JSON node
     * @param parser the JSON parser used for nested conversion
     * @param errors validation errors collected during parsing
     * @return parsed facility selections, or null when absent or invalid
     * @throws IOException if nested deserialization fails
     */
    private List<FacilityVehiclesSelectionRequestBody> parseFacilitySelections(
            JsonNode rootNode,
            JsonParser parser,
            List<FieldError> errors) throws IOException {
        if (!rootNode.has(FIELD_FACILITIES_WITH_VEHICLES) || rootNode.get(FIELD_FACILITIES_WITH_VEHICLES).isNull()) {
            return null;
        }
        JsonNode facilitiesNode = rootNode.get(FIELD_FACILITIES_WITH_VEHICLES);
        if (!facilitiesNode.isArray()) {
            errors.add(new FieldError(FIELD_FACILITIES_WITH_VEHICLES, "Must be an array"));
            return null;
        }
        List<FacilityVehiclesSelectionRequestBody> selections = new ArrayList<>();
        for (JsonNode facilityNode : facilitiesNode) {
            selections.add(parser.getCodec().treeToValue(facilityNode, FacilityVehiclesSelectionRequestBody.class));
        }
        return selections;
    }

    /**
     * Parses selected container identifiers from the request.
     *
     * @param rootNode the root JSON node
     * @param errors validation errors collected during parsing
     * @return parsed container ids, or null when absent or invalid
     */
    private List<UUID> parseSelectedContainerIds(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(FIELD_SELECTED_CONTAINER_IDS) || rootNode.get(FIELD_SELECTED_CONTAINER_IDS).isNull()) {
            return null;
        }
        JsonNode containerIdsNode = rootNode.get(FIELD_SELECTED_CONTAINER_IDS);
        if (!containerIdsNode.isArray()) {
            errors.add(new FieldError(FIELD_SELECTED_CONTAINER_IDS, "Must be an array"));
            return null;
        }
        List<UUID> containerIds = new ArrayList<>();
        for (JsonNode containerIdNode : containerIdsNode) {
            try {
                containerIds.add(UUID.fromString(containerIdNode.asText()));
            } catch (IllegalArgumentException e) {
                errors.add(new FieldError(FIELD_SELECTED_CONTAINER_IDS, "Contains an invalid UUID value"));
            }
        }
        return containerIds;
    }

    /**
     * Parses the optional number of days.
     *
     * @param rootNode the root JSON node
     * @param errors validation errors collected during parsing
     * @return parsed number of days, or null when absent or invalid
     */
    private NumberOfDays parseNumberOfDays(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(FIELD_NUMBER_OF_DAYS) || rootNode.get(FIELD_NUMBER_OF_DAYS).isNull()) {
            return null;
        }
        try {
            return new NumberOfDays(rootNode.get(FIELD_NUMBER_OF_DAYS).asInt());
        } catch (IllegalArgumentException e) {
            errors.add(new FieldError(FIELD_NUMBER_OF_DAYS, e.getMessage()));
            return null;
        }
    }

    /**
     * Parses the optional average pickup time in minutes.
     *
     * @param rootNode the root JSON node
     * @param errors validation errors collected during parsing
     * @return parsed average pickup time, or null when absent or invalid
     */
    private AveragePickupTimeMinutes parseAveragePickupTimeMinutes(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(FIELD_AVERAGE_PICKUP_TIME_MINUTES)
                || rootNode.get(FIELD_AVERAGE_PICKUP_TIME_MINUTES).isNull()) {
            return null;
        }
        try {
            return new AveragePickupTimeMinutes(rootNode.get(FIELD_AVERAGE_PICKUP_TIME_MINUTES).asInt());
        } catch (IllegalArgumentException e) {
            errors.add(new FieldError(FIELD_AVERAGE_PICKUP_TIME_MINUTES, e.getMessage()));
            return null;
        }
    }

    /**
     * Parses the optional collection start time ("HH:mm").
     *
     * @param rootNode the root JSON node
     * @param errors validation errors collected during parsing
     * @return parsed collection start time, or null when absent or invalid
     */
    private CollectionStartTime parseCollectionStartTime(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(FIELD_COLLECTION_START_TIME) || rootNode.get(FIELD_COLLECTION_START_TIME).isNull()) {
            return null;
        }
        try {
            return CollectionStartTime.fromString(rootNode.get(FIELD_COLLECTION_START_TIME).asText());
        } catch (IllegalArgumentException e) {
            errors.add(new FieldError(FIELD_COLLECTION_START_TIME, e.getMessage()));
            return null;
        }
    }

    /**
     * Parses the optional average transfer time in minutes.
     *
     * @param rootNode the root JSON node
     * @param errors validation errors collected during parsing
     * @return parsed average transfer time, or null when absent or invalid
     */
    private AverageTransferTimeMinutes parseAverageTransferTimeMinutes(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(FIELD_AVERAGE_TRANSFER_TIME_MINUTES)
                || rootNode.get(FIELD_AVERAGE_TRANSFER_TIME_MINUTES).isNull()) {
            return null;
        }
        try {
            return new AverageTransferTimeMinutes(rootNode.get(FIELD_AVERAGE_TRANSFER_TIME_MINUTES).asInt());
        } catch (IllegalArgumentException e) {
            errors.add(new FieldError(FIELD_AVERAGE_TRANSFER_TIME_MINUTES, e.getMessage()));
            return null;
        }
    }

    /**
     * Parses the optional greedy weights (distance and fill).
     * Both weights are required together; when neither is present the value is left null.
     *
     * @param rootNode the root JSON node
     * @param errors validation errors collected during parsing
     * @return parsed greedy weights, or null when absent or invalid
     */
    private GreedyWeights parseGreedyWeights(JsonNode rootNode, List<FieldError> errors) {
        boolean hasDistance = rootNode.has(FIELD_DISTANCE_WEIGHT) && !rootNode.get(FIELD_DISTANCE_WEIGHT).isNull();
        boolean hasFill = rootNode.has(FIELD_FILL_WEIGHT) && !rootNode.get(FIELD_FILL_WEIGHT).isNull();
        if (!hasDistance && !hasFill) {
            return null;
        }
        if (!hasDistance) {
            errors.add(new FieldError(FIELD_DISTANCE_WEIGHT, "Field is required when fillWeight is provided"));
            return null;
        }
        if (!hasFill) {
            errors.add(new FieldError(FIELD_FILL_WEIGHT, "Field is required when distanceWeight is provided"));
            return null;
        }
        try {
            return new GreedyWeights(
                    rootNode.get(FIELD_DISTANCE_WEIGHT).asDouble(),
                    rootNode.get(FIELD_FILL_WEIGHT).asDouble());
        } catch (IllegalArgumentException e) {
            errors.add(new FieldError(FIELD_DISTANCE_WEIGHT, e.getMessage()));
            return null;
        }
    }

    /**
     * Parses the optional maximum budget.
     *
     * @param rootNode the root JSON node
     * @param errors validation errors collected during parsing
     * @return parsed maximum budget, or null when absent or invalid
     */
    private MaximumBudget parseMaximumBudget(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.MAX_BUDGET) || rootNode.get(JsonFields.MAX_BUDGET).isNull()) {
            return null;
        }
        JsonNode maxBudgetNode = rootNode.get(JsonFields.MAX_BUDGET);
        if (!maxBudgetNode.isObject()) {
            errors.add(new FieldError(JsonFields.MAX_BUDGET, "Must be an object"));
            return null;
        }
        if (!maxBudgetNode.has(JsonFields.AMOUNT) || maxBudgetNode.get(JsonFields.AMOUNT).isNull()) {
            errors.add(new FieldError(JsonFields.MAX_BUDGET + "." + JsonFields.AMOUNT, "Field is required"));
            return null;
        }
        try {
            double amount = maxBudgetNode.get(JsonFields.AMOUNT).asDouble();
            if (maxBudgetNode.has(JsonFields.CURRENCY) && !maxBudgetNode.get(JsonFields.CURRENCY).isNull()) {
                return new MaximumBudget(amount, new Currency(maxBudgetNode.get(JsonFields.CURRENCY).asText()));
            }
            return new MaximumBudget(amount);
        } catch (IllegalArgumentException e) {
            errors.add(new FieldError(JsonFields.MAX_BUDGET, e.getMessage()));
            return null;
        }
    }
}
