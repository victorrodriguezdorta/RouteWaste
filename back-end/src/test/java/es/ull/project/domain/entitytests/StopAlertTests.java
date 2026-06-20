package es.ull.project.domain.entitytests;

import es.ull.project.domain.entity.StopAlert;
import es.ull.project.domain.valueobject.alert.StopAlertMessage;
import es.ull.project.domain.valueobject.alert.StopAlertType;
import es.ull.project.domain.valueobject.alert.StopAlertValue;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class StopAlertTests {

    private static final String CONTAINER_OVERFLOWED_MESSAGE_VALUE = "Container overflowed";
    private static final String CONTAINER_OVERFLOWED_TYPE_VALUE = "CONTAINER_OVERFLOWED";
    private static final String STOP_ALERT_ID_REQUIRED_MESSAGE = "Stop alert id is required";
    private static final String VALUE_FIELD_TEXT = "value=";
    private static final String VEHICLE_FULL_MESSAGE_VALUE = "Vehicle is full";
    private static final String VEHICLE_FULL_TYPE_VALUE = "VEHICLE_FULL";
    private static final double ALTERNATIVE_ALERT_VALUE = 10.0;
    private static final double ALERT_VALUE = 42.0;

    /**
     * Should create a stop alert with type, message and numeric value.
     */
    @Test
    void constructorRight() {
        StopAlert alert = new StopAlert(
                new StopAlertType(VEHICLE_FULL_TYPE_VALUE),
                new StopAlertMessage(VEHICLE_FULL_MESSAGE_VALUE),
                new StopAlertValue(ALERT_VALUE));
        assertEquals(new StopAlertType(VEHICLE_FULL_TYPE_VALUE), alert.getType());
        assertEquals(new StopAlertMessage(VEHICLE_FULL_MESSAGE_VALUE), alert.getMessage());
        assertTrue(alert.getValue().isPresent());
        assertEquals(new StopAlertValue(ALERT_VALUE), alert.getValue().get());
    }

    /**
     * Should create a stop alert without a numeric value.
     */
    @Test
    void constructorWithoutValue() {
        StopAlert alert = new StopAlert(
                new StopAlertType(CONTAINER_OVERFLOWED_TYPE_VALUE),
                new StopAlertMessage(CONTAINER_OVERFLOWED_MESSAGE_VALUE));
        assertEquals(new StopAlertType(CONTAINER_OVERFLOWED_TYPE_VALUE), alert.getType());
        assertEquals(new StopAlertMessage(CONTAINER_OVERFLOWED_MESSAGE_VALUE), alert.getMessage());
        assertTrue(alert.getValue().isEmpty());
    }

    /**
     * Should copy all relevant stop alert fields.
     */
    @Test
    void copyConstructor() {
        StopAlert original = StopAlert.fromValues(
                VEHICLE_FULL_TYPE_VALUE,
                VEHICLE_FULL_MESSAGE_VALUE,
                ALERT_VALUE);
        StopAlert copy = new StopAlert(original);
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
    }

    /**
     * Should restore a stop alert with a predefined identifier.
     */
    @Test
    void restoreConstructorRight() {
        UUID id = UUID.randomUUID();
        StopAlertType type = new StopAlertType(VEHICLE_FULL_TYPE_VALUE);
        StopAlertMessage message = new StopAlertMessage(VEHICLE_FULL_MESSAGE_VALUE);
        StopAlertValue value = new StopAlertValue(ALERT_VALUE);
        StopAlert alert = new StopAlert(id, type, message, value);
        assertEquals(id, alert.getId());
        assertEquals(type, alert.getType());
        assertEquals(message, alert.getMessage());
        assertEquals(value, alert.getValue().orElseThrow());
    }

    /**
     * Should reject restoring a stop alert without an identifier.
     */
    @Test
    void restoreConstructorRejectsNullId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new StopAlert(
                        null,
                        new StopAlertType(VEHICLE_FULL_TYPE_VALUE),
                        new StopAlertMessage(VEHICLE_FULL_MESSAGE_VALUE),
                        null));
        assertEquals(STOP_ALERT_ID_REQUIRED_MESSAGE, exception.getMessage());
    }

    /**
     * Should create a stop alert from raw values without a numeric value.
     */
    @Test
    void fromValuesWithoutNumericValue() {
        StopAlert alert = StopAlert.fromValues(
                CONTAINER_OVERFLOWED_TYPE_VALUE,
                CONTAINER_OVERFLOWED_MESSAGE_VALUE);
        assertEquals(new StopAlertType(CONTAINER_OVERFLOWED_TYPE_VALUE), alert.getType());
        assertTrue(alert.getValue().isEmpty());
    }

    /**
     * Should assign an identifier when creating a stop alert.
     */
    @Test
    void getIdIsAssignedOnCreation() {
        StopAlert alert = new StopAlert(
                new StopAlertType(VEHICLE_FULL_TYPE_VALUE),
                new StopAlertMessage(VEHICLE_FULL_MESSAGE_VALUE));
        assertNotNull(alert.getId());
    }

    /**
     * Should include alert details in the string representation when a value exists.
     */
    @Test
    void toStringWithValue() {
        StopAlert alert = StopAlert.fromValues(
                VEHICLE_FULL_TYPE_VALUE,
                VEHICLE_FULL_MESSAGE_VALUE,
                ALERT_VALUE);
        String result = alert.toString();
        assertTrue(result.contains(VEHICLE_FULL_TYPE_VALUE));
        assertTrue(result.contains(VEHICLE_FULL_MESSAGE_VALUE));
        assertTrue(result.contains(VALUE_FIELD_TEXT));
    }

    /**
     * Should omit the value field in the string representation when no value exists.
     */
    @Test
    void toStringWithoutValue() {
        StopAlert alert = StopAlert.fromValues(
                CONTAINER_OVERFLOWED_TYPE_VALUE,
                CONTAINER_OVERFLOWED_MESSAGE_VALUE);
        String result = alert.toString();
        assertTrue(result.contains(CONTAINER_OVERFLOWED_TYPE_VALUE));
        assertFalse(result.contains(VALUE_FIELD_TEXT));
    }

    /**
     * Should compare stop alerts by their domain values.
     */
    @Test
    void equalsAndHashCode() {
        StopAlert alert = StopAlert.fromValues(
                VEHICLE_FULL_TYPE_VALUE,
                VEHICLE_FULL_MESSAGE_VALUE,
                ALERT_VALUE);
        StopAlert same = new StopAlert(alert);
        StopAlert different = StopAlert.fromValues(
                CONTAINER_OVERFLOWED_TYPE_VALUE,
                CONTAINER_OVERFLOWED_MESSAGE_VALUE,
                ALTERNATIVE_ALERT_VALUE);
        assertTrue(alert.equals(alert));
        assertFalse(alert.equals(null));
        assertFalse(alert.equals(new Object()));
        assertEquals(alert, same);
        assertEquals(alert.hashCode(), same.hashCode());
        assertNotEquals(alert, different);
        assertNotEquals(alert.hashCode(), different.hashCode());
    }
}
