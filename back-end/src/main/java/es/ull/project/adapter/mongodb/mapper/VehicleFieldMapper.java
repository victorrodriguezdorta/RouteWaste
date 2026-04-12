package es.ull.project.adapter.mongodb.mapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps public API field names to MongoDB field paths.
 * Centralizes field mapping logic for Vehicle queries.
 * 
 * This mapper provides a single source of truth for field name conversions,
 * making it easy to add new sortable fields without modifying the controller.
 */
public class VehicleFieldMapper {

    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();

    static {
        // Basic fields (direct mapping) - with multiple aliases
        FIELD_MAPPING.put("id", "id");
        FIELD_MAPPING.put("type", "vehicleType");                    // Frontend alias
        FIELD_MAPPING.put("vehicleType", "vehicleType");             // Backend name
        FIELD_MAPPING.put("vehicle_type", "vehicleType");            // Alternative alias

        // Capacity in Kilograms (nested documents) - both field names as aliases
        FIELD_MAPPING.put("capacityKilograms", "capacityKilograms.Kilograms");
        FIELD_MAPPING.put("capacity_kg", "capacityKilograms.Kilograms");
        FIELD_MAPPING.put("kg", "capacityKilograms.Kilograms");
        FIELD_MAPPING.put("kilograms", "capacityKilograms.Kilograms");

        // Capacity in Liters (nested documents) - handle both frontend cases
        FIELD_MAPPING.put("CapacityLiters", "CapacityLiters.liters");       // Exact backend field name
        FIELD_MAPPING.put("capacityLiters", "CapacityLiters.liters");       // Camelcase alias
        FIELD_MAPPING.put("capacity_liters", "CapacityLiters.liters");      // Snake_case alias
        FIELD_MAPPING.put("liters", "CapacityLiters.liters");               // Short alias

        // Cost field (nested document)
        FIELD_MAPPING.put("cost", "costPerKilometer.amount");
        FIELD_MAPPING.put("costPerKilometer", "costPerKilometer.amount");
        FIELD_MAPPING.put("cost_per_km", "costPerKilometer.amount");

        // Metadata fields (if they exist)
        FIELD_MAPPING.put("createdAt", "createdAt");
        FIELD_MAPPING.put("updatedAt", "updatedAt");
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
