package es.ull.project.adapter.rest.deserialization.infrastructureplan;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.request.infrastructureplan.InfrastructurePlanPutRequestBody;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * InfrastructurePlanPutRequestBodyDeserializer
 * 
 * Custom JSON deserializer for InfrastructurePlanPutRequestBody.
 * This class converts incoming JSON from PUT requests into InfrastructurePlanPutRequestBody
 * objects, performing validation and constructing required value objects from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested value objects (PlanningPeriod, MaximumBudget, ServicePolicies)
 * by extracting their constituent parts from the JSON structure.
 */
public class InfrastructurePlanPutRequestBodyDeserializer extends JsonDeserializer<InfrastructurePlanPutRequestBody> {

    /**
     * Deserializes JSON content into an InfrastructurePlanPutRequestBody object.
     * 
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized InfrastructurePlanPutRequestBody
     * @throws IOException if parsing or validation fails
     */
    @Override
    public InfrastructurePlanPutRequestBody deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        try {
            PlanningPeriod period = parsePeriod(rootNode);
            MaximumBudget maxBudget = parseMaxBudget(rootNode);
            ServicePolicies servicePolicies = parseServicePolicies(rootNode);
            List<UUID> selectedFacilityIds = parseUuidList(rootNode, JsonFields.SELECTED_FACILITY_IDS);
            List<UUID> serviceAssignmentIds = parseUuidList(rootNode, JsonFields.SERVICE_ASSIGNMENT_IDS);
            InfrastructurePlanPutRequestBody requestBody = new InfrastructurePlanPutRequestBody();
            requestBody.period = period;
            requestBody.maxBudget = maxBudget;
            requestBody.servicePolicies = servicePolicies;
            requestBody.selectedFacilityIds = selectedFacilityIds;
            requestBody.serviceAssignmentIds = serviceAssignmentIds;
            return requestBody;
        } catch (Exception e) {
            throw new IOException("Failed to deserialize InfrastructurePlanPutRequestBody: " + e.getMessage(), e);
        }
    }

    /**
     * Parses the period field from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed PlanningPeriod value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private PlanningPeriod parsePeriod(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.PERIOD)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.PERIOD + "' is missing");
        }
        JsonNode node = rootNode.get(JsonFields.PERIOD);
        if (node.isNull() || !node.isTextual()) {
            throw new IllegalArgumentException("Field '" + JsonFields.PERIOD + "' must be a non-null string");
        }
        String value = node.asText();
        try {
            return new PlanningPeriod(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value '" + value + "' for field '" + JsonFields.PERIOD + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the maxBudget nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed MaximumBudget value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private MaximumBudget parseMaxBudget(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.MAX_BUDGET)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.MAX_BUDGET + "' is missing");
        }
        JsonNode budgetNode = rootNode.get(JsonFields.MAX_BUDGET);
        if (budgetNode.isNull() || !budgetNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.MAX_BUDGET + "' must be a non-null object");
        }
        try {
            if (!budgetNode.has(JsonFields.AMOUNT)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.AMOUNT + "' is missing");
            }
            double amount = budgetNode.get(JsonFields.AMOUNT).asDouble();
            String currencyCode = null;
            if (budgetNode.has(JsonFields.CURRENCY) && !budgetNode.get(JsonFields.CURRENCY).isNull()) {
                currencyCode = budgetNode.get(JsonFields.CURRENCY).asText();
            }
            if (currencyCode == null || currencyCode.trim().isEmpty()) {
                return new MaximumBudget(amount);
            } else {
                Currency currency = new Currency(currencyCode);
                return new MaximumBudget(amount, currency);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.MAX_BUDGET + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the servicePolicies nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed ServicePolicies value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private ServicePolicies parseServicePolicies(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.SERVICE_POLICIES)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.SERVICE_POLICIES + "' is missing");
        }
        JsonNode policiesNode = rootNode.get(JsonFields.SERVICE_POLICIES);
        if (policiesNode.isNull() || !policiesNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.SERVICE_POLICIES + "' must be a non-null object");
        }
        try {
            Double maxServiceDistance = null;
            if (policiesNode.has(JsonFields.MAX_SERVICE_DISTANCE) && !policiesNode.get(JsonFields.MAX_SERVICE_DISTANCE).isNull()) {
                maxServiceDistance = policiesNode.get(JsonFields.MAX_SERVICE_DISTANCE).asDouble();
            }
            Integer maxServiceTime = null;
            if (policiesNode.has(JsonFields.MAX_SERVICE_TIME) && !policiesNode.get(JsonFields.MAX_SERVICE_TIME).isNull()) {
                maxServiceTime = policiesNode.get(JsonFields.MAX_SERVICE_TIME).asInt();
            }
            Integer maxInfrastructureCount = null;
            if (policiesNode.has(JsonFields.MAX_INFRASTRUCTURE_COUNT) && !policiesNode.get(JsonFields.MAX_INFRASTRUCTURE_COUNT).isNull()) {
                maxInfrastructureCount = policiesNode.get(JsonFields.MAX_INFRASTRUCTURE_COUNT).asInt();
            }
            Double maxEmissions = null;
            if (policiesNode.has(JsonFields.MAX_EMISSIONS) && !policiesNode.get(JsonFields.MAX_EMISSIONS).isNull()) {
                maxEmissions = policiesNode.get(JsonFields.MAX_EMISSIONS).asDouble();
            }
            return new ServicePolicies(maxServiceDistance, maxServiceTime, maxInfrastructureCount, maxEmissions);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.SERVICE_POLICIES + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses an optional array of UUID strings from JSON.
     * 
     * @param rootNode the root JSON node
     * @param fieldName the name of the field containing the UUID array
     * @return list of parsed UUIDs, or empty list if field is missing/null
     * @throws IllegalArgumentException if the array contains invalid UUID values
     */
    private List<UUID> parseUuidList(JsonNode rootNode, String fieldName) {
        List<UUID> uuidList = new ArrayList<>();
        if (!rootNode.has(fieldName) || rootNode.get(fieldName).isNull()) {
            return uuidList;
        }
        JsonNode arrayNode = rootNode.get(fieldName);
        if (!arrayNode.isArray()) {
            throw new IllegalArgumentException("Field '" + fieldName + "' must be an array");
        }
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode uuidNode = arrayNode.get(i);
            if (uuidNode.isNull() || !uuidNode.isTextual()) {
                throw new IllegalArgumentException(
                    "Field '" + fieldName + "[" + i + "]' must be a valid UUID string"
                );
            }
            try {
                UUID uuid = UUID.fromString(uuidNode.asText());
                uuidList.add(uuid);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "Invalid UUID format at '" + fieldName + "[" + i + "]: " + uuidNode.asText(), e
                );
            }
        }
        return uuidList;
    }
}
