package es.ull.project.domain.enumerate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StopTypeTests {

    @Test
    void fromString() {
        StopType expected = StopType.random();

        assertEquals(expected, StopType.fromString(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> StopType.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> StopType.fromString("INVALID"));
    }

    @Test
    void indexOf() {
        StopType expected = StopType.random();

        assertEquals(expected.ordinal(), StopType.indexOf(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> StopType.indexOf(null));
        assertThrows(IllegalArgumentException.class, () -> StopType.indexOf("INVALID"));
    }

    @Test
    void isValid() {
        assertTrue(StopType.isValid(StopType.random().name()));
        assertFalse(StopType.isValid(null));
        assertFalse(StopType.isValid("INVALID"));
    }

    @Test
    void random() {
        StopType type = StopType.random();

        assertNotNull(type);
        assertTrue(type instanceof StopType);
    }
}
