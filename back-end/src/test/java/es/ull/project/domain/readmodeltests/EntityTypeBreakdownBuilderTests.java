package es.ull.project.domain.readmodeltests;

import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.readmodel.EntityTypeBreakdown;
import es.ull.project.domain.readmodel.EntityTypeBreakdownBuilder;
import java.util.EnumMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class EntityTypeBreakdownBuilderTests {

    private static final long COLLECTION_TRUCK_COUNT = 6L;
    private static final long TRANSFER_TRUCK_COUNT = 4L;
    private static final long SUPPORT_VEHICLE_COUNT = 3L;
    private static final long TOTAL_VEHICLE_COUNT = 13L;
    private static final String COLLECTION_TRUCK_TYPE = "COLLECTION_TRUCK";
    private static final String TRANSFER_TRUCK_TYPE = "TRANSFER_TRUCK";
    private static final String SUPPORT_VEHICLE_TYPE = "SUPPORT_VEHICLE";

    /**
     * Verifies that the breakdown includes every enum value with the provided totals.
     */
    @Test
    void buildBreakdownIncludesEveryEnumWithTotals() {
        Map<VehicleType, Long> counts = new EnumMap<>(VehicleType.class);
        counts.put(VehicleType.COLLECTION_TRUCK, COLLECTION_TRUCK_COUNT);
        counts.put(VehicleType.TRANSFER_TRUCK, TRANSFER_TRUCK_COUNT);
        counts.put(VehicleType.SUPPORT_VEHICLE, SUPPORT_VEHICLE_COUNT);
        EntityTypeBreakdown breakdown =
                EntityTypeBreakdownBuilder.fromCounts(TOTAL_VEHICLE_COUNT, counts, VehicleType.class);
        assertThat(breakdown.total().getValue()).isEqualTo(TOTAL_VEHICLE_COUNT);
        assertThat(breakdown.byType()).hasSize(VehicleType.values().length);
        assertThat(breakdown.byType())
                .anyMatch(entry -> COLLECTION_TRUCK_TYPE.equals(entry.type().getValue())
                        && entry.count().getValue() == COLLECTION_TRUCK_COUNT);
        assertThat(breakdown.byType())
                .anyMatch(entry -> TRANSFER_TRUCK_TYPE.equals(entry.type().getValue())
                        && entry.count().getValue() == TRANSFER_TRUCK_COUNT);
        assertThat(breakdown.byType())
                .anyMatch(entry -> SUPPORT_VEHICLE_TYPE.equals(entry.type().getValue())
                        && entry.count().getValue() == SUPPORT_VEHICLE_COUNT);
    }
}
