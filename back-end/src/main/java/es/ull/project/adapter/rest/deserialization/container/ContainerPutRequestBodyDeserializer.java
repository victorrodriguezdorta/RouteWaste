package es.ull.project.adapter.rest.deserialization.container;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.request.container.ContainerPutRequestBody;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
            WasteDemand wasteDemand = parseWasteDemand(rootNode);
            ServiceZone serviceZone = parseServiceZone(rootNode);
            ContainerPutRequestBody requestBody = new ContainerPutRequestBody();
            requestBody.location = location;
            requestBody.wasteType = wasteType;
            requestBody.wasteDemand = wasteDemand;
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
     * Parses the wasteDemand nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed WasteDemand value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private WasteDemand parseWasteDemand(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.WASTE_DEMAND)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.WASTE_DEMAND + "' is missing");
        }
        JsonNode demandNode = rootNode.get(JsonFields.WASTE_DEMAND);
        if (demandNode.isNull() || !demandNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.WASTE_DEMAND + "' must be a non-null object");
        }
        try {
            if (!demandNode.has(JsonFields.CAPACITY_VALUE)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.CAPACITY_VALUE + "' is missing");
            }
            double value = demandNode.get(JsonFields.CAPACITY_VALUE).asDouble();
            if (!demandNode.has(JsonFields.QUANTITY_UNIT)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.QUANTITY_UNIT + "' is missing");
            }
            String quantityUnitStr = demandNode.get(JsonFields.QUANTITY_UNIT).asText();
            QuantityUnit quantityUnit = new QuantityUnit(quantityUnitStr);
            if (!demandNode.has(JsonFields.TIME_UNIT)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.TIME_UNIT + "' is missing");
            }
            String timeUnitStr = demandNode.get(JsonFields.TIME_UNIT).asText();
            TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr.toUpperCase());
            return new WasteDemand(value, quantityUnit, timeUnit);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.WASTE_DEMAND + "': " + e.getMessage(), e);
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
