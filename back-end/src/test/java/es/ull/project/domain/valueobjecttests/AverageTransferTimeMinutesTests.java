package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.algorithm.AverageTransferTimeMinutes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class AverageTransferTimeMinutesTests {

    private static final String NEGATIVE_AVERAGE_TRANSFER_TIME_MESSAGE =
            "Average transfer time in minutes cannot be negative";

    /**
     * Verifies that a valid average transfer time value is stored correctly.
     */
    @Test
    void givenValidAverageTransferTimeWhenInstantiatingThenSuccess() {
        AverageTransferTimeMinutes time = new AverageTransferTimeMinutes(15);
        assertThat(time.getValue()).isEqualTo(15);
    }

    /**
     * Verifies that negative average transfer time values are rejected.
     */
    @Test
    void givenNegativeAverageTransferTimeWhenInstantiatingThenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new AverageTransferTimeMinutes(-1));
        assertThat(exception.getMessage()).isEqualTo(NEGATIVE_AVERAGE_TRANSFER_TIME_MESSAGE);
    }

    /**
     * Verifies that average transfer times with equal values are equal and share the same hash code.
     */
    @Test
    void givenEqualAverageTransferTimeWhenEqualsThenTrue() {
        AverageTransferTimeMinutes firstTime = new AverageTransferTimeMinutes(10);
        AverageTransferTimeMinutes secondTime = new AverageTransferTimeMinutes(10);
        assertThat(firstTime).isEqualTo(secondTime);
        assertThat(firstTime.hashCode()).isEqualTo(secondTime.hashCode());
    }

    /**
     * Verifies that average transfer times with different values are not equal.
     */
    @Test
    void givenDifferentAverageTransferTimeWhenEqualsThenFalse() {
        AverageTransferTimeMinutes firstTime = new AverageTransferTimeMinutes(10);
        AverageTransferTimeMinutes secondTime = new AverageTransferTimeMinutes(20);
        assertThat(firstTime).isNotEqualTo(secondTime);
    }
}
