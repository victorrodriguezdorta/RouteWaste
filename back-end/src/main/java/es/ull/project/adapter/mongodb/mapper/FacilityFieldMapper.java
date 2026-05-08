package es.ull.project.adapter.mongodb.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps public API field names to MongoDB field paths.
 * Centralizes field mapping logic for Facility queries.
 *
 * <p>This mapper provides a single source of truth for field name conversions,
 * making it easy to add new sortable fields without modifying the controller.
 */
public class FacilityFieldMapper {

    /** MongoDB field name for the facility identifier. */
    private static final String FIELD_ID = "id";

    /** API field name for facility type (short alias). */
    private static final String FIELD_TYPE = "type";

    /** API and MongoDB field name for the facility type. */
    private static final String FIELD_FACILITY_TYPE = "facilityType";

    /** API and MongoDB field name for the facility status. */
    private static final String FIELD_STATUS = "status";

    /** API and MongoDB field name for the creation timestamp. */
    private static final String FIELD_CREATED_AT = "createdAt";

    /** API and MongoDB field name for the last update timestamp. */
    private static final String FIELD_UPDATED_AT = "updatedAt";

    /** API field name for location (short alias). */
    private static final String FIELD_LOCATION = "location";

    /** API field name for location postal address. */
    private static final String FIELD_POSTAL_ADDRESS = "postalAddress";

    /** API field name for location latitude. */
    private static final String FIELD_LATITUDE = "latitude";

    /** API field name for location longitude. */
    private static final String FIELD_LONGITUDE = "longitude";

    /** API and MongoDB field name for the storage capacity. */
    private static final String FIELD_STORAGE_CAPACITY = "storageCapacity";

    /** API and MongoDB field name for the processing capacity. */
    private static final String FIELD_PROCESSING_CAPACITY = "processingCapacity";

    /** API and MongoDB field name for the unloading time. */
    private static final String FIELD_UNLOADING_TIME = "unloadingTime";

    /** API field name for the opening fixed cost (short alias). */
    private static final String FIELD_OPENING_COST = "openingCost";

    /** API and MongoDB field name for the current filling level. */
    private static final String FIELD_CURRENT_FILLING_LEVEL = "currentFillingLevel";

    /** MongoDB path for the location postal address nested field. */
    private static final String MONGO_LOCATION_POSTAL_ADDRESS = "location.postalAddress";

    /** MongoDB path for the location latitude nested field. */
    private static final String MONGO_LOCATION_LATITUDE = "location.latitude";

    /** MongoDB path for the location longitude nested field. */
    private static final String MONGO_LOCATION_LONGITUDE = "location.longitude";

    /** MongoDB field name for the opening fixed cost. */
    private static final String MONGO_OPENING_FIXED_COST = "openingFixedCost";

    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();

    static {
        registerBasicFields();
        registerLocationFields();
        registerCapacityFields();
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private FacilityFieldMapper() {
    }

    /**
     * Registers direct field mappings for basic facility attributes into the field mapping.
     */
    private static void registerBasicFields() {
        FIELD_MAPPING.put(FIELD_ID, FIELD_ID);
        FIELD_MAPPING.put(FIELD_TYPE, FIELD_FACILITY_TYPE);
        FIELD_MAPPING.put(FIELD_FACILITY_TYPE, FIELD_FACILITY_TYPE);
        FIELD_MAPPING.put(FIELD_STATUS, FIELD_STATUS);
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
     * Registers capacity and operational field mappings for the facility into the field mapping.
     */
    private static void registerCapacityFields() {
        FIELD_MAPPING.put(FIELD_STORAGE_CAPACITY, FIELD_STORAGE_CAPACITY);
        FIELD_MAPPING.put(FIELD_PROCESSING_CAPACITY, FIELD_PROCESSING_CAPACITY);
        FIELD_MAPPING.put(FIELD_UNLOADING_TIME, FIELD_UNLOADING_TIME);
        FIELD_MAPPING.put(FIELD_OPENING_COST, MONGO_OPENING_FIXED_COST);
        FIELD_MAPPING.put(FIELD_CURRENT_FILLING_LEVEL, FIELD_CURRENT_FILLING_LEVEL);
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
