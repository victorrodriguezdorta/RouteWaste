package es.ull.project.adapter.rest.deserialization.vehicle;

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
import es.ull.project.adapter.rest.request.vehicle.VehiclePostRequestBody;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;

/**
 * VehiclePostRequestBodyDeserializer
 * 
 * Custom JSON deserializer for VehiclePostRequestBody.
 * This class converts incoming JSON from POST requests into VehiclePostRequestBody
 * objects, performing validation and constructing required value objects from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested value objects (Capacity, TransportationVariableCost)
 * by extracting their constituent parts from the JSON structure.
 */
public class VehiclePostRequestBodyDeserializer extends JsonDeserializer<VehiclePostRequestBody> {

    /**
     * Deserializes JSON content into a VehiclePostRequestBody object.
     * 
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized VehiclePostRequestBody
     * @throws IOException if parsing or validation fails
     */
    @Override
    public VehiclePostRequestBody deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        List<FieldError> errors = new ArrayList<>();
        VehicleType vehicleType = parseVehicleType(rootNode, errors);
        Capacity transportCapacity = parseCapacity(rootNode, errors);
        TransportationVariableCost costPerKilometer = parseCost(rootNode, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        VehiclePostRequestBody requestBody = new VehiclePostRequestBody();
        requestBody.vehicleType = vehicleType;
        requestBody.transportCapacity = transportCapacity;
        requestBody.costPerKilometer = costPerKilometer;
        return requestBody;
    }

    /**
     * Parses the vehicleType field from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed VehicleType enum, or null if validation fails
     */
    private VehicleType parseVehicleType(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.VEHICLE_TYPE)) {
            errors.add(new FieldError(JsonFields.VEHICLE_TYPE, "Field is required"));
            return null;
        }
        JsonNode node = rootNode.get(JsonFields.VEHICLE_TYPE);
        if (node.isNull() || !node.isTextual()) {
            errors.add(new FieldError(JsonFields.VEHICLE_TYPE, "Must be a non-null string"));
            return null;
        }
        String value = node.asText();
        try {
            return VehicleType.fromString(value);
        } catch (Exception e) {
            errors.add(new FieldError(JsonFields.VEHICLE_TYPE, "Invalid value '" + value + "'"));
            return null;
        }
    }

    /**
     * Parses the transportCapacity nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed Capacity value object, or null if validation fails
     */
    private Capacity parseCapacity(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.TRANSPORT_CAPACITY)) {
            errors.add(new FieldError(JsonFields.TRANSPORT_CAPACITY, "Field is required"));
            return null;
        }
        JsonNode capacityNode = rootNode.get(JsonFields.TRANSPORT_CAPACITY);
        if (capacityNode.isNull() || !capacityNode.isObject()) {
            errors.add(new FieldError(JsonFields.TRANSPORT_CAPACITY, "Must be a non-null object"));
            return null;
        }
        Double value = null;
        QuantityUnit quantityUnit = null;
        TimeUnit timeUnit = null;
        if (!capacityNode.has(JsonFields.CAPACITY_VALUE)) {
            errors.add(new FieldError(
                JsonFields.TRANSPORT_CAPACITY + "." + JsonFields.CAPACITY_VALUE, 
                "Field is required"
            ));
        } else {
            try {
                value = capacityNode.get(JsonFields.CAPACITY_VALUE).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.TRANSPORT_CAPACITY + "." + JsonFields.CAPACITY_VALUE,
                    "Must be a valid number"
                ));
            }
        }
        if (!capacityNode.has(JsonFields.QUANTITY_UNIT)) {
            errors.add(new FieldError(
                JsonFields.TRANSPORT_CAPACITY + "." + JsonFields.QUANTITY_UNIT,
                "Field is required"
            ));
        } else {
            try {
                String quantityUnitStr = capacityNode.get(JsonFields.QUANTITY_UNIT).asText();
                quantityUnit = new QuantityUnit(quantityUnitStr);
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.TRANSPORT_CAPACITY + "." + JsonFields.QUANTITY_UNIT,
                    "Invalid value"
                ));
            }
        }
        if (!capacityNode.has(JsonFields.TIME_UNIT)) {
            errors.add(new FieldError(
                JsonFields.TRANSPORT_CAPACITY + "." + JsonFields.TIME_UNIT,
                "Field is required"
            ));
        } else {
            try {
                String timeUnitStr = capacityNode.get(JsonFields.TIME_UNIT).asText();
                timeUnit = TimeUnit.valueOf(timeUnitStr.toUpperCase());
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.TRANSPORT_CAPACITY + "." + JsonFields.TIME_UNIT,
                    "Invalid time unit"
                ));
            }
        }
        if (value != null && quantityUnit != null && timeUnit != null) {
            try {
                return new Capacity(value, quantityUnit, timeUnit);
            } catch (Exception e) {
                errors.add(new FieldError(JsonFields.TRANSPORT_CAPACITY, "Invalid capacity: " + e.getMessage()));
                return null;
            }
        }
        return null;
    }

    /**
     * Parses the costPerKilometer nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed TransportationVariableCost value object, or null if validation fails
     */
    private TransportationVariableCost parseCost(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.COST_PER_KILOMETER)) {
            errors.add(new FieldError(JsonFields.COST_PER_KILOMETER, "Field is required"));
            return null;
        }
        JsonNode costNode = rootNode.get(JsonFields.COST_PER_KILOMETER);
        if (costNode.isNull() || !costNode.isObject()) {
            errors.add(new FieldError(JsonFields.COST_PER_KILOMETER, "Must be a non-null object"));
            return null;
        }
        Double amount = null;
        if (!costNode.has(JsonFields.AMOUNT)) {
            errors.add(new FieldError(
                JsonFields.COST_PER_KILOMETER + "." + JsonFields.AMOUNT,
                "Field is required"
            ));
        } else {
            try {
                amount = costNode.get(JsonFields.AMOUNT).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.COST_PER_KILOMETER + "." + JsonFields.AMOUNT,
                    "Must be a valid number"
                ));
            }
        }
        Currency currency = null;
        if (costNode.has(JsonFields.CURRENCY) && !costNode.get(JsonFields.CURRENCY).isNull()) {
            try {
                String currencyCode = costNode.get(JsonFields.CURRENCY).asText();
                if (currencyCode != null && !currencyCode.trim().isEmpty()) {
                    currency = new Currency(currencyCode);
                }
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.COST_PER_KILOMETER + "." + JsonFields.CURRENCY,
                    "Invalid currency code"
                ));
            }
        }
        if (amount != null) {
            try {
                if (currency == null) {
                    return new TransportationVariableCost(amount);
                } else {
                    return new TransportationVariableCost(amount, currency);
                }
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.COST_PER_KILOMETER,
                    "Invalid cost: " + e.getMessage()
                ));
                return null;
            }
        }
        return null;
    }
}
