package es.ull.project.domain.valueobject.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PageSizeTests {

    @Test
    void givenValidPageSize_whenInstantiating_thenSuccess() {
        PageSize pageSize = new PageSize(10);
        assertThat(pageSize.getValue()).isEqualTo(10);
    }

    @Test
    void givenZeroPageSize_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new PageSize(0));
        assertThat(exception.getMessage()).isEqualTo("Page size must be greater than zero");
    }

    @Test
    void givenNegativePageSize_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new PageSize(-1));
        assertThat(exception.getMessage()).isEqualTo("Page size must be greater than zero");
    }

    @Test
    void givenEqualPageSize_whenEquals_thenTrue() {
        PageSize firstPageSize = new PageSize(10);
        PageSize secondPageSize = new PageSize(10);
        assertThat(firstPageSize).isEqualTo(secondPageSize);
        assertThat(firstPageSize.hashCode()).isEqualTo(secondPageSize.hashCode());
    }

    @Test
    void givenDifferentPageSize_whenEquals_thenFalse() {
        PageSize firstPageSize = new PageSize(10);
        PageSize secondPageSize = new PageSize(20);
        assertThat(firstPageSize).isNotEqualTo(secondPageSize);
    }
}
