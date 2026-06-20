package es.ull.project.domain.enumeratetests;

import es.ull.project.domain.enumerate.StopType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class StopTypeTests {

    private static final String INVALID_STOP_TYPE_NAME = "INVALID";

    /**
     * Verifies that stop types can be parsed from valid names and reject invalid names.
     */
    @Test
    void fromString() {
        StopType expected = StopType.random();
        assertEquals(expected, StopType.fromString(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> StopType.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> StopType.fromString(INVALID_STOP_TYPE_NAME));
    }

    /**
     * Verifies that stop type names resolve to their enum ordinal.
     */
    @Test
    void indexOf() {
        StopType expected = StopType.random();
        assertEquals(expected.ordinal(), StopType.indexOf(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> StopType.indexOf(null));
        assertThrows(IllegalArgumentException.class, () -> StopType.indexOf(INVALID_STOP_TYPE_NAME));
    }

    /**
     * Verifies stop type name validation for valid, null, and invalid values.
     */
    @Test
    void isValid() {
        assertTrue(StopType.isValid(StopType.random().name()));
        assertFalse(StopType.isValid(null));
        assertFalse(StopType.isValid(INVALID_STOP_TYPE_NAME));
    }

    /**
     * Verifies that random returns a stop type.
     */
    @Test
    void random() {
        StopType type = StopType.random();
        assertNotNull(type);
        assertTrue(type instanceof StopType);
    }
}
