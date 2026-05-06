package es.ull.project.adapter.mongodb.mapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps public API field names to MongoDB field paths for InfrastructurePlan queries.
 */
public class InfrastructurePlanFieldMapper {

    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();

    static {
        FIELD_MAPPING.put("id", "_id");
        FIELD_MAPPING.put("executedAt", "executedAt");
        FIELD_MAPPING.put("estimatedTotalCost", "estimatedTotalCost.amount");
        FIELD_MAPPING.put("estimatedTotalCostAmount", "estimatedTotalCost.amount");
        FIELD_MAPPING.put("numberOfDays", "numberOfDays");
        FIELD_MAPPING.put("averagePickupTimeMinutes", "averagePickupTimeMinutes");
    }

    private InfrastructurePlanFieldMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String toMongoField(String publicFieldName) {
        if (publicFieldName == null || publicFieldName.isBlank()) {
            return null;
        }
        return FIELD_MAPPING.get(publicFieldName);
    }

    public static boolean isValidField(String publicFieldName) {
        return publicFieldName != null && !publicFieldName.isBlank() && FIELD_MAPPING.containsKey(publicFieldName);
    }
}