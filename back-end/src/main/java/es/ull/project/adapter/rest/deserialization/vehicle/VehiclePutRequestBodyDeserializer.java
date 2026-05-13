package es.ull.project.adapter.rest.deserialization.vehicle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.request.vehicle.VehiclePutRequestBody;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.name.Name;

import java.io.IOException;

/**
 * VehiclePutRequestBodyDeserializer
 * 
 * Custom JSON deserializer for VehiclePutRequestBody.
 * This class converts incoming JSON from PUT requests into VehiclePutRequestBody
 * objects, performing validation and constructing required value objects from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested value objects (VehicleCapacityKilograms, 
 * VehicleCapacityLiters, TransportationVariableCost) by extracting their constituent 
 * parts from the JSON structure.
 */
public class VehiclePutRequestBodyDeserializer extends JsonDeserializer<VehiclePutRequestBody> {

    private static final String CAPACITY_KILOGRAMS_FIELD = "capacityKilograms";
    private static final String KILOGRAMS_FIELD = "Kilograms";
    private static final String CAPACITY_LITERS_FIELD = "CapacityLiters";
    private static final String LITERS_FIELD = "liters";

    private static final String ERR_REQ_CAP_KG_MISSING = "Required field 'capacityKilograms' is missing";
    private static final String ERR_CAP_KG_NON_NULL = "Field 'capacityKilograms' must be a non-null value";
    private static final String ERR_CAP_KG_FORMAT = "Field 'capacityKilograms' must be a number or object with 'Kilograms' field";
    
    private static final String ERR_REQ_CAP_LITERS_MISSING = "Required field 'CapacityLiters' is missing";
    private static final String ERR_CAP_LITERS_NON_NULL = "Field 'CapacityLiters' must be a non-null value";
    private static final String ERR_CAP_LITERS_FORMAT = "Field 'CapacityLiters' must be a number or object with 'liters' field";

    /**
     * Deserializes JSON content into a VehiclePutRequestBody object.
     * 
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized VehiclePutRequestBody
     * @throws IOException if parsing or validation fails
     */
    @Override
    public VehiclePutRequestBody deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        try {
            VehicleType vehicleType = parseVehicleType(rootNode);
            Name name = parseName(rootNode);
            VehicleCapacityKilograms capacityKilograms = parseCapacityKilograms(rootNode);
            VehicleCapacityLiters capacityLiters = parseCapacityLiters(rootNode);
            TransportationVariableCost costPerKilometer = parseCost(rootNode);
            VehiclePutRequestBody requestBody = new VehiclePutRequestBody();
            requestBody.name = name;
            requestBody.vehicleType = vehicleType;
            requestBody.capacityKilograms = capacityKilograms;
            requestBody.capacityLiters = capacityLiters;
            requestBody.costPerKilometer = costPerKilometer;
            return requestBody;
        } catch (Exception e) {
            throw new IOException("Failed to deserialize VehiclePutRequestBody: " + e.getMessage(), e);
        }
    }

    private Name parseName(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.NAME)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.NAME + "' is missing");
        }
        JsonNode node = rootNode.get(JsonFields.NAME);
        if (node.isNull() || !node.isTextual()) {
            throw new IllegalArgumentException("Field '" + JsonFields.NAME + "' must be a non-null string");
        }
        return new Name(node.asText());
    }

    /**
     * Parses the vehicleType field from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed VehicleType enum
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private VehicleType parseVehicleType(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.VEHICLE_TYPE)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.VEHICLE_TYPE + "' is missing");
        }
        JsonNode node = rootNode.get(JsonFields.VEHICLE_TYPE);
        if (node.isNull() || !node.isTextual()) {
            throw new IllegalArgumentException("Field '" + JsonFields.VEHICLE_TYPE + "' must be a non-null string");
        }
        String value = node.asText();
        try {
            return VehicleType.fromString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value '" + value + "' for field '" + JsonFields.VEHICLE_TYPE + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the capacityKilograms field from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed VehicleCapacityKilograms value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private VehicleCapacityKilograms parseCapacityKilograms(JsonNode rootNode) {
        if (!rootNode.has(CAPACITY_KILOGRAMS_FIELD)) {
            throw new IllegalArgumentException(ERR_REQ_CAP_KG_MISSING);
        }
        JsonNode capacityNode = rootNode.get(CAPACITY_KILOGRAMS_FIELD);
        if (capacityNode.isNull()) {
            throw new IllegalArgumentException(ERR_CAP_KG_NON_NULL);
        }
        try {
            Double Kilograms;
            if (capacityNode.isNumber()) {
                Kilograms = capacityNode.asDouble();
            } else if (capacityNode.isObject() && capacityNode.has(KILOGRAMS_FIELD)) {
                Kilograms = capacityNode.get(KILOGRAMS_FIELD).asDouble();
            } else {
                throw new IllegalArgumentException(ERR_CAP_KG_FORMAT);
            }
            return new VehicleCapacityKilograms(Kilograms);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field 'capacityKilograms': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the CapacityLiters field from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed VehicleCapacityLiters value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private VehicleCapacityLiters parseCapacityLiters(JsonNode rootNode) {
        if (!rootNode.has(CAPACITY_LITERS_FIELD)) {
            throw new IllegalArgumentException(ERR_REQ_CAP_LITERS_MISSING);
        }
        JsonNode capacityNode = rootNode.get(CAPACITY_LITERS_FIELD);
        if (capacityNode.isNull()) {
            throw new IllegalArgumentException(ERR_CAP_LITERS_NON_NULL);
        }
        try {
            Double liters;
            if (capacityNode.isNumber()) {
                liters = capacityNode.asDouble();
            } else if (capacityNode.isObject() && capacityNode.has(LITERS_FIELD)) {
                liters = capacityNode.get(LITERS_FIELD).asDouble();
            } else {
                throw new IllegalArgumentException(ERR_CAP_LITERS_FORMAT);
            }
            return new VehicleCapacityLiters(liters);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field 'CapacityLiters': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the costPerKilometer nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed TransportationVariableCost value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private TransportationVariableCost parseCost(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.COST_PER_KILOMETER)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.COST_PER_KILOMETER + "' is missing");
        }
        JsonNode costNode = rootNode.get(JsonFields.COST_PER_KILOMETER);
        if (costNode.isNull() || !costNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.COST_PER_KILOMETER + "' must be a non-null object");
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
                return new TransportationVariableCost(amount);
            } else {
                Currency currency = new Currency(currencyCode);
                return new TransportationVariableCost(amount, currency);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.COST_PER_KILOMETER + "': " + e.getMessage(), e);
        }
    }
}
