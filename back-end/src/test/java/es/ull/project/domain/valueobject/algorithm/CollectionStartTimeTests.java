package es.ull.project.domain.valueobject.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class CollectionStartTimeTests {

    @Test
    void givenValidLocalTime_whenInstantiating_thenSuccess() {
        CollectionStartTime time = new CollectionStartTime(LocalTime.of(8, 30));

        assertThat(time.getValue()).isEqualTo(LocalTime.of(8, 30));
        assertThat(time.getFormatted()).isEqualTo("08:30");
    }

    @Test
    void givenValidString_whenParsing_thenSuccess() {
        CollectionStartTime time = CollectionStartTime.fromString("09:15");

        assertThat(time.getValue()).isEqualTo(LocalTime.of(9, 15));
        assertThat(time.getFormatted()).isEqualTo("09:15");
    }

    @Test
    void givenNullLocalTime_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new CollectionStartTime((LocalTime) null));

        assertThat(exception.getMessage()).isEqualTo("Collection start time is not defined");
    }

    @Test
    void givenMalformedString_whenParsing_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> CollectionStartTime.fromString("invalid"));

        assertThat(exception.getMessage()).isEqualTo("Collection start time must follow the HH:mm format");
    }

    @Test
    void givenEqualTimes_whenEquals_thenTrue() {
        CollectionStartTime firstTime = CollectionStartTime.fromString("10:00");
        CollectionStartTime secondTime = new CollectionStartTime(LocalTime.of(10, 0));

        assertThat(firstTime).isEqualTo(secondTime);
        assertThat(firstTime.hashCode()).isEqualTo(secondTime.hashCode());
    }

    @Test
    void givenDifferentTimes_whenEquals_thenFalse() {
        CollectionStartTime firstTime = CollectionStartTime.fromString("10:00");
        CollectionStartTime secondTime = CollectionStartTime.fromString("11:00");

        assertThat(firstTime).isNotEqualTo(secondTime);
    }
}
