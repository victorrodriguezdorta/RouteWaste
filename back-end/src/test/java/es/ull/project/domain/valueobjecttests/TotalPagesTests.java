package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.page.TotalPages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class TotalPagesTests {

    private static final String NEGATIVE_TOTAL_PAGES_MESSAGE = "Total pages cannot be negative";

    /**
     * Verifies that a valid total pages value is stored correctly.
     */
    @Test
    void constructorValidValue() {
        TotalPages totalPages = new TotalPages(10);
        assertEquals(10, totalPages.getValue());
    }

    /**
     * Verifies that negative total pages values are rejected.
     */
    @Test
    void constructorNegativeValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TotalPages(-1));
        assertEquals(NEGATIVE_TOTAL_PAGES_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that total pages with equal values are equal and share the same hash code.
     */
    @Test
    void equalsSameValue() {
        TotalPages firstTotalPages = new TotalPages(5);
        TotalPages secondTotalPages = new TotalPages(5);
        assertEquals(firstTotalPages, secondTotalPages);
        assertEquals(firstTotalPages.hashCode(), secondTotalPages.hashCode());
    }

    /**
     * Verifies that total pages with different values are not equal.
     */
    @Test
    void equalsDifferentValue() {
        TotalPages firstTotalPages = new TotalPages(5);
        TotalPages secondTotalPages = new TotalPages(10);
        assertNotEquals(firstTotalPages, secondTotalPages);
    }
}
