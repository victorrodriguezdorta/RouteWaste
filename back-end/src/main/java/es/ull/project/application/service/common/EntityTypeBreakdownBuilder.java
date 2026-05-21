package es.ull.project.application.service.common;

import es.ull.project.domain.readmodel.EntityTypeBreakdown;
import es.ull.project.domain.valueobject.page.TotalElements;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Builds {@link EntityTypeBreakdown} from repository count maps keyed by enum constants.
 */
public final class EntityTypeBreakdownBuilder {

    private EntityTypeBreakdownBuilder() {
    }

    /**
     * Creates a breakdown with one entry per enum constant (zero when absent from the map).
     *
     * @param total      total entity count
     * @param counts     counts keyed by enum constant
     * @param enumValues all enum constants for the entity type dimension
     * @param typeName   function mapping enum constant to its serialized name
     * @param <E>        enum type
     * @return domain breakdown
     */
    public static <E extends Enum<E>> EntityTypeBreakdown fromCounts(
            long total,
            Map<E, Long> counts,
            E[] enumValues,
            Function<E, String> typeName) {
        List<EntityTypeBreakdown.TypeCount> byType = new ArrayList<>(enumValues.length);
        for (E enumValue : enumValues) {
            long count = counts.getOrDefault(enumValue, 0L);
            byType.add(new EntityTypeBreakdown.TypeCount(
                    typeName.apply(enumValue),
                    new TotalElements(count)));
        }
        return new EntityTypeBreakdown(new TotalElements(total), byType);
    }

    /**
     * Creates a breakdown for the given enum class using {@link Enum#name()} as type key.
     *
     * @param total      total entity count
     * @param counts     counts keyed by enum constant
     * @param enumClass  enum class defining all type values
     * @param <E>        enum type
     * @return domain breakdown
     */
    public static <E extends Enum<E>> EntityTypeBreakdown fromCounts(
            long total,
            Map<E, Long> counts,
            Class<E> enumClass) {
        E[] enumValues = enumClass.getEnumConstants();
        return fromCounts(total, counts, enumValues, Enum::name);
    }

    /**
     * Convenience overload that derives total from the sum of grouped counts.
     *
     * @param counts    counts keyed by enum constant
     * @param enumClass enum class defining all type values
     * @param <E>       enum type
     * @return domain breakdown
     */
    public static <E extends Enum<E>> EntityTypeBreakdown fromCounts(Map<E, Long> counts, Class<E> enumClass) {
        long total = Arrays.stream(enumClass.getEnumConstants())
                .mapToLong(type -> counts.getOrDefault(type, 0L))
                .sum();
        return fromCounts(total, counts, enumClass);
    }
}
