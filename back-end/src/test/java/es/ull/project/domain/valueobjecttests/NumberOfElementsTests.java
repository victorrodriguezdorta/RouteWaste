package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.page.NumberOfElements;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class NumberOfElementsTests {

    private static final String NEGATIVE_NUMBER_OF_ELEMENTS_MESSAGE =
            "Number of elements cannot be negative";

    /**
     * Verifies that a valid number of elements is stored correctly.
     */
    @Test
    void constructorValidNumberOfElements() {
        NumberOfElements numberOfElements = new NumberOfElements(8);
        assertThat(numberOfElements.getValue()).isEqualTo(8);
    }

    /**
     * Verifies that negative numbers of elements are rejected.
     */
    @Test
    void constructorNegativeNumberOfElements() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new NumberOfElements(-1));
        assertThat(exception.getMessage()).isEqualTo(NEGATIVE_NUMBER_OF_ELEMENTS_MESSAGE);
    }

    /**
     * Verifies that numbers of elements with equal values are equal and share the same hash code.
     */
    @Test
    void equalsEqualNumberOfElements() {
        NumberOfElements firstNumberOfElements = new NumberOfElements(8);
        NumberOfElements secondNumberOfElements = new NumberOfElements(8);
        assertThat(firstNumberOfElements).isEqualTo(secondNumberOfElements);
        assertThat(firstNumberOfElements.hashCode()).isEqualTo(secondNumberOfElements.hashCode());
    }

    /**
     * Verifies that numbers of elements with different values are not equal.
     */
    @Test
    void equalsDifferentNumberOfElements() {
        NumberOfElements firstNumberOfElements = new NumberOfElements(8);
        NumberOfElements secondNumberOfElements = new NumberOfElements(12);
        assertThat(firstNumberOfElements).isNotEqualTo(secondNumberOfElements);
    }
}
