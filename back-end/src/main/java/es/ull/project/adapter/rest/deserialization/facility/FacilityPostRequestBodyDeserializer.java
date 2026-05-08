package es.ull.project.adapter.rest.deserialization.facility;

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
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.location.Location;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        FacilityType facilityType = parseFacilityType(rootNode, errors);
        Location location = parseLocation(rootNode, errors);
        StorageCapacityKilograms storageCapacity = parseStorageCapacity(rootNode, errors);
        ProcessingCapacityKilogramsPerDay processingCapacity = parseProcessingCapacity(rootNode, errors);
        UnloadingTime unloadingTime = parseUnloadingTime(rootNode, errors);
        OpeningFixedCost openingFixedCost = parseOpeningFixedCost(rootNode, errors);
        FacilityStatus status = parseStatus(rootNode, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        FacilityPostRequestBody requestBody = new FacilityPostRequestBody();
        requestBody.facilityType = facilityType;
        requestBody.location = location;
        requestBody.storageCapacity = storageCapacity;
        requestBody.processingCapacity = processingCapacity;
        requestBody.unloadingTime = unloadingTime;
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
     * Parses the storage capacity nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed StorageCapacityKilograms value object, or null if validation fails
     */
    private StorageCapacityKilograms parseStorageCapacity(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.STORAGE_CAPACITY)) {
            errors.add(new FieldError(JsonFields.STORAGE_CAPACITY, "Field is required"));
            return null;
        }
        JsonNode capacityNode = rootNode.get(JsonFields.STORAGE_CAPACITY);
        if (capacityNode.isNull() || !capacityNode.isObject()) {
            errors.add(new FieldError(JsonFields.STORAGE_CAPACITY, "Must be a non-null object"));
            return null;
        }
        Double value = null;
        if (!capacityNode.has(JsonFields.CAPACITY_VALUE)) {
            errors.add(new FieldError(
                JsonFields.STORAGE_CAPACITY + "." + JsonFields.CAPACITY_VALUE,
                "Field is required"
            ));
        } else {
            try {
                value = capacityNode.get(JsonFields.CAPACITY_VALUE).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.STORAGE_CAPACITY + "." + JsonFields.CAPACITY_VALUE,
                    "Must be a valid number"
                ));
            }
        }
        if (value != null) {
            try {
                return new StorageCapacityKilograms(value);
            } catch (Exception e) {
                errors.add(new FieldError(JsonFields.STORAGE_CAPACITY, "Invalid storage capacity: " + e.getMessage()));
                return null;
            }
        }
        return null;
    }

    /**
     * Parses the processing capacity nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed ProcessingCapacityKilogramsPerDay value object, or null if validation fails
     */
    private ProcessingCapacityKilogramsPerDay parseProcessingCapacity(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.PROCESSING_CAPACITY)) {
            errors.add(new FieldError(JsonFields.PROCESSING_CAPACITY, "Field is required"));
            return null;
        }
        JsonNode capacityNode = rootNode.get(JsonFields.PROCESSING_CAPACITY);
        if (capacityNode.isNull() || !capacityNode.isObject()) {
            errors.add(new FieldError(JsonFields.PROCESSING_CAPACITY, "Must be a non-null object"));
            return null;
        }
        Double value = null;
        if (!capacityNode.has(JsonFields.CAPACITY_VALUE)) {
            errors.add(new FieldError(
                JsonFields.PROCESSING_CAPACITY + "." + JsonFields.CAPACITY_VALUE,
                "Field is required"
            ));
        } else {
            try {
                value = capacityNode.get(JsonFields.CAPACITY_VALUE).asDouble();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.PROCESSING_CAPACITY + "." + JsonFields.CAPACITY_VALUE,
                    "Must be a valid number"
                ));
            }
        }
        if (value != null) {
            try {
                return new ProcessingCapacityKilogramsPerDay(value);
            } catch (Exception e) {
                errors.add(new FieldError(JsonFields.PROCESSING_CAPACITY, "Invalid processing capacity: " + e.getMessage()));
                return null;
            }
        }
        return null;
    }

    /**
     * Parses the unloading time nested object from JSON.
     * 
     * @param rootNode the root JSON node
     * @param errors list to accumulate validation errors
     * @return the parsed UnloadingTime value object, or null if validation fails
     */
    private UnloadingTime parseUnloadingTime(JsonNode rootNode, List<FieldError> errors) {
        if (!rootNode.has(JsonFields.UNLOADING_TIME)) {
            errors.add(new FieldError(JsonFields.UNLOADING_TIME, "Field is required"));
            return null;
        }
        JsonNode timeNode = rootNode.get(JsonFields.UNLOADING_TIME);
        if (timeNode.isNull() || !timeNode.isObject()) {
            errors.add(new FieldError(JsonFields.UNLOADING_TIME, "Must be a non-null object"));
            return null;
        }
        Integer value = null;
        if (!timeNode.has(JsonFields.TIME_VALUE)) {
            errors.add(new FieldError(
                JsonFields.UNLOADING_TIME + "." + JsonFields.TIME_VALUE,
                "Field is required"
            ));
        } else {
            try {
                value = timeNode.get(JsonFields.TIME_VALUE).asInt();
            } catch (Exception e) {
                errors.add(new FieldError(
                    JsonFields.UNLOADING_TIME + "." + JsonFields.TIME_VALUE,
                    "Must be a valid integer"
                ));
            }
        }
        if (value != null) {
            try {
                return new UnloadingTime(value);
            } catch (Exception e) {
                errors.add(new FieldError(JsonFields.UNLOADING_TIME, "Invalid unloading time: " + e.getMessage()));
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
