package es.ull.project.domain.enumerate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeUnitTests {

    /**
     * fromString()
     */
    @Test
    public void fromString() {
        final String value = TimeUnit.random().name();
        TimeUnit unit = TimeUnit.fromString(value);

        assertNotNull(unit);
        assertTrue(unit instanceof TimeUnit);

        assertThrows(
                IllegalArgumentException.class,
                () -> TimeUnit.fromString("INVALID_UNIT")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> TimeUnit.fromString(null)
        );
    }

    /**
     * isValid() - valid value
     */
    @Test
    public void isValid_validValue() {
        final String value = TimeUnit.random().name();
        assertTrue(TimeUnit.isValid(value));
    }

    /**
     * isValid() - not valid value
     */
    @Test
    public void isValid_notValidValue() {
        assertFalse(TimeUnit.isValid(null));
        assertFalse(TimeUnit.isValid("INVALID_UNIT"));
    }

    /**
     * random()
     */
    @Test
    public void random() {
        TimeUnit unit = TimeUnit.random();

        assertNotNull(unit);
        assertTrue(unit instanceof TimeUnit);
    }
}
