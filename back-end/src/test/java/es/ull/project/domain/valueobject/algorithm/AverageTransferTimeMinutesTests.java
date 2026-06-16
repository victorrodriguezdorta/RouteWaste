package es.ull.project.domain.valueobject.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AverageTransferTimeMinutesTests {

    @Test
    void givenValidAverageTransferTime_whenInstantiating_thenSuccess() {
        AverageTransferTimeMinutes time = new AverageTransferTimeMinutes(15);

        assertThat(time.getValue()).isEqualTo(15);
    }

    @Test
    void givenNegativeAverageTransferTime_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new AverageTransferTimeMinutes(-1));

        assertThat(exception.getMessage()).isEqualTo("Average transfer time in minutes cannot be negative");
    }

    @Test
    void givenEqualAverageTransferTime_whenEquals_thenTrue() {
        AverageTransferTimeMinutes firstTime = new AverageTransferTimeMinutes(10);
        AverageTransferTimeMinutes secondTime = new AverageTransferTimeMinutes(10);

        assertThat(firstTime).isEqualTo(secondTime);
        assertThat(firstTime.hashCode()).isEqualTo(secondTime.hashCode());
    }

    @Test
    void givenDifferentAverageTransferTime_whenEquals_thenFalse() {
        AverageTransferTimeMinutes firstTime = new AverageTransferTimeMinutes(10);
        AverageTransferTimeMinutes secondTime = new AverageTransferTimeMinutes(20);

        assertThat(firstTime).isNotEqualTo(secondTime);
    }
}
