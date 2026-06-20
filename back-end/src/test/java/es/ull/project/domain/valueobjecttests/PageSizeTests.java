package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.page.PageSize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class PageSizeTests {

    private static final String PAGE_SIZE_MUST_BE_GREATER_THAN_ZERO_MESSAGE =
            "Page size must be greater than zero";

    /**
     * Should create a page size with a valid positive value.
     */
    @Test
    void constructorValidValue() {
        PageSize pageSize = new PageSize(10);
        assertThat(pageSize.getValue()).isEqualTo(10);
    }

    /**
     * Should reject a zero page size value.
     */
    @Test
    void constructorZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new PageSize(0));
        assertThat(exception.getMessage()).isEqualTo(PAGE_SIZE_MUST_BE_GREATER_THAN_ZERO_MESSAGE);
    }

    /**
     * Should reject a negative page size value.
     */
    @Test
    void constructorNegativeValue() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new PageSize(-1));
        assertThat(exception.getMessage()).isEqualTo(PAGE_SIZE_MUST_BE_GREATER_THAN_ZERO_MESSAGE);
    }

    /**
     * Should compare equal page sizes as equal.
     */
    @Test
    void equalsEqualPageSizes() {
        PageSize firstPageSize = new PageSize(10);
        PageSize secondPageSize = new PageSize(10);
        assertThat(firstPageSize).isEqualTo(secondPageSize);
        assertThat(firstPageSize.hashCode()).isEqualTo(secondPageSize.hashCode());
    }

    /**
     * Should compare different page sizes as not equal.
     */
    @Test
    void equalsDifferentPageSizes() {
        PageSize firstPageSize = new PageSize(10);
        PageSize secondPageSize = new PageSize(20);
        assertThat(firstPageSize).isNotEqualTo(secondPageSize);
    }
}
