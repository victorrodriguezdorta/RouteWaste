package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class NumberOfDaysTests {

    private static final String NON_POSITIVE_NUMBER_OF_DAYS_MESSAGE = "Number of days must be greater than zero";

    /**
     * Verifies that a valid number of days value is stored correctly.
     */
    @Test
    void givenValidNumberOfDaysWhenInstantiatingThenSuccess() {
        NumberOfDays numberOfDays = new NumberOfDays(7);
        assertThat(numberOfDays.getValue()).isEqualTo(7);
    }

    /**
     * Verifies that zero number of days values are rejected.
     */
    @Test
    void givenZeroNumberOfDaysWhenInstantiatingThenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new NumberOfDays(0));
        assertThat(exception.getMessage()).isEqualTo(NON_POSITIVE_NUMBER_OF_DAYS_MESSAGE);
    }

    /**
     * Verifies that number of days values with equal values are equal and share the same hash code.
     */
    @Test
    void givenEqualNumberOfDaysWhenEqualsThenTrue() {
        NumberOfDays firstNumberOfDays = new NumberOfDays(7);
        NumberOfDays secondNumberOfDays = new NumberOfDays(7);
        assertThat(firstNumberOfDays).isEqualTo(secondNumberOfDays);
        assertThat(firstNumberOfDays.hashCode()).isEqualTo(secondNumberOfDays.hashCode());
    }

    /**
     * Verifies that number of days values with different values are not equal.
     */
    @Test
    void givenDifferentNumberOfDaysWhenEqualsThenFalse() {
        NumberOfDays firstNumberOfDays = new NumberOfDays(7);
        NumberOfDays secondNumberOfDays = new NumberOfDays(14);
        assertThat(firstNumberOfDays).isNotEqualTo(secondNumberOfDays);
    }
}
