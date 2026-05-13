package es.ull.project.adapter.mongodb.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps public API field names to MongoDB field paths.
 * Centralizes field mapping logic for Container queries.
 *
 * <p>This mapper provides a single source of truth for field name conversions,
 * making it easy to add new sortable fields without modifying the controller.
 */
public class ContainerFieldMapper {

    /**
     * MongoDB field name for the container identifier.
     */
    private static final String FIELD_ID = "id";

    /**
     * API and MongoDB field name for the waste type.
     */
    private static final String FIELD_WASTE_TYPE = "wasteType";
    private static final String FIELD_NAME = "name";

    /**
     * API and MongoDB field name for the service zone.
     */
    private static final String FIELD_SERVICE_ZONE = "serviceZone";

    /**
     * API and MongoDB field name for the creation timestamp.
     */
    private static final String FIELD_CREATED_AT = "createdAt";

    /**
     * API and MongoDB field name for the last update timestamp.
     */
    private static final String FIELD_UPDATED_AT = "updatedAt";

    /**
     * API field name for location (short alias).
     */
    private static final String FIELD_LOCATION = "location";

    /**
     * API field name for location postal address.
     */
    private static final String FIELD_POSTAL_ADDRESS = "postalAddress";

    /**
     * API field name for location latitude.
     */
    private static final String FIELD_LATITUDE = "latitude";

    /**
     * API field name for location longitude.
     */
    private static final String FIELD_LONGITUDE = "longitude";

    /**
     * API field name for capacity (short alias).
     */
    private static final String FIELD_CAPACITY = "capacity";

    /**
     * API and MongoDB field name for capacity in liters.
     */
    private static final String FIELD_CAPACITY_LITERS = "capacityLiters";

    /**
     * API field name for daily demand (short alias).
     */
    private static final String FIELD_DEMAND = "demand";

    /**
     * API field name for daily demand (camelCase alias).
     */
    private static final String FIELD_DAILY_DEMAND = "dailyDemand";

    /**
     * MongoDB path for the location postal address nested field.
     */
    private static final String MONGO_LOCATION_POSTAL_ADDRESS = "location.postalAddress";

    /**
     * MongoDB path for the location latitude nested field.
     */
    private static final String MONGO_LOCATION_LATITUDE = "location.latitude";

    /**
     * MongoDB path for the location longitude nested field.
     */
    private static final String MONGO_LOCATION_LONGITUDE = "location.longitude";

    /**
     * MongoDB field name for the daily demand in liters per day.
     */
    private static final String MONGO_DAILY_DEMAND_LITERS_PER_DAY = "dailyDemandLitersPerDay";

    private static final String ERR_UNSUPPORTED_INSTANTIATION = "This is a utility class and cannot be instantiated.";

    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();

    static {
        registerBasicFields();
        registerLocationFields();
        registerCapacityFields();
        registerDemandFields();
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ContainerFieldMapper() {
        throw new UnsupportedOperationException(ERR_UNSUPPORTED_INSTANTIATION);
    }

    /**
     * Registers direct field mappings for basic container attributes into the field mapping.
     */
    private static void registerBasicFields() {
        FIELD_MAPPING.put(FIELD_ID, FIELD_ID);
        FIELD_MAPPING.put(FIELD_NAME, FIELD_NAME);
        FIELD_MAPPING.put(FIELD_WASTE_TYPE, FIELD_WASTE_TYPE);
        FIELD_MAPPING.put(FIELD_SERVICE_ZONE, FIELD_SERVICE_ZONE);
        FIELD_MAPPING.put(FIELD_CREATED_AT, FIELD_CREATED_AT);
        FIELD_MAPPING.put(FIELD_UPDATED_AT, FIELD_UPDATED_AT);
    }

    /**
     * Registers all API aliases that map to nested location MongoDB fields using dot notation.
     */
    private static void registerLocationFields() {
        FIELD_MAPPING.put(FIELD_LOCATION, MONGO_LOCATION_POSTAL_ADDRESS);
        FIELD_MAPPING.put(FIELD_POSTAL_ADDRESS, MONGO_LOCATION_POSTAL_ADDRESS);
        FIELD_MAPPING.put(FIELD_LATITUDE, MONGO_LOCATION_LATITUDE);
        FIELD_MAPPING.put(FIELD_LONGITUDE, MONGO_LOCATION_LONGITUDE);
    }

    /**
     * Registers all API aliases that map to the capacity-in-liters MongoDB field.
     */
    private static void registerCapacityFields() {
        FIELD_MAPPING.put(FIELD_CAPACITY, FIELD_CAPACITY_LITERS);
        FIELD_MAPPING.put(FIELD_CAPACITY_LITERS, FIELD_CAPACITY_LITERS);
    }

    /**
     * Registers all API aliases that map to the daily demand MongoDB field.
     */
    private static void registerDemandFields() {
        FIELD_MAPPING.put(FIELD_DEMAND, MONGO_DAILY_DEMAND_LITERS_PER_DAY);
        FIELD_MAPPING.put(FIELD_DAILY_DEMAND, MONGO_DAILY_DEMAND_LITERS_PER_DAY);
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
