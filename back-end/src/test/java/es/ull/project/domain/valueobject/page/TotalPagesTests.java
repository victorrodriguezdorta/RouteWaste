package es.ull.project.domain.valueobject.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TotalPagesTests {

    @Test
    void givenValidTotalPages_whenInstantiating_thenSuccess() {
        TotalPages totalPages = new TotalPages(10);
        assertThat(totalPages.getValue()).isEqualTo(10);
    }

    @Test
    void givenNegativeTotalPages_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new TotalPages(-1));
        assertThat(exception.getMessage()).isEqualTo("Total pages cannot be negative");
    }

    @Test
    void givenEqualTotalPages_whenEquals_thenTrue() {
        TotalPages tp1 = new TotalPages(5);
        TotalPages tp2 = new TotalPages(5);
        assertThat(tp1).isEqualTo(tp2);
        assertThat(tp1.hashCode()).isEqualTo(tp2.hashCode());
    }

    @Test
    void givenDifferentTotalPages_whenEquals_thenFalse() {
        TotalPages tp1 = new TotalPages(5);
        TotalPages tp2 = new TotalPages(10);
        assertThat(tp1).isNotEqualTo(tp2);
    }
}
