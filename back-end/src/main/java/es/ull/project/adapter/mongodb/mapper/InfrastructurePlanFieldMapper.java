package es.ull.project.adapter.mongodb.mapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps public API field names to MongoDB field paths for InfrastructurePlan queries.
 */
public class InfrastructurePlanFieldMapper {

    private static final String UTILITY_CLASS_ERROR = "This is a utility class and cannot be instantiated";
    private static final String FIELD_ID = "id";
    private static final String MONGO_FIELD_ID = "_id";
    private static final String FIELD_EXECUTED_AT = "executedAt";
    private static final String FIELD_ESTIMATED_TOTAL_COST = "estimatedTotalCost";
    private static final String FIELD_ESTIMATED_TOTAL_COST_AMOUNT = "estimatedTotalCostAmount";
    private static final String MONGO_FIELD_ESTIMATED_TOTAL_COST_AMOUNT = "estimatedTotalCost.amount";
    private static final String FIELD_NUMBER_OF_DAYS = "numberOfDays";
    private static final String FIELD_AVERAGE_PICKUP_TIME_MINUTES = "averagePickupTimeMinutes";

    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();

    static {
        FIELD_MAPPING.put(FIELD_ID, MONGO_FIELD_ID);
        FIELD_MAPPING.put(FIELD_EXECUTED_AT, FIELD_EXECUTED_AT);
        FIELD_MAPPING.put(FIELD_ESTIMATED_TOTAL_COST, MONGO_FIELD_ESTIMATED_TOTAL_COST_AMOUNT);
        FIELD_MAPPING.put(FIELD_ESTIMATED_TOTAL_COST_AMOUNT, MONGO_FIELD_ESTIMATED_TOTAL_COST_AMOUNT);
        FIELD_MAPPING.put(FIELD_NUMBER_OF_DAYS, FIELD_NUMBER_OF_DAYS);
        FIELD_MAPPING.put(FIELD_AVERAGE_PICKUP_TIME_MINUTES, FIELD_AVERAGE_PICKUP_TIME_MINUTES);
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private InfrastructurePlanFieldMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR);
    }

    /**
     * Maps a public API field name to its corresponding MongoDB field path.
     *
     * @param publicFieldName the field name used in the public API
     * @return the MongoDB field path, or {@code null} if the field name is blank or not mapped
     */
    public static String toMongoField(String publicFieldName) {
        if (publicFieldName == null || publicFieldName.isBlank()) {
            return null;
        }
        return FIELD_MAPPING.get(publicFieldName);
    }

    /**
     * Checks whether a given public API field name has a valid MongoDB mapping.
     *
     * @param publicFieldName the field name used in the public API
     * @return {@code true} if the field name is non-blank and has a known mapping
     */
    public static boolean isValidField(String publicFieldName) {
        return publicFieldName != null && !publicFieldName.isBlank() && FIELD_MAPPING.containsKey(publicFieldName);
    }
}