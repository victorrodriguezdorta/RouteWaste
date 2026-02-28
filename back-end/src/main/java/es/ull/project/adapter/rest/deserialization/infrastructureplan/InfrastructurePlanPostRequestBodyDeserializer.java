package es.ull.project.adapter.rest.deserialization.infrastructureplan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.exception.FieldError;
import es.ull.project.adapter.rest.exception.ValidationException;
import es.ull.project.adapter.rest.request.infrastructureplan.InfrastructurePlanPostRequestBody;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.policy.ServicePolicies;
import es.ull.project.domain.valueobject.time.PlanningPeriod;

/**
 * InfrastructurePlanPostRequestBodyDeserializer
 * 
 * Custom JSON deserializer for InfrastructurePlanPostRequestBody.
 * This class converts incoming JSON from POST requests into InfrastructurePlanPostRequestBody
 * objects, performing validation and constructing required value objects from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested value objects (PlanningPeriod, MaximumBudget, ServicePolicies)
 * by extracting their constituent parts from the JSON structure.
 */
public class InfrastructurePlanPostRequestBodyDeserializer extends JsonDeserializer<InfrastructurePlanPostRequestBody> {

    /**
     * Deserializes JSON content into an InfrastructurePlanPostRequestBody object.
     * 
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized InfrastructurePlanPostRequestBody
     * @throws IOException if parsing or validation fails
     */
    @Override
    public InfrastructurePlanPostRequestBody deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        List<FieldError> errors = new ArrayList<>();
        PlanningPeriod period = parsePeriod(rootNode, errors);
        MaximumBudget maxBudget = parseMaxBudget(rootNode, errors);
        ServicePolicies servicePolicies = parseServicePolicies(rootNode, errors);
        List<UUID> selectedFacilityIds = parseUuidList(rootNode, JsonFields.SELECTED_FACILITY_IDS, errors);
        List<UUID> serviceAssignmentIds = parseUuidList(rootNode, JsonFields.SERVICE_ASSIGNMENT_IDS, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        InfrastructurePlanPostRequestBody requestBody = new InfrastructurePlanPostRequestBody();
        requestBody.period = period;
        requestBody.maxBudget = maxBudget;
        requestBody.servicePolicies = servicePolicies;
        requestBody.selectedFacilityIds = selectedFacilityIds;
        requestBody.serviceAssignmentIds = serviceAssignmentIds;
        return requestBody;
    }

    /**
     * Parses the period field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed PlanningPeriod value object, or null if validation fails
     */
    private PlanningPeriod parsePeriod(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.PERIOD)) {
            errors.add(new FieldError(JsonFields.PERIOD, "Field is required"));
            return null;
        }
        JsonNode node = rootNode.get(JsonFields.PERIOD);
        if (node.isNull() || !node.isTextual()) {
            errors.add(new FieldError(JsonFields.PERIOD, "Must be a non-null string"));
            return null;
        }
        String value = node.asText();
        try {
            return new PlanningPeriod(value);
        } catch (Exception e) {
            errors.add(new FieldError(JsonFields.PERIOD, "Invalid value '" + value + "'"));
            return null;
        }
    }

    /**
     * Parses the maxBudget nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed MaximumBudget value object, or null if validation fails
     */
    private MaximumBudget parseMaxBudget(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.MAX_BUDGET)) {
            errors.add(new FieldError(JsonFields.MAX_BUDGET, "Field is required"));
            return null;
        }
        JsonNode budgetNode = rootNode.get(JsonFields.MAX_BUDGET);
        if (budgetNode.isNull() || !budgetNode.isObject()) {
            errors.add(new FieldError(JsonFields.MAX_BUDGET, "Must be a non-null object"));
            return null;
        }
        Double amount = null;
        if (!budgetNode.has(JsonFields.AMOUNT)) {
            errors.add(new FieldError(
                JsonFields.MAX_BUDGET + "." + JsonFields.AMOUNT,
                "Field is required"
            ));
        } else {
            try {
                amount = budgetNode.get(JsonFields.AMOUNT).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.MAX_BUDGET + "." + JsonFields.AMOUNT,
                    "Must be a valid number"
                ));
            }
        }
        Currency currency = null;
        if (budgetNode.has(JsonFields.CURRENCY) && !budgetNode.get(JsonFields.CURRENCY).isNull()) {
            try {
                String currencyCode = budgetNode.get(JsonFields.CURRENCY).asText();
                if (currencyCode != null && !currencyCode.trim().isEmpty()) {
                    currency = new Currency(currencyCode);
                }
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.MAX_BUDGET + "." + JsonFields.CURRENCY,
                    "Invalid currency code"
                ));
            }
        }
        if (amount != null) {
            try {
                if (currency == null) {
                    return new MaximumBudget(amount);
                } else {
                    return new MaximumBudget(amount, currency);
                }
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.MAX_BUDGET,
                    "Invalid budget: " + e.getMessage()
                ));
                return null;
            }
        }
        return null;
    }

    /**
     * Parses the servicePolicies nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed ServicePolicies value object, or null if validation fails
     */
    private ServicePolicies parseServicePolicies(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.SERVICE_POLICIES)) {
            errors.add(new FieldError(JsonFields.SERVICE_POLICIES, "Field is required"));
            return null;
        }
        JsonNode policiesNode = rootNode.get(JsonFields.SERVICE_POLICIES);
        if (policiesNode.isNull() || !policiesNode.isObject()) {
            errors.add(new FieldError(JsonFields.SERVICE_POLICIES, "Must be a non-null object"));
            return null;
        }
        try {
            Double maxServiceDistance = null;
            if (policiesNode.has(JsonFields.MAX_SERVICE_DISTANCE) && !policiesNode.get(JsonFields.MAX_SERVICE_DISTANCE).isNull()) {
                try {
                    maxServiceDistance = policiesNode.get(JsonFields.MAX_SERVICE_DISTANCE).asDouble();
                } catch (Exception e) {
                    errors.add(new FieldError(
                        JsonFields.SERVICE_POLICIES + "." + JsonFields.MAX_SERVICE_DISTANCE,
                        "Must be a valid number"
                    ));
                }
            }
            Integer maxServiceTime = null;
            if (policiesNode.has(JsonFields.MAX_SERVICE_TIME) && !policiesNode.get(JsonFields.MAX_SERVICE_TIME).isNull()) {
                try {
                    maxServiceTime = policiesNode.get(JsonFields.MAX_SERVICE_TIME).asInt();
                } catch (Exception e) {
                    errors.add(new FieldError(
                        JsonFields.SERVICE_POLICIES + "." + JsonFields.MAX_SERVICE_TIME,
                        "Must be a valid integer"
                    ));
                }
            }
            Integer maxInfrastructureCount = null;
            if (policiesNode.has(JsonFields.MAX_INFRASTRUCTURE_COUNT) && !policiesNode.get(JsonFields.MAX_INFRASTRUCTURE_COUNT).isNull()) {
                try {
                    maxInfrastructureCount = policiesNode.get(JsonFields.MAX_INFRASTRUCTURE_COUNT).asInt();
                } catch (Exception e) {
                    errors.add(new FieldError(
                        JsonFields.SERVICE_POLICIES + "." + JsonFields.MAX_INFRASTRUCTURE_COUNT,
                        "Must be a valid integer"
                    ));
                }
            }
            Double maxEmissions = null;
            if (policiesNode.has(JsonFields.MAX_EMISSIONS) && !policiesNode.get(JsonFields.MAX_EMISSIONS).isNull()) {
                try {
                    maxEmissions = policiesNode.get(JsonFields.MAX_EMISSIONS).asDouble();
                } catch (Exception e) {
                    errors.add(new FieldError(
                        JsonFields.SERVICE_POLICIES + "." + JsonFields.MAX_EMISSIONS,
                        "Must be a valid number"
                    ));
                }
            }
            return new ServicePolicies(maxServiceDistance, maxServiceTime, maxInfrastructureCount, maxEmissions);
        } catch (Exception e) {
            errors.add(new FieldError(
                JsonFields.SERVICE_POLICIES,
                "Invalid service policies: " + e.getMessage()
            ));
            return null;
        }
    }

    /**
     * Parses an optional array of UUID strings from JSON.
     * 
     * @param rootNode the root JSON node
     * @param fieldName the name of the field containing the UUID array
     * @param errors list to accumulate validation errors
     * @return list of parsed UUIDs, or empty list if field is missing/null
     */
    private List<UUID> parseUuidList(JsonNode rootNode, String fieldName, List<FieldError> errors) {
        List<UUID> uuidList = new ArrayList<>();
        if (!rootNode.has(fieldName) || rootNode.get(fieldName).isNull()) {
            return uuidList;
        }
        JsonNode arrayNode = rootNode.get(fieldName);
        if (!arrayNode.isArray()) {
            errors.add(new FieldError(fieldName, "Must be an array"));
            return uuidList;
        }
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode uuidNode = arrayNode.get(i);
            if (uuidNode.isNull() || !uuidNode.isTextual()) {
                errors.add(new FieldError(
                    fieldName + "[" + i + "]",
                    "Must be a valid UUID string"
                ));
                continue;
            }
            try {
                UUID uuid = UUID.fromString(uuidNode.asText());
                uuidList.add(uuid);
            } catch (IllegalArgumentException e) {
                errors.add(new FieldError(
                    fieldName + "[" + i + "]",
                    "Invalid UUID format: " + uuidNode.asText()
                ));
            }
        }
        return uuidList;
    }
}
