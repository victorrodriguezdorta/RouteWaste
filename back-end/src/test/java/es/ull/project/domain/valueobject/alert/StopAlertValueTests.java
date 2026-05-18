package es.ull.project.domain.valueobject.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class StopAlertValueTests {

    @Test
    void constructorRight() {
        StopAlertValue value = new StopAlertValue(42.0);

        assertEquals(42.0, value.getValue());
    }

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
