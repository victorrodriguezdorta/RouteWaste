package es.ull.project.domain.enumeratetests;

import es.ull.project.domain.enumerate.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TimeUnitTests {

    private static final String INVALID_UNIT_NAME = "INVALID_UNIT";

    /**
     * Should convert a valid time unit name and reject invalid names.
     */
    @Test
    void fromString() {
        final String value = TimeUnit.random().name();
        TimeUnit unit = TimeUnit.fromString(value);
        assertNotNull(unit);
        assertTrue(unit instanceof TimeUnit);
        assertThrows(
                IllegalArgumentException.class,
                () -> TimeUnit.fromString(INVALID_UNIT_NAME));
        assertThrows(
                IllegalArgumentException.class,
                () -> TimeUnit.fromString(null));
    }

    /**
     * Should return the ordinal index for a valid time unit name and reject invalid names.
     */
    @Test
    void indexOf() {
        TimeUnit expected = TimeUnit.random();
        assertEquals(expected.ordinal(), TimeUnit.indexOf(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> TimeUnit.indexOf(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> TimeUnit.indexOf(INVALID_UNIT_NAME));
    }

    /**
     * Should report whether a time unit name can be parsed.
     */
    @Test
    void isValid() {
        assertTrue(TimeUnit.isValid(TimeUnit.random().name()));
        assertFalse(TimeUnit.isValid(null));
        assertFalse(TimeUnit.isValid(INVALID_UNIT_NAME));
    }

    /**
     * Should return a random time unit.
     */
    @Test
    void random() {
        TimeUnit unit = TimeUnit.random();
        assertNotNull(unit);
        assertTrue(unit instanceof TimeUnit);
    }
}
