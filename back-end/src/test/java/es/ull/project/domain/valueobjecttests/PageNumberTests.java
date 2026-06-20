package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.page.PageNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class PageNumberTests {

    private static final String NEGATIVE_PAGE_NUMBER_MESSAGE = "Page number cannot be negative";

    /**
     * Verifies that a valid page number is stored correctly.
     */
    @Test
    void constructorValidPageNumber() {
        PageNumber pageNumber = new PageNumber(2);
        assertThat(pageNumber.getValue()).isEqualTo(2);
    }

    /**
     * Verifies that negative page numbers are rejected.
     */
    @Test
    void constructorNegativePageNumber() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new PageNumber(-1));
        assertThat(exception.getMessage()).isEqualTo(NEGATIVE_PAGE_NUMBER_MESSAGE);
    }

    /**
     * Verifies that page numbers with equal values are equal and share the same hash code.
     */
    @Test
    void equalsEqualPageNumbers() {
        PageNumber firstPageNumber = new PageNumber(2);
        PageNumber secondPageNumber = new PageNumber(2);
        assertThat(firstPageNumber).isEqualTo(secondPageNumber);
        assertThat(firstPageNumber.hashCode()).isEqualTo(secondPageNumber.hashCode());
    }

    /**
     * Verifies that page numbers with different values are not equal.
     */
    @Test
    void equalsDifferentPageNumbers() {
        PageNumber firstPageNumber = new PageNumber(2);
        PageNumber secondPageNumber = new PageNumber(3);
        assertThat(firstPageNumber).isNotEqualTo(secondPageNumber);
    }
}
