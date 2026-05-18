package es.ull.project.domain.valueobject.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StopAlertTypeTests {

    @Test
    void constructorRight() {
        StopAlertType type = new StopAlertType("VEHICLE_FULL");

        assertEquals("VEHICLE_FULL", type.getValue());
    }

    @Test
    void constructorInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new StopAlertType(null));
        assertThrows(IllegalArgumentException.class, () -> new StopAlertType(" "));
    }

    @Test
    void equalsAndHashCode() {
        StopAlertType type = new StopAlertType("VEHICLE_FULL");
        StopAlertType same = new StopAlertType("VEHICLE_FULL");
        StopAlertType different = new StopAlertType("CONTAINER_OVERFLOWED");

        assertEquals(type, same);
        assertEquals(type.hashCode(), same.hashCode());
        assertNotEquals(type, different);
    }
}
