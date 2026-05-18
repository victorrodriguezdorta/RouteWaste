package es.ull.project.domain.enumerate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InfrastructurePlanValidityStateTests {

    @Test
    void fromString() {
        InfrastructurePlanValidityState expected = InfrastructurePlanValidityState.random();

        assertEquals(expected, InfrastructurePlanValidityState.fromString(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanValidityState.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanValidityState.fromString("INVALID"));
    }

    @Test
    void indexOf() {
        InfrastructurePlanValidityState expected = InfrastructurePlanValidityState.random();

        assertEquals(expected.ordinal(), InfrastructurePlanValidityState.indexOf(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanValidityState.indexOf(null));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanValidityState.indexOf("INVALID"));
    }

    @Test
    void isValid() {
        assertTrue(InfrastructurePlanValidityState.isValid(InfrastructurePlanValidityState.random().name()));
        assertFalse(InfrastructurePlanValidityState.isValid(null));
        assertFalse(InfrastructurePlanValidityState.isValid("INVALID"));
    }

    @Test
    void random() {
        InfrastructurePlanValidityState state = InfrastructurePlanValidityState.random();

        assertNotNull(state);
        assertTrue(state instanceof InfrastructurePlanValidityState);
    }
}
