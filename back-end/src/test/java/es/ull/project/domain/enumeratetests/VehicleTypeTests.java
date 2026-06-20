package es.ull.project.domain.enumeratetests;

import es.ull.project.domain.enumerate.VehicleType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class VehicleTypeTests {

    private static final int MINIMUM_VALID_INDEX = 0;
    private static final String INVALID_TYPE_NAME = "INVALID_TYPE";
    private static final String NOT_A_TYPE_NAME = "NOT_A_TYPE";

    /**
     * fromString()
     */
    @Test
    void fromString() {
        final String value = VehicleType.random().name();
        VehicleType type = VehicleType.fromString(value);
        assertNotNull(type);
        assertTrue(type instanceof VehicleType);
        assertThrows(
                IllegalArgumentException.class,
                () -> VehicleType.fromString(NOT_A_TYPE_NAME));
        assertThrows(
                IllegalArgumentException.class,
                () -> VehicleType.fromString(null));
    }

    /**
     * indexOf()
     */
    @Test
    void indexOf() {
        final String value = VehicleType.random().name();
        int index = VehicleType.indexOf(value);
        assertTrue(MINIMUM_VALID_INDEX <= index && index < VehicleType.values().length);
        assertThrows(
                IllegalArgumentException.class,
                () -> VehicleType.indexOf(INVALID_TYPE_NAME));
        assertThrows(
                IllegalArgumentException.class,
                () -> VehicleType.indexOf(null));
    }

    /**
     * isValid()
     */
    @Test
    void isValid() {
        final String value = VehicleType.random().name();
        assertTrue(VehicleType.isValid(value));
        assertFalse(VehicleType.isValid(null));
        assertFalse(VehicleType.isValid(INVALID_TYPE_NAME));
    }

    /**
     * random()
     */
    @Test
    void random() {
        VehicleType type = VehicleType.random();
        assertNotNull(type);
        assertTrue(type instanceof VehicleType);
    }
}
