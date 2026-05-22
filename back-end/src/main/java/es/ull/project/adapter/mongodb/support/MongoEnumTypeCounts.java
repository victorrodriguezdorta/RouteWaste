package es.ull.project.adapter.mongodb.support;

import java.util.EnumMap;
import java.util.Map;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Counts MongoDB documents grouped by an enum field.
 */
public final class MongoEnumTypeCounts {

    private static final String UTILITY_CLASS_MESSAGE = "This is a utility class and cannot be instantiated.";

    /**
     * Prevents instantiation of this utility class.
     */
    private MongoEnumTypeCounts() {
        throw new UnsupportedOperationException(UTILITY_CLASS_MESSAGE);
    }

    /**
     * Returns the total document count in a collection.
     *
     * @param mongoTemplate  Mongo template
     * @param entityClass    entity class used for mapping
     * @param collectionName collection name
     * @return total documents
     */
    public static long countAll(MongoTemplate mongoTemplate, Class<?> entityClass, String collectionName) {
        return mongoTemplate.count(new Query(), entityClass, collectionName);
    }

    /**
     * Counts documents per enum value for the given field.
     *
     * @param mongoTemplate  Mongo template
     * @param entityClass    entity class used for mapping
     * @param collectionName collection name
     * @param fieldName      document field holding the enum
     * @param enumClass      enum type
     * @param <E>            enum type parameter
     * @return map with every enum constant and its count (zero when none)
     */
    public static <E extends Enum<E>> Map<E, Long> countByEnumField(
            MongoTemplate mongoTemplate,
            Class<?> entityClass,
            String collectionName,
            String fieldName,
            Class<E> enumClass) {
        Map<E, Long> counts = new EnumMap<>(enumClass);
        for (E enumValue : enumClass.getEnumConstants()) {
            Query query = new Query(Criteria.where(fieldName).is(enumValue));
            counts.put(enumValue, mongoTemplate.count(query, entityClass, collectionName));
        }
        return counts;
    }
}
