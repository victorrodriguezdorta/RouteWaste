package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class AveragePickupTimeMinutesTests {

    private static final String NEGATIVE_AVERAGE_PICKUP_TIME_MESSAGE =
            "Average pickup time in minutes cannot be negative";

    /**
     * Verifies that a valid average pickup time value is stored correctly.
     */
    @Test
    void givenValidAveragePickupTimeWhenInstantiatingThenSuccess() {
        AveragePickupTimeMinutes time = new AveragePickupTimeMinutes(15);
        assertThat(time.getValue()).isEqualTo(15);
    }

    /**
     * Verifies that negative average pickup time values are rejected.
     */
    @Test
    void givenNegativeAveragePickupTimeWhenInstantiatingThenThrowsException() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AveragePickupTimeMinutes(-1));
        assertThat(exception.getMessage()).isEqualTo(NEGATIVE_AVERAGE_PICKUP_TIME_MESSAGE);
    }

    /**
     * Verifies that average pickup times with equal values are equal and share the same hash code.
     */
    @Test
    void givenEqualAveragePickupTimeWhenEqualsThenTrue() {
        AveragePickupTimeMinutes firstTime = new AveragePickupTimeMinutes(10);
        AveragePickupTimeMinutes secondTime = new AveragePickupTimeMinutes(10);
        assertThat(firstTime).isEqualTo(secondTime);
        assertThat(firstTime.hashCode()).isEqualTo(secondTime.hashCode());
    }

    /**
     * Verifies that average pickup times with different values are not equal.
     */
    @Test
    void givenDifferentAveragePickupTimeWhenEqualsThenFalse() {
        AveragePickupTimeMinutes firstTime = new AveragePickupTimeMinutes(10);
        AveragePickupTimeMinutes secondTime = new AveragePickupTimeMinutes(20);
        assertThat(firstTime).isNotEqualTo(secondTime);
    }
}
