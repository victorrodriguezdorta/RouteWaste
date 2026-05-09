package es.ull.project.domain.valueobject.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NumberOfElementsTests {

    @Test
    void givenValidNumberOfElements_whenInstantiating_thenSuccess() {
        NumberOfElements numberOfElements = new NumberOfElements(8);
        assertThat(numberOfElements.getValue()).isEqualTo(8);
    }

    @Test
    void givenNegativeNumberOfElements_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new NumberOfElements(-1));
        assertThat(exception.getMessage()).isEqualTo("Number of elements cannot be negative");
    }

    @Test
    void givenEqualNumberOfElements_whenEquals_thenTrue() {
        NumberOfElements firstNumberOfElements = new NumberOfElements(8);
        NumberOfElements secondNumberOfElements = new NumberOfElements(8);
        assertThat(firstNumberOfElements).isEqualTo(secondNumberOfElements);
        assertThat(firstNumberOfElements.hashCode()).isEqualTo(secondNumberOfElements.hashCode());
    }

    @Test
    void givenDifferentNumberOfElements_whenEquals_thenFalse() {
        NumberOfElements firstNumberOfElements = new NumberOfElements(8);
        NumberOfElements secondNumberOfElements = new NumberOfElements(12);
        assertThat(firstNumberOfElements).isNotEqualTo(secondNumberOfElements);
    }
}
