package es.ull.project.adapter.rest.deserialization.vehicle;

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
import es.ull.project.adapter.rest.request.vehicle.VehiclePostRequestBody;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;

/**
 * VehiclePostRequestBodyDeserializer
 * 
 * Custom JSON deserializer for VehiclePostRequestBody.
 * This class converts incoming JSON from POST requests into VehiclePostRequestBody
 * objects, performing validation and constructing required value objects from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested value objects (VehicleCapacityKilograms, 
 * VehicleCapacityLiters, TransportationVariableCost) by extracting their constituent 
 * parts from the JSON structure.
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
        VehicleCapacityKilograms capacityKilograms = parseCapacityKilograms(rootNode, errors);
        VehicleCapacityLiters capacityLiters = parseCapacityLiters(rootNode, errors);
        TransportationVariableCost costPerKilometer = parseCost(rootNode, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        VehiclePostRequestBody requestBody = new VehiclePostRequestBody();
        requestBody.vehicleType = vehicleType;
        requestBody.capacityKilograms = capacityKilograms;
        requestBody.capacityLiters = capacityLiters;
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
     * Parses the capacityKilograms nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed VehicleCapacityKilograms value object, or null if validation fails
     */
    private VehicleCapacityKilograms parseCapacityKilograms(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has("capacityKilograms")) {
            errors.add(new FieldError("capacityKilograms", "Field is required"));
            return null;
        }
        JsonNode capacityNode = rootNode.get("capacityKilograms");
        if (capacityNode.isNull()) {
            errors.add(new FieldError("capacityKilograms", "Must be a non-null value"));
            return null;
        }
        Double Kilograms = null;
        if (capacityNode.isNumber()) {
            Kilograms = capacityNode.asDouble();
        } else if (capacityNode.isObject() && capacityNode.has("Kilograms")) {
            try {
                Kilograms = capacityNode.get("Kilograms").asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    "capacityKilograms.Kilograms",
                    "Must be a valid number"
                ));
            }
        } else {
            errors.add(new FieldError("capacityKilograms", "Must be a number or object with 'Kilograms' field"));
        }
        if (Kilograms != null) {
            try {
                return new VehicleCapacityKilograms(Kilograms);
            } catch (Exception e) {
                errors.add(new FieldError("capacityKilograms", "Invalid capacity: " + e.getMessage()));
                return null;
            }
        }
        return null;
    }

    /**
     * Parses the CapacityLiters nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed VehicleCapacityLiters value object, or null if validation fails
     */
    private VehicleCapacityLiters parseCapacityLiters(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has("CapacityLiters")) {
            errors.add(new FieldError("CapacityLiters", "Field is required"));
            return null;
        }
        JsonNode capacityNode = rootNode.get("CapacityLiters");
        if (capacityNode.isNull()) {
            errors.add(new FieldError("CapacityLiters", "Must be a non-null value"));
            return null;
        }
        Double liters = null;
        if (capacityNode.isNumber()) {
            liters = capacityNode.asDouble();
        } else if (capacityNode.isObject() && capacityNode.has("liters")) {
            try {
                liters = capacityNode.get("liters").asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    "CapacityLiters.liters",
                    "Must be a valid number"
                ));
            }
        } else {
            errors.add(new FieldError("CapacityLiters", "Must be a number or object with 'liters' field"));
        }
        if (liters != null) {
            try {
                return new VehicleCapacityLiters(liters);
            } catch (Exception e) {
                errors.add(new FieldError("CapacityLiters", "Invalid capacity: " + e.getMessage()));
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
