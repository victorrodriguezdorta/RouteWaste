package es.ull.project.adapter.rest.deserialization.serviceassignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.exception.FieldError;
import es.ull.project.adapter.rest.exception.ValidationException;
import es.ull.project.adapter.rest.request.serviceassignment.ServiceAssignmentPostRequestBody;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;

/**
 * ServiceAssignmentPostRequestBodyDeserializer
 * 
 * Custom JSON deserializer for ServiceAssignmentPostRequestBody.
 * This class converts incoming JSON from POST requests into ServiceAssignmentPostRequestBody
 * objects, performing validation and constructing required value objects from primitive JSON fields.
 * 
 * The deserializer receives only the UUIDs of the container and facility. The service layer
 * will be responsible for fetching the complete entities from their respective repositories.
 * This approach ensures data integrity, avoids duplication, and follows REST best practices
 * for referencing existing resources.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails.
 */
public class ServiceAssignmentPostRequestBodyDeserializer extends JsonDeserializer<ServiceAssignmentPostRequestBody> {

    /**
     * Deserializes JSON content into a ServiceAssignmentPostRequestBody object.
     * 
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized ServiceAssignmentPostRequestBody
     * @throws IOException if parsing or validation fails
     */
    @Override
    public ServiceAssignmentPostRequestBody deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException {
        
        JsonNode rootNode = parser.getCodec().readTree(parser);
        List<FieldError> errors = new ArrayList<>();
        
        // Parse all fields, accumulating errors instead of throwing immediately
        UUID containerId = parseContainerId(rootNode, errors);
        UUID facilityId = parseFacilityId(rootNode, errors);
        WasteDemand wasteDemand = parseWasteDemand(rootNode, errors);
        Distance distance = parseDistance(rootNode, errors);
        ServiceTime serviceTime = parseServiceTime(rootNode, errors);
        TransportationVariableCost transportCost = parseTransportCost(rootNode, errors);
        
        // If there are any validation errors, throw ValidationException with all of them
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        
        // Create and populate request body
        ServiceAssignmentPostRequestBody requestBody = new ServiceAssignmentPostRequestBody();
        requestBody.containerId = containerId;
        requestBody.facilityId = facilityId;
        requestBody.wasteDemand = wasteDemand;
        requestBody.distance = distance;
        requestBody.serviceTime = serviceTime;
        requestBody.transportCost = transportCost;
        
        return requestBody;
    }

    /**
     * Parses the containerId field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed container UUID, or null if validation fails
     */
    private UUID parseContainerId(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.CONTAINER_ID)) {
            errors.add(new FieldError(JsonFields.CONTAINER_ID, "Field is required"));
            return null;
        }
        
        JsonNode containerIdNode = rootNode.get(JsonFields.CONTAINER_ID);
        if (containerIdNode.isNull()) {
            errors.add(new FieldError(JsonFields.CONTAINER_ID, "Cannot be null"));
            return null;
        }
        
        try {
            String containerIdStr = containerIdNode.asText();
            return UUID.fromString(containerIdStr);
        } catch (IllegalArgumentException e) {
            errors.add(new FieldError(JsonFields.CONTAINER_ID, "Must be a valid UUID"));
            return null;
        }
    }

    /**
     * Parses the facilityId field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed facility UUID, or null if validation fails
     */
    private UUID parseFacilityId(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.FACILITY_ID)) {
            errors.add(new FieldError(JsonFields.FACILITY_ID, "Field is required"));
            return null;
        }
        
        JsonNode facilityIdNode = rootNode.get(JsonFields.FACILITY_ID);
        if (facilityIdNode.isNull()) {
            errors.add(new FieldError(JsonFields.FACILITY_ID, "Cannot be null"));
            return null;
        }
        
        try {
            String facilityIdStr = facilityIdNode.asText();
            return UUID.fromString(facilityIdStr);
        } catch (IllegalArgumentException e) {
            errors.add(new FieldError(JsonFields.FACILITY_ID, "Must be a valid UUID"));
            return null;
        }
    }

    /**
     * Parses the wasteDemand field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed WasteDemand value object, or null if validation fails
     */
    private WasteDemand parseWasteDemand(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.WASTE_DEMAND)) {
            errors.add(new FieldError(JsonFields.WASTE_DEMAND, "Field is required"));
            return null;
        }
        
        JsonNode demandNode = rootNode.get(JsonFields.WASTE_DEMAND);
        if (demandNode.isNull() || !demandNode.isObject()) {
            errors.add(new FieldError(JsonFields.WASTE_DEMAND, "Must be a non-null object"));
            return null;
        }
        
        // Track required nested fields
        Double value = null;
        QuantityUnit quantityUnit = null;
        TimeUnit timeUnit = null;
        
        // Extract demand value
        if (!demandNode.has(JsonFields.CAPACITY_VALUE)) {
            errors.add(new FieldError(
                JsonFields.WASTE_DEMAND + "." + JsonFields.CAPACITY_VALUE,
                "Field is required"
            ));
        } else {
            try {
                value = demandNode.get(JsonFields.CAPACITY_VALUE).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.WASTE_DEMAND + "." + JsonFields.CAPACITY_VALUE,
                    "Must be a valid number"
                ));
            }
        }
        
        // Extract quantity unit
        if (!demandNode.has(JsonFields.QUANTITY_UNIT)) {
            errors.add(new FieldError(
                JsonFields.WASTE_DEMAND + "." + JsonFields.QUANTITY_UNIT,
                "Field is required"
            ));
        } else {
            try {
                String quantityUnitStr = demandNode.get(JsonFields.QUANTITY_UNIT).asText();
                quantityUnit = new QuantityUnit(quantityUnitStr);
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.WASTE_DEMAND + "." + JsonFields.QUANTITY_UNIT,
                    "Invalid value"
                ));
            }
        }
        
        // Extract time unit
        if (!demandNode.has(JsonFields.TIME_UNIT)) {
            errors.add(new FieldError(
                JsonFields.WASTE_DEMAND + "." + JsonFields.TIME_UNIT,
                "Field is required"
            ));
        } else {
            try {
                String timeUnitStr = demandNode.get(JsonFields.TIME_UNIT).asText();
                timeUnit = TimeUnit.valueOf(timeUnitStr.toUpperCase());
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.WASTE_DEMAND + "." + JsonFields.TIME_UNIT,
                    "Invalid time unit"
                ));
            }
        }
        
        // Only create WasteDemand if all required fields are valid
        if (value != null && quantityUnit != null && timeUnit != null) {
            try {
                return new WasteDemand(value, quantityUnit, timeUnit);
            } catch (Exception e) {
                errors.add(new FieldError(JsonFields.WASTE_DEMAND, "Invalid waste demand: " + e.getMessage()));
                return null;
            }
        }
        
        return null;
    }

    /**
     * Parses the distance field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed Distance value object, or null if validation fails
     */
    private Distance parseDistance(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.DISTANCE)) {
            errors.add(new FieldError(JsonFields.DISTANCE, "Field is required"));
            return null;
        }
        
        JsonNode distanceNode = rootNode.get(JsonFields.DISTANCE);
        if (distanceNode.isNull() || !distanceNode.isObject()) {
            errors.add(new FieldError(JsonFields.DISTANCE, "Must be a non-null object"));
            return null;
        }
        
        try {
            // Check which unit is provided (meters, kilometers, or miles)
            if (distanceNode.has(JsonFields.METERS)) {
                try {
                    double meters = distanceNode.get(JsonFields.METERS).asDouble();
                    return Distance.fromMeters(meters);
                } catch (Exception e) {
                    errors.add(new FieldError(
                        JsonFields.DISTANCE + "." + JsonFields.METERS,
                        "Must be a valid number"
                    ));
                    return null;
                }
            } else if (distanceNode.has(JsonFields.KILOMETERS)) {
                try {
                    double kilometers = distanceNode.get(JsonFields.KILOMETERS).asDouble();
                    return Distance.fromKilometers(kilometers);
                } catch (Exception e) {
                    errors.add(new FieldError(
                        JsonFields.DISTANCE + "." + JsonFields.KILOMETERS,
                        "Must be a valid number"
                    ));
                    return null;
                }
            } else if (distanceNode.has(JsonFields.MILES)) {
                try {
                    double miles = distanceNode.get(JsonFields.MILES).asDouble();
                    return Distance.fromMiles(miles);
                } catch (Exception e) {
                    errors.add(new FieldError(
                        JsonFields.DISTANCE + "." + JsonFields.MILES,
                        "Must be a valid number"
                    ));
                    return null;
                }
            } else {
                errors.add(new FieldError(
                    JsonFields.DISTANCE,
                    "Must specify one of: meters, kilometers, or miles"
                ));
                return null;
            }
            
        } catch (Exception e) {
            errors.add(new FieldError(JsonFields.DISTANCE, "Invalid distance: " + e.getMessage()));
            return null;
        }
    }

    /**
     * Parses the serviceTime field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed ServiceTime value object, or null if validation fails
     */
    private ServiceTime parseServiceTime(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.SERVICE_TIME)) {
            errors.add(new FieldError(JsonFields.SERVICE_TIME, "Field is required"));
            return null;
        }
        
        JsonNode timeNode = rootNode.get(JsonFields.SERVICE_TIME);
        if (timeNode.isNull() || !timeNode.isObject()) {
            errors.add(new FieldError(JsonFields.SERVICE_TIME, "Must be a non-null object"));
            return null;
        }
        
        try {
            // Check which unit is provided (minutes, hours, or seconds)
            if (timeNode.has(JsonFields.MINUTES)) {
                try {
                    double minutes = timeNode.get(JsonFields.MINUTES).asDouble();
                    return new ServiceTime(minutes);
                } catch (Exception e) {
                    errors.add(new FieldError(
                        JsonFields.SERVICE_TIME + "." + JsonFields.MINUTES,
                        "Must be a valid number"
                    ));
                    return null;
                }
            } else if (timeNode.has(JsonFields.HOURS)) {
                try {
                    double hours = timeNode.get(JsonFields.HOURS).asDouble();
                    return ServiceTime.fromHours(hours);
                } catch (Exception e) {
                    errors.add(new FieldError(
                        JsonFields.SERVICE_TIME + "." + JsonFields.HOURS,
                        "Must be a valid number"
                    ));
                    return null;
                }
            } else if (timeNode.has(JsonFields.SECONDS)) {
                try {
                    double seconds = timeNode.get(JsonFields.SECONDS).asDouble();
                    return ServiceTime.fromSeconds(seconds);
                } catch (Exception e) {
                    errors.add(new FieldError(
                        JsonFields.SERVICE_TIME + "." + JsonFields.SECONDS,
                        "Must be a valid number"
                    ));
                    return null;
                }
            } else {
                errors.add(new FieldError(
                    JsonFields.SERVICE_TIME,
                    "Must specify one of: minutes, hours, or seconds"
                ));
                return null;
            }
            
        } catch (Exception e) {
            errors.add(new FieldError(JsonFields.SERVICE_TIME, "Invalid service time: " + e.getMessage()));
            return null;
        }
    }

    /**
     * Parses the transportCost field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed TransportationVariableCost value object, or null if validation fails
     */
    private TransportationVariableCost parseTransportCost(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.TRANSPORT_COST)) {
            errors.add(new FieldError(JsonFields.TRANSPORT_COST, "Field is required"));
            return null;
        }
        
        JsonNode costNode = rootNode.get(JsonFields.TRANSPORT_COST);
        if (costNode.isNull() || !costNode.isObject()) {
            errors.add(new FieldError(JsonFields.TRANSPORT_COST, "Must be a non-null object"));
            return null;
        }
        
        // Extract amount (required)
        Double amount = null;
        if (!costNode.has(JsonFields.AMOUNT)) {
            errors.add(new FieldError(
                JsonFields.TRANSPORT_COST + "." + JsonFields.AMOUNT,
                "Field is required"
            ));
        } else {
            try {
                amount = costNode.get(JsonFields.AMOUNT).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.TRANSPORT_COST + "." + JsonFields.AMOUNT,
                    "Must be a valid number"
                ));
            }
        }
        
        // Extract currency (optional)
        Currency currency = null;
        if (costNode.has(JsonFields.CURRENCY) && !costNode.get(JsonFields.CURRENCY).isNull()) {
            try {
                String currencyCode = costNode.get(JsonFields.CURRENCY).asText();
                if (currencyCode != null && !currencyCode.trim().isEmpty()) {
                    currency = new Currency(currencyCode);
                }
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.TRANSPORT_COST + "." + JsonFields.CURRENCY,
                    "Invalid currency code"
                ));
            }
        }
        
        // Only create TransportationVariableCost if amount is valid
        if (amount != null) {
            try {
                if (currency == null) {
                    return new TransportationVariableCost(amount);
                } else {
                    return new TransportationVariableCost(amount, currency);
                }
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.TRANSPORT_COST,
                    "Invalid transport cost: " + e.getMessage()
                ));
                return null;
            }
        }
        
        return null;
    }
}
