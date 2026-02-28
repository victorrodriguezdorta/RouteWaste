package es.ull.project.adapter.rest.deserialization.vehicle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.request.vehicle.VehiclePutRequestBody;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * VehiclePutRequestBodyDeserializer
 * 
 * Custom JSON deserializer for VehiclePutRequestBody.
 * This class converts incoming JSON from PUT requests into VehiclePutRequestBody
 * objects, performing validation and constructing required value objects from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested value objects (Capacity, TransportationVariableCost)
 * by extracting their constituent parts from the JSON structure.
 */
public class VehiclePutRequestBodyDeserializer extends JsonDeserializer<VehiclePutRequestBody> {

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
            Capacity transportCapacity = parseCapacity(rootNode);
            TransportationVariableCost costPerKilometer = parseCost(rootNode);
            VehiclePutRequestBody requestBody = new VehiclePutRequestBody();
            requestBody.vehicleType = vehicleType;
            requestBody.transportCapacity = transportCapacity;
            requestBody.costPerKilometer = costPerKilometer;
            return requestBody;
        } catch (Exception e) {
            throw new IOException("Failed to deserialize VehiclePutRequestBody: " + e.getMessage(), e);
        }
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
     * Parses the transportCapacity nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed Capacity value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private Capacity parseCapacity(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.TRANSPORT_CAPACITY)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.TRANSPORT_CAPACITY + "' is missing");
        }
        JsonNode capacityNode = rootNode.get(JsonFields.TRANSPORT_CAPACITY);
        if (capacityNode.isNull() || !capacityNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.TRANSPORT_CAPACITY + "' must be a non-null object");
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
                "Invalid value for field '" + JsonFields.TRANSPORT_CAPACITY + "': " + e.getMessage(), e);
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
