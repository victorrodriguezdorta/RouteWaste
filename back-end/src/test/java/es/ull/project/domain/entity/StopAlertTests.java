package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.ull.project.domain.valueobject.alert.StopAlertMessage;
import es.ull.project.domain.valueobject.alert.StopAlertType;
import es.ull.project.domain.valueobject.alert.StopAlertValue;
import org.junit.jupiter.api.Test;

class StopAlertTests {

    @Test
    void constructorRight() {
        StopAlert alert = new StopAlert(
                new StopAlertType("VEHICLE_FULL"),
                new StopAlertMessage("Vehicle is full"),
                new StopAlertValue(42.0));

        assertEquals(new StopAlertType("VEHICLE_FULL"), alert.getType());
        assertEquals(new StopAlertMessage("Vehicle is full"), alert.getMessage());
        assertTrue(alert.getValue().isPresent());
        assertEquals(new StopAlertValue(42.0), alert.getValue().get());
    }

    @Test
    void constructorWithoutValue() {
        StopAlert alert = new StopAlert(
                new StopAlertType("CONTAINER_OVERFLOWED"),
                new StopAlertMessage("Container overflowed"));

        assertEquals(new StopAlertType("CONTAINER_OVERFLOWED"), alert.getType());
        assertEquals(new StopAlertMessage("Container overflowed"), alert.getMessage());
        assertTrue(alert.getValue().isEmpty());
    }

    @Test
    void copyConstructor() {
        StopAlert original = StopAlert.fromValues("VEHICLE_FULL", "Vehicle is full", 42.0);
        StopAlert copy = new StopAlert(original);

        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
    }

    @Test
    void equalsAndHashCode() {
        StopAlert alert = StopAlert.fromValues("VEHICLE_FULL", "Vehicle is full", 42.0);
        StopAlert same = new StopAlert(alert);
        StopAlert different = StopAlert.fromValues("CONTAINER_OVERFLOWED", "Container overflowed", 10.0);

        assertTrue(alert.equals(alert));
        assertFalse(alert.equals(null));
        assertFalse(alert.equals(new Object()));
        assertEquals(alert, same);
        assertEquals(alert.hashCode(), same.hashCode());
        assertNotEquals(alert, different);
        assertNotEquals(alert.hashCode(), different.hashCode());
    }
}
