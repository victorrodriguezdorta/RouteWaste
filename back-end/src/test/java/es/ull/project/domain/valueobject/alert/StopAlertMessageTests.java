package es.ull.project.domain.valueobject.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StopAlertMessageTests {

    @Test
    void constructorRight() {
        StopAlertMessage message = new StopAlertMessage("Vehicle is full");

        assertEquals("Vehicle is full", message.getValue());
    }

    @Test
    void constructorInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new StopAlertMessage(null));
        assertThrows(IllegalArgumentException.class, () -> new StopAlertMessage(" "));
    }

    @Test
    void equalsAndHashCode() {
        StopAlertMessage message = new StopAlertMessage("Vehicle is full");
        StopAlertMessage same = new StopAlertMessage("Vehicle is full");
        StopAlertMessage different = new StopAlertMessage("Container overflowed");

        assertEquals(message, same);
        assertEquals(message.hashCode(), same.hashCode());
        assertNotEquals(message, different);
    }
}
