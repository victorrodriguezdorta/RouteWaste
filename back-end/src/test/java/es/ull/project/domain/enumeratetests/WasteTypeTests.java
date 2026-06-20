package es.ull.project.domain.enumeratetests;

import es.ull.project.domain.enumerate.WasteType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class WasteTypeTests {

    private static final String NOT_A_TYPE_NAME = "NOT_A_TYPE";
    private static final String INVALID_TYPE_NAME = "INVALID_TYPE";
    private static final int MINIMUM_VALID_INDEX = 0;

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
                () -> WasteType.fromString(NOT_A_TYPE_NAME)
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
        assertTrue(MINIMUM_VALID_INDEX <= index && index < WasteType.values().length);
        assertThrows(
                IllegalArgumentException.class,
                () -> WasteType.indexOf(INVALID_TYPE_NAME)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> WasteType.indexOf(null)
        );
    }

    /**
     * isValid()
     */
    @Test
    void isValid() {
        final String value = WasteType.random().name();
        assertTrue(WasteType.isValid(value));
        assertFalse(WasteType.isValid(null));
        assertFalse(WasteType.isValid(INVALID_TYPE_NAME));
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
