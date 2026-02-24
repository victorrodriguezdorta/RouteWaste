package es.ull.project.adapter.rest.deserialization.facility;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.request.facility.FacilityPutRequestBody;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.location.Location;

/**
 * FacilityPutRequestBodyDeserializer
 * 
 * Custom JSON deserializer for FacilityPutRequestBody.
 * This class converts incoming JSON from PUT requests into FacilityPutRequestBody
 * objects, performing validation and constructing required value objects from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested value objects (Location, Capacity, OpeningFixedCost)
 * by extracting their constituent parts from the JSON structure.
 */
public class FacilityPutRequestBodyDeserializer extends JsonDeserializer<FacilityPutRequestBody> {

    /**
     * Deserializes JSON content into a FacilityPutRequestBody object.
     * 
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized FacilityPutRequestBody
     * @throws IOException if parsing or validation fails
     */
    @Override
    public FacilityPutRequestBody deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        try {
            FacilityType facilityType = parseFacilityType(rootNode);
            Location location = parseLocation(rootNode);
            Capacity capacity = parseCapacity(rootNode);
            OpeningFixedCost openingFixedCost = parseOpeningFixedCost(rootNode);
            FacilityStatus status = parseStatus(rootNode);
            FacilityPutRequestBody requestBody = new FacilityPutRequestBody();
            requestBody.facilityType = facilityType;
            requestBody.location = location;
            requestBody.capacity = capacity;
            requestBody.openingFixedCost = openingFixedCost;
            requestBody.status = status;
            return requestBody;
        } catch (Exception e) {
            throw new IOException("Failed to deserialize FacilityPutRequestBody: " + e.getMessage(), e);
        }
    }

    /**
     * Parses the facilityType field from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed FacilityType enum
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private FacilityType parseFacilityType(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.FACILITY_TYPE)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.FACILITY_TYPE + "' is missing");
        }
        JsonNode node = rootNode.get(JsonFields.FACILITY_TYPE);
        if (node.isNull() || !node.isTextual()) {
            throw new IllegalArgumentException("Field '" + JsonFields.FACILITY_TYPE + "' must be a non-null string");
        }
        String value = node.asText();
        try {
            return FacilityType.fromString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value '" + value + "' for field '" + JsonFields.FACILITY_TYPE + "': " + e.getMessage(), e);
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
     * Parses the capacity nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed Capacity value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private Capacity parseCapacity(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.CAPACITY)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.CAPACITY + "' is missing");
        }
        JsonNode capacityNode = rootNode.get(JsonFields.CAPACITY);
        if (capacityNode.isNull() || !capacityNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.CAPACITY + "' must be a non-null object");
        }
        try {
            if (!capacityNode.has(JsonFields.CAPACITY_VALUE)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.CAPACITY_VALUE + "' is missing");
            }
            double value = capacityNode.get(JsonFields.CAPACITY_VALUE).asDouble();
            if (!capacityNode.has(JsonFields.QUANTITY_UNIT)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.QUANTITY_UNIT + "' is missing");
            }
            String quantityUnitStr = capacityNode.get(JsonFields.QUANTITY_UNIT).asText();
            QuantityUnit quantityUnit = new QuantityUnit(quantityUnitStr);
            if (!capacityNode.has(JsonFields.TIME_UNIT)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.TIME_UNIT + "' is missing");
            }
            String timeUnitStr = capacityNode.get(JsonFields.TIME_UNIT).asText();
            TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr.toUpperCase());
            return new Capacity(value, quantityUnit, timeUnit);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.CAPACITY + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the openingFixedCost nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed OpeningFixedCost value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private OpeningFixedCost parseOpeningFixedCost(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.OPENING_FIXED_COST)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.OPENING_FIXED_COST + "' is missing");
        }
        JsonNode costNode = rootNode.get(JsonFields.OPENING_FIXED_COST);
        if (costNode.isNull() || !costNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.OPENING_FIXED_COST + "' must be a non-null object");
        }
        try {
            if (!costNode.has(JsonFields.AMOUNT)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.AMOUNT + "' is missing");
            }
            double amount = costNode.get(JsonFields.AMOUNT).asDouble();
            String currencyCode = null;
            if (costNode.has(JsonFields.CURRENCY) && !costNode.get(JsonFields.CURRENCY).isNull()) {
                currencyCode = costNode.get(JsonFields.CURRENCY).asText();
            }
            if (currencyCode == null || currencyCode.trim().isEmpty()) {
                return new OpeningFixedCost(amount);
            } else {
                Currency currency = new Currency(currencyCode);
                return new OpeningFixedCost(amount, currency);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.OPENING_FIXED_COST + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the status field from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed FacilityStatus enum
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private FacilityStatus parseStatus(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.STATUS)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.STATUS + "' is missing");
        }
        JsonNode node = rootNode.get(JsonFields.STATUS);
        if (node.isNull() || !node.isTextual()) {
            throw new IllegalArgumentException("Field '" + JsonFields.STATUS + "' must be a non-null string");
        }
        String value = node.asText();
        try {
            return FacilityStatus.fromString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value '" + value + "' for field '" + JsonFields.STATUS + "': " + e.getMessage(), e);
        }
    }
}
