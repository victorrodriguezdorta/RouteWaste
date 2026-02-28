package es.ull.project.adapter.rest.deserialization.container;

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
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        WasteDemand wasteDemand = parseWasteDemand(rootNode, errors);
        ServiceZone serviceZone = parseServiceZone(rootNode, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        ContainerPostRequestBody requestBody = new ContainerPostRequestBody();
        requestBody.location = location;
        requestBody.wasteType = wasteType;
        requestBody.wasteDemand = wasteDemand;
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
     * Parses the wasteDemand nested object from JSON.
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
        Double value = null;
        QuantityUnit quantityUnit = null;
        TimeUnit timeUnit = null;
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
