package es.ull.project.adapter.memory;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Counts in-memory entities grouped by an enum attribute.
 */
public final class InMemoryEnumTypeCounts {

    private InMemoryEnumTypeCounts() {
    }

    /**
     * Counts entities per enum value using the given classifier.
     *
     * @param entities   stream of entities to count
     * @param enumClass  enum type
     * @param classifier maps each entity to its enum value
     * @param <T>        entity type
     * @param <E>        enum type
     * @return map with every enum constant and its count (zero when none)
     */
    public static <T, E extends Enum<E>> Map<E, Long> countByEnum(
            Stream<T> entities,
            Class<E> enumClass,
            Function<T, E> classifier) {
        Map<E, Long> counts = new EnumMap<>(enumClass);
        for (E enumValue : enumClass.getEnumConstants()) {
            counts.put(enumValue, 0L);
        }
        entities.forEach(entity -> {
            E key = classifier.apply(entity);
            if (key != null) {
                counts.merge(key, 1L, Long::sum);
            }
        });
        return counts;
    }
}
