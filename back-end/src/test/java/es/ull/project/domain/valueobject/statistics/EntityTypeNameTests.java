package es.ull.project.domain.valueobject.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EntityTypeNameTests {

    @Test
    void givenValidTypeName_whenInstantiating_thenSuccess() {
        EntityTypeName typeName = new EntityTypeName("COLLECTION_TRUCK");

        assertThat(typeName.getValue()).isEqualTo("COLLECTION_TRUCK");
    }

    @Test
    void givenBlankTypeName_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new EntityTypeName(" "));

        assertThat(exception.getMessage()).isEqualTo("Entity type name must not be null or blank");
    }

    @Test
    void givenEqualTypeNames_whenEquals_thenTrue() {
        EntityTypeName first = new EntityTypeName("COLLECTION_TRUCK");
        EntityTypeName second = new EntityTypeName("COLLECTION_TRUCK");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void givenDifferentTypeNames_whenEquals_thenFalse() {
        EntityTypeName first = new EntityTypeName("COLLECTION_TRUCK");
        EntityTypeName second = new EntityTypeName("COMPACTOR_TRUCK");

        assertThat(first).isNotEqualTo(second);
    }
}
