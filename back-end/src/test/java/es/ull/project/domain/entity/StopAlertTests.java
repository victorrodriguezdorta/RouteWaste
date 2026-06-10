package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

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
    void restoreConstructor_right() {
        UUID id = UUID.randomUUID();
        StopAlertType type = new StopAlertType("VEHICLE_FULL");
        StopAlertMessage message = new StopAlertMessage("Vehicle is full");
        StopAlertValue value = new StopAlertValue(42.0);

        StopAlert alert = new StopAlert(id, type, message, value);

        assertEquals(id, alert.getId());
        assertEquals(type, alert.getType());
        assertEquals(message, alert.getMessage());
        assertEquals(value, alert.getValue().orElseThrow());
    }

    @Test
    void restoreConstructor_rejectsNullId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new StopAlert(
                        null,
                        new StopAlertType("VEHICLE_FULL"),
                        new StopAlertMessage("Vehicle is full"),
                        null));

        assertEquals("Stop alert id is required", exception.getMessage());
    }

    @Test
    void fromValues_withoutNumericValue() {
        StopAlert alert = StopAlert.fromValues("CONTAINER_OVERFLOWED", "Container overflowed");

        assertEquals(new StopAlertType("CONTAINER_OVERFLOWED"), alert.getType());
        assertTrue(alert.getValue().isEmpty());
    }

    @Test
    void getId_isAssignedOnCreation() {
        StopAlert alert = new StopAlert(
                new StopAlertType("VEHICLE_FULL"),
                new StopAlertMessage("Vehicle is full"));

        assertNotNull(alert.getId());
    }

    @Test
    void toString_withValue() {
        StopAlert alert = StopAlert.fromValues("VEHICLE_FULL", "Vehicle is full", 42.0);

        String result = alert.toString();

        assertTrue(result.contains("VEHICLE_FULL"));
        assertTrue(result.contains("Vehicle is full"));
        assertTrue(result.contains("value="));
    }

    @Test
    void toString_withoutValue() {
        StopAlert alert = StopAlert.fromValues("CONTAINER_OVERFLOWED", "Container overflowed");

        String result = alert.toString();

        assertTrue(result.contains("CONTAINER_OVERFLOWED"));
        assertFalse(result.contains("value="));
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
