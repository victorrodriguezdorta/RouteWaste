package es.ull.project.adapter.rest.deserialization.container;

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
import es.ull.project.adapter.rest.request.container.ContainerPostRequestBody;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.demand.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;

/**
 * ContainerPostRequestBodyDeserializer
 * 
 * Custom JSON deserializer for ContainerPostRequestBody.
 * This class converts incoming JSON from POST requests into ContainerPostRequestBody
 * objects, performing validation and constructing required value objects from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested value objects (Location, WasteDemand)
 * by extracting their constituent parts from the JSON structure.
 */
public class ContainerPostRequestBodyDeserializer extends JsonDeserializer<ContainerPostRequestBody> {

    /**
     * Deserializes JSON content into a ContainerPostRequestBody object.
     * 
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized ContainerPostRequestBody
     * @throws IOException if parsing or validation fails
     */
    @Override
    public ContainerPostRequestBody deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        List<FieldError> errors = new ArrayList<>();
        Location location = parseLocation(rootNode, errors);
        WasteType wasteType = parseWasteType(rootNode, errors);
        ContainerCapacityLiters capacityLiters = parseCapacityLiters(rootNode, errors);
        DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = parseDailyDemandLitersPerDay(rootNode, errors);
        ServiceZone serviceZone = parseServiceZone(rootNode, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        ContainerPostRequestBody requestBody = new ContainerPostRequestBody();
        requestBody.location = location;
        requestBody.wasteType = wasteType;
        requestBody.capacityLiters = capacityLiters;
        requestBody.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
        requestBody.serviceZone = serviceZone;
        return requestBody;
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
        Double latitude = null;
        Double longitude = null;
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
        String postalAddress = null;
        if (locationNode.has(JsonFields.POSTAL_ADDRESS) && !locationNode.get(JsonFields.POSTAL_ADDRESS).isNull()) {
            postalAddress = locationNode.get(JsonFields.POSTAL_ADDRESS).asText();
        }
        String gisReference = null;
        if (locationNode.has(JsonFields.GIS_REFERENCE) && !locationNode.get(JsonFields.GIS_REFERENCE).isNull()) {
            gisReference = locationNode.get(JsonFields.GIS_REFERENCE).asText();
        }
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
     * Parses the wasteType field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed WasteType enum, or null if validation fails
     */
    private WasteType parseWasteType(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.WASTE_TYPE)) {
            errors.add(new FieldError(JsonFields.WASTE_TYPE, "Field is required"));
            return null;
        }
        JsonNode node = rootNode.get(JsonFields.WASTE_TYPE);
        if (node.isNull() || !node.isTextual()) {
            errors.add(new FieldError(JsonFields.WASTE_TYPE, "Must be a non-null string"));
            return null;
        }
        String value = node.asText();
        try {
            return WasteType.fromString(value);
        } catch (Exception e) {
            errors.add(new FieldError(JsonFields.WASTE_TYPE, "Invalid value '" + value + "'"));
            return null;
        }
    }

    /**
     * Parses the capacity liters field from JSON.
     * Handles both formats: simple number or nested object {liters: number}
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed ContainerCapacityLiters value object, or null if validation fails
     */
    private ContainerCapacityLiters parseCapacityLiters(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.CAPACITY_LITERS)) {
            errors.add(new FieldError(JsonFields.CAPACITY_LITERS, "Field is required"));
            return null;
        }
        try {
            JsonNode capacityNode = rootNode.get(JsonFields.CAPACITY_LITERS);
            double liters;
            
            if (capacityNode.isNumber()) {
                // Simple number format
                liters = capacityNode.asDouble();
            } else if (capacityNode.isObject() && capacityNode.has("liters")) {
                // Nested object format {liters: number}
                liters = capacityNode.get("liters").asDouble();
            } else {
                errors.add(new FieldError(JsonFields.CAPACITY_LITERS, "Must be a valid number or object with 'liters' field"));
                return null;
            }
            return new ContainerCapacityLiters(liters);
        } catch (Exception e) {
            errors.add(new FieldError(JsonFields.CAPACITY_LITERS, "Must be a valid number"));
            return null;
        }
    }

    /**
     * Parses the daily demand liters per day field from JSON.
     * Handles both formats: simple number or nested object {litersPerDay: number}
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed DailyWasteDemandLitersPerDay value object, or null if validation fails
     */
    private DailyWasteDemandLitersPerDay parseDailyDemandLitersPerDay(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.DAILY_DEMAND_LITERS_PER_DAY)) {
            errors.add(new FieldError(JsonFields.DAILY_DEMAND_LITERS_PER_DAY, "Field is required"));
            return null;
        }
        try {
            JsonNode demandNode = rootNode.get(JsonFields.DAILY_DEMAND_LITERS_PER_DAY);
            double litersPerDay;
            
            if (demandNode.isNumber()) {
                // Simple number format
                litersPerDay = demandNode.asDouble();
            } else if (demandNode.isObject() && demandNode.has("litersPerDay")) {
                // Nested object format {litersPerDay: number}
                litersPerDay = demandNode.get("litersPerDay").asDouble();
            } else {
                errors.add(new FieldError(JsonFields.DAILY_DEMAND_LITERS_PER_DAY, "Must be a valid number or object with 'litersPerDay' field"));
                return null;
            }
            return new DailyWasteDemandLitersPerDay(litersPerDay);
        } catch (Exception e) {
            errors.add(new FieldError(JsonFields.DAILY_DEMAND_LITERS_PER_DAY, "Must be a valid number"));
            return null;
        }
    }

    /**
     * Parses the serviceZone field from JSON (optional field).
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed ServiceZone enum, or null if not present
     */
    private ServiceZone parseServiceZone(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.SERVICE_ZONE) || rootNode.get(JsonFields.SERVICE_ZONE).isNull()) {
            return null;
        }
        JsonNode node = rootNode.get(JsonFields.SERVICE_ZONE);
        if (!node.isTextual()) {
            errors.add(new FieldError(JsonFields.SERVICE_ZONE, "Must be a string"));
            return null;
        }
        String value = node.asText();
        try {
            return ServiceZone.fromString(value);
        } catch (Exception e) {
            errors.add(new FieldError(JsonFields.SERVICE_ZONE, "Invalid value '" + value + "'"));
            return null;
        }
    }
}
