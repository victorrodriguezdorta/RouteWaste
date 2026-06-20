package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.page.TotalElements;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class TotalElementsTests {

    private static final String NEGATIVE_TOTAL_ELEMENTS_MESSAGE = "Total elements cannot be negative";

    /**
     * Verifies that valid total elements are created successfully.
     */
    @Test
    void constructorValidValue() {
        TotalElements totalElements = new TotalElements(10L);
        assertEquals(10L, totalElements.getValue());
    }

    /**
     * Verifies that negative total elements throw an exception.
     */
    @Test
    void constructorNegativeValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TotalElements(-1L));
        assertEquals(NEGATIVE_TOTAL_ELEMENTS_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that equal total elements compare as equal.
     */
    @Test
    void equalsSameValue() {
        TotalElements firstTotalElements = new TotalElements(5L);
        TotalElements secondTotalElements = new TotalElements(5L);
        assertEquals(firstTotalElements, secondTotalElements);
        assertEquals(firstTotalElements.hashCode(), secondTotalElements.hashCode());
    }

    /**
     * Verifies that different total elements compare as different.
     */
    @Test
    void equalsDifferentValue() {
        TotalElements firstTotalElements = new TotalElements(5L);
        TotalElements secondTotalElements = new TotalElements(10L);
        assertNotEquals(firstTotalElements, secondTotalElements);
    }
}
