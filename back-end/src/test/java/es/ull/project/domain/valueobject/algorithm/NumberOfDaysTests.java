package es.ull.project.domain.valueobject.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NumberOfDaysTests {

    @Test
    void givenValidNumberOfDays_whenInstantiating_thenSuccess() {
        NumberOfDays numberOfDays = new NumberOfDays(7);
        assertThat(numberOfDays.getValue()).isEqualTo(7);
    }

    @Test
    void givenZeroNumberOfDays_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new NumberOfDays(0));
        assertThat(exception.getMessage()).isEqualTo("Number of days must be greater than zero");
    }

    @Test
    void givenEqualNumberOfDays_whenEquals_thenTrue() {
        NumberOfDays firstNumberOfDays = new NumberOfDays(7);
        NumberOfDays secondNumberOfDays = new NumberOfDays(7);
        assertThat(firstNumberOfDays).isEqualTo(secondNumberOfDays);
        assertThat(firstNumberOfDays.hashCode()).isEqualTo(secondNumberOfDays.hashCode());
    }

    @Test
    void givenDifferentNumberOfDays_whenEquals_thenFalse() {
        NumberOfDays firstNumberOfDays = new NumberOfDays(7);
        NumberOfDays secondNumberOfDays = new NumberOfDays(14);
        assertThat(firstNumberOfDays).isNotEqualTo(secondNumberOfDays);
    }
}
