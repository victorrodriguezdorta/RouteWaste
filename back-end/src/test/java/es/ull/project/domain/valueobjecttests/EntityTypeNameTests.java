package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.statistics.EntityTypeName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class EntityTypeNameTests {

    private static final String BLANK_TYPE_NAME = " ";
    private static final String COLLECTION_TRUCK_TYPE_NAME = "COLLECTION_TRUCK";
    private static final String COMPACTOR_TRUCK_TYPE_NAME = "COMPACTOR_TRUCK";
    private static final String NULL_OR_BLANK_TYPE_NAME_MESSAGE = "Entity type name must not be null or blank";

    /**
     * Verifies that a valid entity type name is stored successfully.
     */
    @Test
    void givenValidTypeNameWhenInstantiatingThenSuccess() {
        EntityTypeName typeName = new EntityTypeName(COLLECTION_TRUCK_TYPE_NAME);
        assertThat(typeName.getValue()).isEqualTo(COLLECTION_TRUCK_TYPE_NAME);
    }

    /**
     * Verifies that blank entity type names are rejected.
     */
    @Test
    void givenBlankTypeNameWhenInstantiatingThenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new EntityTypeName(BLANK_TYPE_NAME));
        assertThat(exception.getMessage()).isEqualTo(NULL_OR_BLANK_TYPE_NAME_MESSAGE);
    }

    /**
     * Verifies equality and hash code for matching entity type names.
     */
    @Test
    void givenEqualTypeNamesWhenEqualsThenTrue() {
        EntityTypeName first = new EntityTypeName(COLLECTION_TRUCK_TYPE_NAME);
        EntityTypeName second = new EntityTypeName(COLLECTION_TRUCK_TYPE_NAME);
        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Verifies inequality for different entity type names.
     */
    @Test
    void givenDifferentTypeNamesWhenEqualsThenFalse() {
        EntityTypeName first = new EntityTypeName(COLLECTION_TRUCK_TYPE_NAME);
        EntityTypeName second = new EntityTypeName(COMPACTOR_TRUCK_TYPE_NAME);
        assertThat(first).isNotEqualTo(second);
    }
}
