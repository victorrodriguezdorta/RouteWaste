package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.alert.StopAlertValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

class StopAlertValueTests {

    /**
     * Verifies that a stop alert value stores its numeric value.
     */
    @Test
    void constructorRight() {
        StopAlertValue value = new StopAlertValue(42.0);
        assertEquals(42.0, value.getValue());
    }

    /**
     * Verifies equality and hash code for matching stop alert values.
     */
    @Test
    void equalsAndHashCode() {
        StopAlertValue value = new StopAlertValue(42.0);
        StopAlertValue same = new StopAlertValue(42.0);
        StopAlertValue different = new StopAlertValue(7.0);
        assertEquals(value, same);
        assertEquals(value.hashCode(), same.hashCode());
        assertNotEquals(value, different);
    }
}
