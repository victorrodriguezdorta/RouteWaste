package es.ull.project.adapter.rest.deserialization.container;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.request.container.ContainerPutRequestBody;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import java.io.IOException;

/**
 * ContainerPutRequestBodyDeserializer
 * 
 * Custom JSON deserializer for ContainerPutRequestBody.
 * This class converts incoming JSON from PUT requests into ContainerPutRequestBody
 * objects, performing validation and constructing required value objects from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested value objects (Location, WasteDemand)
 * by extracting their constituent parts from the JSON structure.
 */
public class ContainerPutRequestBodyDeserializer extends JsonDeserializer<ContainerPutRequestBody> {

    private static final String FIELD_LITERS = "liters";
    private static final String FIELD_LITERS_PER_DAY = "litersPerDay";
    private static final String ERR_INVALID_LITERS = "Must be a valid number or object with 'liters' field";
    private static final String ERR_INVALID_LITERS_PER_DAY = "Must be a valid number or object with 'litersPerDay' field";

    /**
     * Deserializes JSON content into a ContainerPutRequestBody object.
     * 
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized ContainerPutRequestBody
     * @throws IOException if parsing or validation fails
     */
    @Override
    public ContainerPutRequestBody deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        try {
            Location location = parseLocation(rootNode);
            WasteType wasteType = parseWasteType(rootNode);
            ContainerCapacityLiters capacityLiters = parseCapacityLiters(rootNode);
            DailyWasteDemandLitersPerDay dailyDemandLitersPerDay = parseDailyDemandLitersPerDay(rootNode);
            ServiceZone serviceZone = parseServiceZone(rootNode);
            ContainerPutRequestBody requestBody = new ContainerPutRequestBody();
            requestBody.location = location;
            requestBody.wasteType = wasteType;
            requestBody.capacityLiters = capacityLiters;
            requestBody.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
            requestBody.serviceZone = serviceZone;
            return requestBody;
        } catch (Exception e) {
            throw new IOException("Failed to deserialize ContainerPutRequestBody: " + e.getMessage(), e);
        }
    }

    /**
     * Parses the location nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed Location value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private Location parseLocation(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.LOCATION)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.LOCATION + "' is missing");
        }
        JsonNode locationNode = rootNode.get(JsonFields.LOCATION);
        if (locationNode.isNull() || !locationNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.LOCATION + "' must be a non-null object");
        }
        try {
            if (!locationNode.has(JsonFields.LATITUDE)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.LATITUDE + "' is missing");
            }
            double latitude = locationNode.get(JsonFields.LATITUDE).asDouble();
            if (!locationNode.has(JsonFields.LONGITUDE)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.LONGITUDE + "' is missing");
            }
            double longitude = locationNode.get(JsonFields.LONGITUDE).asDouble();
            String postalAddress = null;
            if (locationNode.has(JsonFields.POSTAL_ADDRESS) && !locationNode.get(JsonFields.POSTAL_ADDRESS).isNull()) {
                postalAddress = locationNode.get(JsonFields.POSTAL_ADDRESS).asText();
            }
            String gisReference = null;
            if (locationNode.has(JsonFields.GIS_REFERENCE) && !locationNode.get(JsonFields.GIS_REFERENCE).isNull()) {
                gisReference = locationNode.get(JsonFields.GIS_REFERENCE).asText();
            }
            return new Location(latitude, longitude, postalAddress, gisReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.LOCATION + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the wasteType field from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed WasteType enum
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private WasteType parseWasteType(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.WASTE_TYPE)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.WASTE_TYPE + "' is missing");
        }
        JsonNode node = rootNode.get(JsonFields.WASTE_TYPE);
        if (node.isNull() || !node.isTextual()) {
            throw new IllegalArgumentException("Field '" + JsonFields.WASTE_TYPE + "' must be a non-null string");
        }
        String value = node.asText();
        try {
            return WasteType.fromString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value '" + value + "' for field '" + JsonFields.WASTE_TYPE + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the capacity liters field from JSON.
     * Handles both formats: simple number or nested object {liters: number}
     * 
     * @param rootNode the root JSON node
     * @return the parsed ContainerCapacityLiters value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private ContainerCapacityLiters parseCapacityLiters(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.CAPACITY_LITERS)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.CAPACITY_LITERS + "' is missing");
        }
        try {
            JsonNode capacityNode = rootNode.get(JsonFields.CAPACITY_LITERS);
            double liters;
            if (capacityNode.isNumber()) {
                liters = capacityNode.asDouble();
            } else if (capacityNode.isObject() && capacityNode.has(FIELD_LITERS)) {
                liters = capacityNode.get(FIELD_LITERS).asDouble();
            } else {
                throw new IllegalArgumentException(ERR_INVALID_LITERS);
            }
            return new ContainerCapacityLiters(liters);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.CAPACITY_LITERS + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the daily demand liters per day field from JSON.
     * Handles both formats: simple number or nested object {litersPerDay: number}
     * 
     * @param rootNode the root JSON node
     * @return the parsed DailyWasteDemandLitersPerDay value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private DailyWasteDemandLitersPerDay parseDailyDemandLitersPerDay(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.DAILY_DEMAND_LITERS_PER_DAY)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.DAILY_DEMAND_LITERS_PER_DAY + "' is missing");
        }
        try {
            JsonNode demandNode = rootNode.get(JsonFields.DAILY_DEMAND_LITERS_PER_DAY);
            double litersPerDay;
            if (demandNode.isNumber()) {
                litersPerDay = demandNode.asDouble();
            } else if (demandNode.isObject() && demandNode.has(FIELD_LITERS_PER_DAY)) {
                litersPerDay = demandNode.get(FIELD_LITERS_PER_DAY).asDouble();
            } else {
                throw new IllegalArgumentException(ERR_INVALID_LITERS_PER_DAY);
            }
            return new DailyWasteDemandLitersPerDay(litersPerDay);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.DAILY_DEMAND_LITERS_PER_DAY + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the serviceZone field from JSON (optional field).
     * 
     * @param rootNode the root JSON node
     * @return the parsed ServiceZone enum, or null if not present
     * @throws IllegalArgumentException if the field is invalid
     */
    private ServiceZone parseServiceZone(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.SERVICE_ZONE) || rootNode.get(JsonFields.SERVICE_ZONE).isNull()) {
            return null;
        }
        JsonNode node = rootNode.get(JsonFields.SERVICE_ZONE);
        if (!node.isTextual()) {
            throw new IllegalArgumentException("Field '" + JsonFields.SERVICE_ZONE + "' must be a string");
        }
        String value = node.asText();
        try {
            return ServiceZone.fromString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value '" + value + "' for field '" + JsonFields.SERVICE_ZONE + "': " + e.getMessage(), e);
        }
    }
}
