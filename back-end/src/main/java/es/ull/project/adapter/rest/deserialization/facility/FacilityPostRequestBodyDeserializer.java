package es.ull.project.adapter.rest.deserialization.facility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.exception.FieldError;
import es.ull.project.adapter.rest.exception.ValidationException;
import es.ull.project.adapter.rest.request.facility.FacilityPostRequestBody;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.location.Location;

/**
 * FacilityPostRequestBodyDeserializer
 * 
 * Custom JSON deserializer for FacilityPostRequestBody.
 * This class converts incoming JSON from POST requests into FacilityPostRequestBody
 * objects, performing validation and constructing required value objects from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested value objects (Location, Capacity, OpeningFixedCost)
 * by extracting their constituent parts from the JSON structure.
 */
public class FacilityPostRequestBodyDeserializer extends JsonDeserializer<FacilityPostRequestBody> {

    /**
     * Deserializes JSON content into a FacilityPostRequestBody object.
     * 
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized FacilityPostRequestBody
     * @throws IOException if parsing or validation fails
     */
    @Override
    public FacilityPostRequestBody deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException {
        
        JsonNode rootNode = parser.getCodec().readTree(parser);
        List<FieldError> errors = new ArrayList<>();
        
        // Parse all fields, accumulating errors instead of throwing immediately
        FacilityType facilityType = parseFacilityType(rootNode, errors);
        Location location = parseLocation(rootNode, errors);
        Capacity capacity = parseCapacity(rootNode, errors);
        OpeningFixedCost openingFixedCost = parseOpeningFixedCost(rootNode, errors);
        FacilityStatus status = parseStatus(rootNode, errors);
        
        // If there are any validation errors, throw ValidationException with all of them
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        
        // Create and populate request body
        FacilityPostRequestBody requestBody = new FacilityPostRequestBody();
        requestBody.facilityType = facilityType;
        requestBody.location = location;
        requestBody.capacity = capacity;
        requestBody.openingFixedCost = openingFixedCost;
        requestBody.status = status;
        
        return requestBody;
    }

    /**
     * Parses the facilityType field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed FacilityType enum, or null if validation fails
     */
    private FacilityType parseFacilityType(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.FACILITY_TYPE)) {
            errors.add(new FieldError(JsonFields.FACILITY_TYPE, "Field is required"));
            return null;
        }
        
        JsonNode node = rootNode.get(JsonFields.FACILITY_TYPE);
        if (node.isNull() || !node.isTextual()) {
            errors.add(new FieldError(JsonFields.FACILITY_TYPE, "Must be a non-null string"));
            return null;
        }
        
        String value = node.asText();
        try {
            return FacilityType.fromString(value);
        } catch (Exception e) {
            errors.add(new FieldError(JsonFields.FACILITY_TYPE, "Invalid value '" + value + "'"));
            return null;
        }
    }

    /**
     * Parses the location nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed Location value object, or null if validation fails
     */
    private Location parseLocation(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.LOCATION)) {
            errors.add(new FieldError(JsonFields.LOCATION, "Field is required"));
            return null;
        }
        
        JsonNode locationNode = rootNode.get(JsonFields.LOCATION);
        if (locationNode.isNull() || !locationNode.isObject()) {
            errors.add(new FieldError(JsonFields.LOCATION, "Must be a non-null object"));
            return null;
        }
        
        // Track required nested fields
        Double latitude = null;
        Double longitude = null;
        
        // Extract latitude
        if (!locationNode.has(JsonFields.LATITUDE)) {
            errors.add(new FieldError(
                JsonFields.LOCATION + "." + JsonFields.LATITUDE,
                "Field is required"
            ));
        } else {
            try {
                latitude = locationNode.get(JsonFields.LATITUDE).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.LOCATION + "." + JsonFields.LATITUDE,
                    "Must be a valid number"
                ));
            }
        }
        
        // Extract longitude
        if (!locationNode.has(JsonFields.LONGITUDE)) {
            errors.add(new FieldError(
                JsonFields.LOCATION + "." + JsonFields.LONGITUDE,
                "Field is required"
            ));
        } else {
            try {
                longitude = locationNode.get(JsonFields.LONGITUDE).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.LOCATION + "." + JsonFields.LONGITUDE,
                    "Must be a valid number"
                ));
            }
        }
        
        // Extract postal address (optional)
        String postalAddress = null;
        if (locationNode.has(JsonFields.POSTAL_ADDRESS) && !locationNode.get(JsonFields.POSTAL_ADDRESS).isNull()) {
            postalAddress = locationNode.get(JsonFields.POSTAL_ADDRESS).asText();
        }
        
        // Extract GIS reference (optional)
        String gisReference = null;
        if (locationNode.has(JsonFields.GIS_REFERENCE) && !locationNode.get(JsonFields.GIS_REFERENCE).isNull()) {
            gisReference = locationNode.get(JsonFields.GIS_REFERENCE).asText();
        }
        
        // Only create Location if required fields are valid
        if (latitude != null && longitude != null) {
            try {
                return new Location(latitude, longitude, postalAddress, gisReference);
            } catch (Exception e) {
                errors.add(new FieldError(JsonFields.LOCATION, "Invalid location: " + e.getMessage()));
                return null;
            }
        }
        
        return null;
    }

    /**
     * Parses the capacity nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed Capacity value object, or null if validation fails
     */
    private Capacity parseCapacity(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.CAPACITY)) {
            errors.add(new FieldError(JsonFields.CAPACITY, "Field is required"));
            return null;
        }
        
        JsonNode capacityNode = rootNode.get(JsonFields.CAPACITY);
        if (capacityNode.isNull() || !capacityNode.isObject()) {
            errors.add(new FieldError(JsonFields.CAPACITY, "Must be a non-null object"));
            return null;
        }
        
        // Track required nested fields
        Double value = null;
        QuantityUnit quantityUnit = null;
        TimeUnit timeUnit = null;
        
        // Extract capacity value
        if (!capacityNode.has(JsonFields.CAPACITY_VALUE)) {
            errors.add(new FieldError(
                JsonFields.CAPACITY + "." + JsonFields.CAPACITY_VALUE,
                "Field is required"
            ));
        } else {
            try {
                value = capacityNode.get(JsonFields.CAPACITY_VALUE).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.CAPACITY + "." + JsonFields.CAPACITY_VALUE,
                    "Must be a valid number"
                ));
            }
        }
        
        // Extract quantity unit
        if (!capacityNode.has(JsonFields.QUANTITY_UNIT)) {
            errors.add(new FieldError(
                JsonFields.CAPACITY + "." + JsonFields.QUANTITY_UNIT,
                "Field is required"
            ));
        } else {
            try {
                String quantityUnitStr = capacityNode.get(JsonFields.QUANTITY_UNIT).asText();
                quantityUnit = new QuantityUnit(quantityUnitStr);
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.CAPACITY + "." + JsonFields.QUANTITY_UNIT,
                    "Invalid value"
                ));
            }
        }
        
        // Extract time unit
        if (!capacityNode.has(JsonFields.TIME_UNIT)) {
            errors.add(new FieldError(
                JsonFields.CAPACITY + "." + JsonFields.TIME_UNIT,
                "Field is required"
            ));
        } else {
            try {
                String timeUnitStr = capacityNode.get(JsonFields.TIME_UNIT).asText();
                timeUnit = TimeUnit.valueOf(timeUnitStr.toUpperCase());
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.CAPACITY + "." + JsonFields.TIME_UNIT,
                    "Invalid time unit"
                ));
            }
        }
        
        // Only create Capacity if all required fields are valid
        if (value != null && quantityUnit != null && timeUnit != null) {
            try {
                return new Capacity(value, quantityUnit, timeUnit);
            } catch (Exception e) {
                errors.add(new FieldError(JsonFields.CAPACITY, "Invalid capacity: " + e.getMessage()));
                return null;
            }
        }
        
        return null;
    }

    /**
     * Parses the openingFixedCost nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed OpeningFixedCost value object, or null if validation fails
     */
    private OpeningFixedCost parseOpeningFixedCost(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.OPENING_FIXED_COST)) {
            errors.add(new FieldError(JsonFields.OPENING_FIXED_COST, "Field is required"));
            return null;
        }
        
        JsonNode costNode = rootNode.get(JsonFields.OPENING_FIXED_COST);
        if (costNode.isNull() || !costNode.isObject()) {
            errors.add(new FieldError(JsonFields.OPENING_FIXED_COST, "Must be a non-null object"));
            return null;
        }
        
        // Extract amount (required)
        Double amount = null;
        if (!costNode.has(JsonFields.AMOUNT)) {
            errors.add(new FieldError(
                JsonFields.OPENING_FIXED_COST + "." + JsonFields.AMOUNT,
                "Field is required"
            ));
        } else {
            try {
                amount = costNode.get(JsonFields.AMOUNT).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.OPENING_FIXED_COST + "." + JsonFields.AMOUNT,
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
                    JsonFields.OPENING_FIXED_COST + "." + JsonFields.CURRENCY,
                    "Invalid currency code"
                ));
            }
        }
        
        // Only create OpeningFixedCost if amount is valid
        if (amount != null) {
            try {
                if (currency == null) {
                    return new OpeningFixedCost(amount);
                } else {
                    return new OpeningFixedCost(amount, currency);
                }
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.OPENING_FIXED_COST,
                    "Invalid opening cost: " + e.getMessage()
                ));
                return null;
            }
        }
        
        return null;
    }

    /**
     * Parses the status field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed FacilityStatus enum, or null if validation fails
     */
    private FacilityStatus parseStatus(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.STATUS)) {
            errors.add(new FieldError(JsonFields.STATUS, "Field is required"));
            return null;
        }
        
        JsonNode node = rootNode.get(JsonFields.STATUS);
        if (node.isNull() || !node.isTextual()) {
            errors.add(new FieldError(JsonFields.STATUS, "Must be a non-null string"));
            return null;
        }
        
        String value = node.asText();
        try {
            return FacilityStatus.fromString(value);
        } catch (Exception e) {
            errors.add(new FieldError(JsonFields.STATUS, "Invalid value '" + value + "'"));
            return null;
        }
    }
}
