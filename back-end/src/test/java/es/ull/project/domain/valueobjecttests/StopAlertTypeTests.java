package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.alert.StopAlertType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class StopAlertTypeTests {

    private static final String VALID_ALERT_TYPE = "VEHICLE_FULL";
    private static final String DIFFERENT_ALERT_TYPE = "CONTAINER_OVERFLOWED";
    private static final String BLANK_ALERT_TYPE = " ";

    /**
     * Verifies that a valid stop alert type is stored correctly.
     */
    @Test
    void constructorRight() {
        StopAlertType type = new StopAlertType(VALID_ALERT_TYPE);
        assertEquals(VALID_ALERT_TYPE, type.getValue());
    }

    /**
     * Verifies that null and blank stop alert types are rejected.
     */
    @Test
    void constructorInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new StopAlertType(null));
        assertThrows(IllegalArgumentException.class, () -> new StopAlertType(BLANK_ALERT_TYPE));
    }

    /**
     * Verifies equality and hash code behavior for stop alert types.
     */
    @Test
    void equalsAndHashCode() {
        StopAlertType type = new StopAlertType(VALID_ALERT_TYPE);
        StopAlertType same = new StopAlertType(VALID_ALERT_TYPE);
        StopAlertType different = new StopAlertType(DIFFERENT_ALERT_TYPE);
        assertEquals(type, same);
        assertEquals(type.hashCode(), same.hashCode());
        assertNotEquals(type, different);
    }
}
