package es.ull.project.adapter.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import es.ull.project.adapter.rest.response.common.EntityStatisticsResponseBody;
import es.ull.project.domain.readmodel.EntityTypeBreakdown;
import es.ull.project.domain.valueobject.page.TotalElements;
import java.util.List;
import org.junit.jupiter.api.Test;

class EntityStatisticsResponseMapperTests {

    @Test
    void givenBreakdown_whenMapping_thenCopiesTotalAndByType() {
        EntityTypeBreakdown breakdown = new EntityTypeBreakdown(
                new TotalElements(13L),
                List.of(
                        new EntityTypeBreakdown.TypeCount("COLLECTION_TRUCK", new TotalElements(6L)),
                        new EntityTypeBreakdown.TypeCount("TRANSFER_TRUCK", new TotalElements(4L)),
                        new EntityTypeBreakdown.TypeCount("SUPPORT_VEHICLE", new TotalElements(3L))));

        EntityStatisticsResponseBody body = EntityStatisticsResponseMapper.toResponseBody(breakdown);

        assertThat(body.total.getValue()).isEqualTo(13L);
        assertThat(body.byType).hasSize(3);
        assertThat(body.byType.get(0).type).isEqualTo("COLLECTION_TRUCK");
        assertThat(body.byType.get(0).count.getValue()).isEqualTo(6L);
    }
}
