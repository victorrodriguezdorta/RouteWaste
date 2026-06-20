package es.ull.project.domain.enumeratetests;

import es.ull.project.domain.enumerate.InfrastructurePlanValidityState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class InfrastructurePlanValidityStateTests {

    private static final String INVALID_VALIDITY_STATE = "INVALID";

    /**
     * Should convert a valid state name and reject invalid names.
     */
    @Test
    void fromString() {
        InfrastructurePlanValidityState expected = InfrastructurePlanValidityState.random();
        assertEquals(expected, InfrastructurePlanValidityState.fromString(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanValidityState.fromString(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> InfrastructurePlanValidityState.fromString(INVALID_VALIDITY_STATE));
    }

    /**
     * Should return the ordinal index for a valid state name and reject invalid names.
     */
    @Test
    void indexOf() {
        InfrastructurePlanValidityState expected = InfrastructurePlanValidityState.random();
        assertEquals(expected.ordinal(), InfrastructurePlanValidityState.indexOf(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanValidityState.indexOf(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> InfrastructurePlanValidityState.indexOf(INVALID_VALIDITY_STATE));
    }

    /**
     * Should report whether a validity state name can be parsed.
     */
    @Test
    void isValid() {
        assertTrue(InfrastructurePlanValidityState.isValid(InfrastructurePlanValidityState.random().name()));
        assertFalse(InfrastructurePlanValidityState.isValid(null));
        assertFalse(InfrastructurePlanValidityState.isValid(INVALID_VALIDITY_STATE));
    }

    /**
     * Should return a random infrastructure plan validity state.
     */
    @Test
    void random() {
        InfrastructurePlanValidityState state = InfrastructurePlanValidityState.random();
        assertNotNull(state);
        assertTrue(state instanceof InfrastructurePlanValidityState);
    }
}
