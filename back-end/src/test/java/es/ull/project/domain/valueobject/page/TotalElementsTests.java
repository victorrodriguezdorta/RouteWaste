package es.ull.project.domain.valueobject.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TotalElementsTests {

    @Test
    void givenValidTotalElements_whenInstantiating_thenSuccess() {
        TotalElements totalElements = new TotalElements(10L);
        assertThat(totalElements.getValue()).isEqualTo(10L);
    }

    @Test
    void givenNegativeTotalElements_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new TotalElements(-1L));
        assertThat(exception.getMessage()).isEqualTo("Total elements cannot be negative");
    }

    @Test
    void givenEqualTotalElements_whenEquals_thenTrue() {
        TotalElements firstTotalElements = new TotalElements(5L);
        TotalElements secondTotalElements = new TotalElements(5L);
        assertThat(firstTotalElements).isEqualTo(secondTotalElements);
        assertThat(firstTotalElements.hashCode()).isEqualTo(secondTotalElements.hashCode());
    }

    @Test
    void givenDifferentTotalElements_whenEquals_thenFalse() {
        TotalElements firstTotalElements = new TotalElements(5L);
        TotalElements secondTotalElements = new TotalElements(10L);
        assertThat(firstTotalElements).isNotEqualTo(secondTotalElements);
    }
}
