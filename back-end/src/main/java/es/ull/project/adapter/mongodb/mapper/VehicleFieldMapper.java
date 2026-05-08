package es.ull.project.adapter.mongodb.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps public API field names to MongoDB field paths.
 * Centralizes field mapping logic for Vehicle queries.
 *
 * <p>This mapper provides a single source of truth for field name conversions,
 * making it easy to add new sortable fields without modifying the controller.
 */
public class VehicleFieldMapper {

    /** MongoDB field name for the vehicle identifier. */
    private static final String FIELD_ID = "id";

    /** API field name for vehicle type (frontend alias). */
    private static final String FIELD_TYPE = "type";

    /** API field name for vehicle type (camelCase). */
    private static final String FIELD_VEHICLE_TYPE = "vehicleType";

    /** API field name for vehicle type (snake_case). */
    private static final String FIELD_VEHICLE_TYPE_SNAKE = "vehicle_type";

    /** API field name for capacity in kilograms (camelCase). */
    private static final String FIELD_CAPACITY_KILOGRAMS = "capacityKilograms";

    /** API field name for capacity in kilograms (snake_case). */
    private static final String FIELD_CAPACITY_KG = "capacity_kg";

    /** API field name for capacity in kilograms (short alias). */
    private static final String FIELD_KG = "kg";

    /** API field name for capacity in kilograms (long alias). */
    private static final String FIELD_KILOGRAMS = "kilograms";

    /** API field name for capacity in liters (exact backend name). */
    private static final String FIELD_CAPACITY_LITERS_UPPER = "CapacityLiters";

    /** API field name for capacity in liters (camelCase). */
    private static final String FIELD_CAPACITY_LITERS = "capacityLiters";

    /** API field name for capacity in liters (snake_case). */
    private static final String FIELD_CAPACITY_LITERS_SNAKE = "capacity_liters";

    /** API field name for capacity in liters (short alias). */
    private static final String FIELD_LITERS = "liters";

    /** API field name for cost per kilometer (short alias). */
    private static final String FIELD_COST = "cost";

    /** API field name for cost per kilometer (camelCase). */
    private static final String FIELD_COST_PER_KILOMETER = "costPerKilometer";

    /** API field name for cost per kilometer (snake_case). */
    private static final String FIELD_COST_PER_KM = "cost_per_km";

    /** API field name for the creation timestamp. */
    private static final String FIELD_CREATED_AT = "createdAt";

    /** API field name for the last update timestamp. */
    private static final String FIELD_UPDATED_AT = "updatedAt";

    /** MongoDB path for the vehicle type field. */
    private static final String MONGO_VEHICLE_TYPE = "vehicleType";

    /** MongoDB path for the capacity in kilograms nested field. */
    private static final String MONGO_CAPACITY_KILOGRAMS = "capacityKilograms.Kilograms";

    /** MongoDB path for the capacity in liters nested field. */
    private static final String MONGO_CAPACITY_LITERS = "CapacityLiters.liters";

    /** MongoDB path for the cost per kilometer nested field. */
    private static final String MONGO_COST_PER_KILOMETER = "costPerKilometer.amount";

    /** MongoDB path for the creation timestamp field. */
    private static final String MONGO_CREATED_AT = "createdAt";

    /** MongoDB path for the last update timestamp field. */
    private static final String MONGO_UPDATED_AT = "updatedAt";

    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();

    static {
        registerBasicFields();
        registerCapacityKilogramFields();
        registerCapacityLiterFields();
        registerCostFields();
        registerMetadataFields();
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private VehicleFieldMapper() {
    }

    /**
     * Registers direct field mappings and vehicle type aliases into the field mapping.
     */
    private static void registerBasicFields() {
        FIELD_MAPPING.put(FIELD_ID, FIELD_ID);
        FIELD_MAPPING.put(FIELD_TYPE, MONGO_VEHICLE_TYPE);
        FIELD_MAPPING.put(FIELD_VEHICLE_TYPE, MONGO_VEHICLE_TYPE);
        FIELD_MAPPING.put(FIELD_VEHICLE_TYPE_SNAKE, MONGO_VEHICLE_TYPE);
    }

    /**
     * Registers all API aliases that map to the nested capacity-in-kilograms MongoDB field.
     */
    private static void registerCapacityKilogramFields() {
        FIELD_MAPPING.put(FIELD_CAPACITY_KILOGRAMS, MONGO_CAPACITY_KILOGRAMS);
        FIELD_MAPPING.put(FIELD_CAPACITY_KG, MONGO_CAPACITY_KILOGRAMS);
        FIELD_MAPPING.put(FIELD_KG, MONGO_CAPACITY_KILOGRAMS);
        FIELD_MAPPING.put(FIELD_KILOGRAMS, MONGO_CAPACITY_KILOGRAMS);
    }

    /**
     * Registers all API aliases that map to the nested capacity-in-liters MongoDB field.
     */
    private static void registerCapacityLiterFields() {
        FIELD_MAPPING.put(FIELD_CAPACITY_LITERS_UPPER, MONGO_CAPACITY_LITERS);
        FIELD_MAPPING.put(FIELD_CAPACITY_LITERS, MONGO_CAPACITY_LITERS);
        FIELD_MAPPING.put(FIELD_CAPACITY_LITERS_SNAKE, MONGO_CAPACITY_LITERS);
        FIELD_MAPPING.put(FIELD_LITERS, MONGO_CAPACITY_LITERS);
    }

    /**
     * Registers all API aliases that map to the nested cost-per-kilometer MongoDB field.
     */
    private static void registerCostFields() {
        FIELD_MAPPING.put(FIELD_COST, MONGO_COST_PER_KILOMETER);
        FIELD_MAPPING.put(FIELD_COST_PER_KILOMETER, MONGO_COST_PER_KILOMETER);
        FIELD_MAPPING.put(FIELD_COST_PER_KM, MONGO_COST_PER_KILOMETER);
    }

    /**
     * Registers metadata timestamp fields into the field mapping.
     */
    private static void registerMetadataFields() {
        FIELD_MAPPING.put(FIELD_CREATED_AT, MONGO_CREATED_AT);
        FIELD_MAPPING.put(FIELD_UPDATED_AT, MONGO_UPDATED_AT);
    }

    /**
     * Converts a public API field name to its MongoDB field path.
     *
     * @param publicFieldName the field name used in the API request
     * @return the MongoDB field path, or null if field is not recognized
     */
    public static String toMongoField(String publicFieldName) {
        if (publicFieldName == null || publicFieldName.isBlank()) {
            return null;
        }
        return FIELD_MAPPING.get(publicFieldName);
    }

    /**
     * Checks if a field name is valid and mappable.
     *
     * @param publicFieldName the field name to validate
     * @return true if the field can be mapped, false otherwise
     */
    public static boolean isValidField(String publicFieldName) {
        return publicFieldName != null && !publicFieldName.isBlank()
               && FIELD_MAPPING.containsKey(publicFieldName);
    }

    /**
     * Gets all available sortable field names.
     *
     * @return a list of field names that can be used for sorting
     */
    public static List<String> getAvailableFields() {
        return new ArrayList<>(FIELD_MAPPING.keySet());
    }
}
