package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.alert.StopAlertMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class StopAlertMessageTests {

    private static final String VALID_ALERT_MESSAGE = "Vehicle is full";
    private static final String DIFFERENT_ALERT_MESSAGE = "Container overflowed";
    private static final String BLANK_ALERT_MESSAGE = " ";

    /**
     * Verifies that a valid stop alert message is stored correctly.
     */
    @Test
    void constructorRight() {
        StopAlertMessage message = new StopAlertMessage(VALID_ALERT_MESSAGE);
        assertEquals(VALID_ALERT_MESSAGE, message.getValue());
    }

    /**
     * Verifies that null and blank stop alert messages are rejected.
     */
    @Test
    void constructorInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new StopAlertMessage(null));
        assertThrows(IllegalArgumentException.class, () -> new StopAlertMessage(BLANK_ALERT_MESSAGE));
    }

    /**
     * Verifies equality and hash code behavior for stop alert messages.
     */
    @Test
    void equalsAndHashCode() {
        StopAlertMessage message = new StopAlertMessage(VALID_ALERT_MESSAGE);
        StopAlertMessage same = new StopAlertMessage(VALID_ALERT_MESSAGE);
        StopAlertMessage different = new StopAlertMessage(DIFFERENT_ALERT_MESSAGE);
        assertEquals(message, same);
        assertEquals(message.hashCode(), same.hashCode());
        assertNotEquals(message, different);
    }
}
