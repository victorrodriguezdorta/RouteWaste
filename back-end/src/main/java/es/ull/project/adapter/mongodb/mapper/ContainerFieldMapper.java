package es.ull.project.adapter.mongodb.mapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps public API field names to MongoDB field paths.
 * Centralizes field mapping logic for Container queries.
 * 
 * This mapper provides a single source of truth for field name conversions,
 * making it easy to add new sortable fields without modifying the controller.
 */
public class ContainerFieldMapper {

    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();

    static {
        // Basic fields (direct mapping)
        FIELD_MAPPING.put("id", "id");
        FIELD_MAPPING.put("wasteType", "wasteType");
        FIELD_MAPPING.put("serviceZone", "serviceZone");
        FIELD_MAPPING.put("createdAt", "createdAt");
        FIELD_MAPPING.put("updatedAt", "updatedAt");

        // Nested fields (MongoDB dot notation)
        FIELD_MAPPING.put("location", "location.postalAddress");
        FIELD_MAPPING.put("postalAddress", "location.postalAddress");
        FIELD_MAPPING.put("latitude", "location.latitude");
        FIELD_MAPPING.put("longitude", "location.longitude");

        // Capacity and Demand fields (simple numeric fields in MongoDB)
        FIELD_MAPPING.put("capacity", "capacityLiters");
        FIELD_MAPPING.put("capacityLiters", "capacityLiters");

        FIELD_MAPPING.put("demand", "dailyDemandLitersPerDay");
        FIELD_MAPPING.put("dailyDemand", "dailyDemandLitersPerDay");
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
    public static java.util.List<String> getAvailableFields() {
        return new java.util.ArrayList<>(FIELD_MAPPING.keySet());
    }
}
