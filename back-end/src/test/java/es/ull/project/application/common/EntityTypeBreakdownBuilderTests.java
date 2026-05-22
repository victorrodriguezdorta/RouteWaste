package es.ull.project.application.common;

import static org.assertj.core.api.Assertions.assertThat;

import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.readmodel.EntityTypeBreakdown;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EntityTypeBreakdownBuilderTests {

    @Test
    void givenCountsByType_whenBuildingBreakdown_thenIncludesEveryEnumWithTotals() {
        Map<VehicleType, Long> counts = new EnumMap<>(VehicleType.class);
        counts.put(VehicleType.COLLECTION_TRUCK, 6L);
        counts.put(VehicleType.TRANSFER_TRUCK, 4L);
        counts.put(VehicleType.SUPPORT_VEHICLE, 3L);

        EntityTypeBreakdown breakdown = EntityTypeBreakdownBuilder.fromCounts(13L, counts, VehicleType.class);

        assertThat(breakdown.total().getValue()).isEqualTo(13L);
        assertThat(breakdown.byType()).hasSize(VehicleType.values().length);
        assertThat(breakdown.byType())
                .anyMatch(entry -> "COLLECTION_TRUCK".equals(entry.type().getValue())
                        && entry.count().getValue() == 6L);
        assertThat(breakdown.byType())
                .anyMatch(entry -> "TRANSFER_TRUCK".equals(entry.type().getValue())
                        && entry.count().getValue() == 4L);
        assertThat(breakdown.byType())
                .anyMatch(entry -> "SUPPORT_VEHICLE".equals(entry.type().getValue())
                        && entry.count().getValue() == 3L);
    }
}
