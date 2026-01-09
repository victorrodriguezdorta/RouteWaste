package es.ull.project.domain.enumerate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WasteTypeTests {

    /**
     * fromString()
     */
    @Test
    public void fromString() {
        final String value = WasteType.random().name();
        WasteType type = WasteType.fromString(value);

        assertNotNull(type);
        assertTrue(type instanceof WasteType);

        assertThrows(
                IllegalArgumentException.class,
                () -> WasteType.fromString("NOT_A_TYPE")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> WasteType.fromString(null)
        );
    }

    /**
     * indexOf()
     */
    @Test
    public void indexOf() {
        final String value = WasteType.random().name();
        int index = WasteType.indexOf(value);

        assertTrue(0 <= index && index < WasteType.values().length);

        assertThrows(
                IllegalArgumentException.class,
                () -> WasteType.indexOf("INVALID_TYPE")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> WasteType.indexOf(null)
        );
    }

    /**
     * isValid() - valid value
     */
    @Test
    public void isValid_validValue() {
        final String value = WasteType.random().name();
        assertTrue(WasteType.isValid(value));
    }

    /**
     * isValid() - not valid value
     */
    @Test
    public void isValid_notValidValue() {
        assertFalse(WasteType.isValid(null));
        assertFalse(WasteType.isValid("INVALID_TYPE"));
    }

    /**
     * random()
     */
    @Test
    public void random() {
        WasteType type = WasteType.random();

        assertNotNull(type);
        assertTrue(type instanceof WasteType);
    }
}
