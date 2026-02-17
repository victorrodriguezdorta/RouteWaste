package es.ull.project.adapter.rest.deserialization.infrastructureplan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        
        // Parse all fields, accumulating errors instead of throwing immediately
        PlanningPeriod period = parsePeriod(rootNode, errors);
        MaximumBudget maxBudget = parseMaxBudget(rootNode, errors);
        ServicePolicies servicePolicies = parseServicePolicies(rootNode, errors);
        
        // If there are any validation errors, throw ValidationException with all of them
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        
        // Create and populate request body
        InfrastructurePlanPostRequestBody requestBody = new InfrastructurePlanPostRequestBody();
        requestBody.period = period;
        requestBody.maxBudget = maxBudget;
        requestBody.servicePolicies = servicePolicies;
        
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
        
        // Extract amount (required)
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
        
        // Extract currency (optional)
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
        
        // Only create MaximumBudget if amount is valid
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
            // Extract maxServiceDistance (optional)
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
            
            // Extract maxServiceTime (optional)
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
            
            // Extract maxInfrastructureCount (optional)
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
            
            // Extract maxEmissions (optional)
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
}
