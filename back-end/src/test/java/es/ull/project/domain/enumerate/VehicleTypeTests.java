package es.ull.project.domain.enumerate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleTypeTests {

    /**
     * fromString()
     */
    @Test
    public void fromString() {
        final String value = VehicleType.random().name();
        VehicleType type = VehicleType.fromString(value);

        assertNotNull(type);
        assertTrue(type instanceof VehicleType);

        assertThrows(
                IllegalArgumentException.class,
                () -> VehicleType.fromString("NOT_A_TYPE")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> VehicleType.fromString(null)
        );
    }

    /**
     * indexOf()
     */
    @Test
    public void indexOf() {
        final String value = VehicleType.random().name();
        int index = VehicleType.indexOf(value);

        assertTrue(0 <= index && index < VehicleType.values().length);

        assertThrows(
                IllegalArgumentException.class,
                () -> VehicleType.indexOf("INVALID_TYPE")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> VehicleType.indexOf(null)
        );
    }

    /**
     * isValid() - valid value
     */
    @Test
    public void isValid_validValue() {
        final String value = VehicleType.random().name();
        assertTrue(VehicleType.isValid(value));
    }

    /**
     * isValid() - not valid value
     */
    @Test
    public void isValid_notValidValue() {
        assertFalse(VehicleType.isValid(null));
        assertFalse(VehicleType.isValid("INVALID_TYPE"));
    }

    /**
     * random()
     */
    @Test
    public void random() {
        VehicleType type = VehicleType.random();

        assertNotNull(type);
        assertTrue(type instanceof VehicleType);
    }
}
