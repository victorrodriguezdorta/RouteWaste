package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.algorithm.CollectionStartTime;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class CollectionStartTimeTests {

    private static final String COLLECTION_START_TIME_NOT_DEFINED_MESSAGE =
            "Collection start time is not defined";
    private static final String COLLECTION_START_TIME_FORMAT_MESSAGE =
            "Collection start time must follow the HH:mm format";
    private static final String EIGHT_THIRTY_TIME_VALUE = "08:30";
    private static final String NINE_FIFTEEN_TIME_VALUE = "09:15";
    private static final String TEN_OCLOCK_TIME_VALUE = "10:00";
    private static final String ELEVEN_OCLOCK_TIME_VALUE = "11:00";
    private static final String INVALID_TIME_VALUE = "invalid";

    /**
     * Should create a collection start time from a valid local time.
     */
    @Test
    void constructorValidLocalTime() {
        CollectionStartTime time = new CollectionStartTime(LocalTime.of(8, 30));
        assertThat(time.getValue()).isEqualTo(LocalTime.of(8, 30));
        assertThat(time.getFormatted()).isEqualTo(EIGHT_THIRTY_TIME_VALUE);
    }

    /**
     * Should parse a collection start time from a valid string.
     */
    @Test
    void fromStringValidValue() {
        CollectionStartTime time = CollectionStartTime.fromString(NINE_FIFTEEN_TIME_VALUE);
        assertThat(time.getValue()).isEqualTo(LocalTime.of(9, 15));
        assertThat(time.getFormatted()).isEqualTo(NINE_FIFTEEN_TIME_VALUE);
    }

    /**
     * Should reject a null local time value.
     */
    @Test
    void constructorNullLocalTimeThrowsException() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CollectionStartTime((LocalTime) null));
        assertThat(exception.getMessage()).isEqualTo(COLLECTION_START_TIME_NOT_DEFINED_MESSAGE);
    }

    /**
     * Should reject a malformed time string.
     */
    @Test
    void fromStringMalformedValueThrowsException() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> CollectionStartTime.fromString(INVALID_TIME_VALUE));
        assertThat(exception.getMessage()).isEqualTo(COLLECTION_START_TIME_FORMAT_MESSAGE);
    }

    /**
     * Should compare equal collection start times as equal.
     */
    @Test
    void equalsEqualTimes() {
        CollectionStartTime firstTime = CollectionStartTime.fromString(TEN_OCLOCK_TIME_VALUE);
        CollectionStartTime secondTime = new CollectionStartTime(LocalTime.of(10, 0));
        assertThat(firstTime).isEqualTo(secondTime);
        assertThat(firstTime.hashCode()).isEqualTo(secondTime.hashCode());
    }

    /**
     * Should compare different collection start times as not equal.
     */
    @Test
    void equalsDifferentTimes() {
        CollectionStartTime firstTime = CollectionStartTime.fromString(TEN_OCLOCK_TIME_VALUE);
        CollectionStartTime secondTime = CollectionStartTime.fromString(ELEVEN_OCLOCK_TIME_VALUE);
        assertThat(firstTime).isNotEqualTo(secondTime);
    }
}
