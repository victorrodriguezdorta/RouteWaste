package es.ull.project.domain.valueobject.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PageNumberTests {

    @Test
    void givenValidPageNumber_whenInstantiating_thenSuccess() {
        PageNumber pageNumber = new PageNumber(2);
        assertThat(pageNumber.getValue()).isEqualTo(2);
    }

    @Test
    void givenNegativePageNumber_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new PageNumber(-1));
        assertThat(exception.getMessage()).isEqualTo("Page number cannot be negative");
    }

    @Test
    void givenEqualPageNumber_whenEquals_thenTrue() {
        PageNumber firstPageNumber = new PageNumber(2);
        PageNumber secondPageNumber = new PageNumber(2);
        assertThat(firstPageNumber).isEqualTo(secondPageNumber);
        assertThat(firstPageNumber.hashCode()).isEqualTo(secondPageNumber.hashCode());
    }

    @Test
    void givenDifferentPageNumber_whenEquals_thenFalse() {
        PageNumber firstPageNumber = new PageNumber(2);
        PageNumber secondPageNumber = new PageNumber(3);
        assertThat(firstPageNumber).isNotEqualTo(secondPageNumber);
    }
}
