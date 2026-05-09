package es.ull.project.domain.valueobject.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AveragePickupTimeMinutesTests {

    @Test
    void givenValidAveragePickupTime_whenInstantiating_thenSuccess() {
        AveragePickupTimeMinutes time = new AveragePickupTimeMinutes(15);
        assertThat(time.getValue()).isEqualTo(15);
    }

    @Test
    void givenNegativeAveragePickupTime_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new AveragePickupTimeMinutes(-1));
        assertThat(exception.getMessage()).isEqualTo("Average pickup time in minutes cannot be negative");
    }

    @Test
    void givenEqualAveragePickupTime_whenEquals_thenTrue() {
        AveragePickupTimeMinutes t1 = new AveragePickupTimeMinutes(10);
        AveragePickupTimeMinutes t2 = new AveragePickupTimeMinutes(10);
        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    }

    @Test
    void givenDifferentAveragePickupTime_whenEquals_thenFalse() {
        AveragePickupTimeMinutes t1 = new AveragePickupTimeMinutes(10);
        AveragePickupTimeMinutes t2 = new AveragePickupTimeMinutes(20);
        assertThat(t1).isNotEqualTo(t2);
    }
}
