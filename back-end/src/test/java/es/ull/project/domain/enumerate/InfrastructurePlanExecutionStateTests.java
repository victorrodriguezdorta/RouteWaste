package es.ull.project.domain.enumerate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InfrastructurePlanExecutionStateTests {

    @Test
    void fromString() {
        InfrastructurePlanExecutionState expected = InfrastructurePlanExecutionState.random();

        assertEquals(expected, InfrastructurePlanExecutionState.fromString(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanExecutionState.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanExecutionState.fromString("INVALID"));
    }

    @Test
    void fromStoredString_defaultsToCompletedForLegacyDocuments() {
        assertEquals(InfrastructurePlanExecutionState.COMPLETED, InfrastructurePlanExecutionState.fromStoredString(null));
        assertEquals(InfrastructurePlanExecutionState.COMPLETED, InfrastructurePlanExecutionState.fromStoredString(""));
        assertEquals(InfrastructurePlanExecutionState.COMPLETED, InfrastructurePlanExecutionState.fromStoredString("unknown"));
        assertEquals(InfrastructurePlanExecutionState.RUNNING, InfrastructurePlanExecutionState.fromStoredString("RUNNING"));
    }

    @Test
    void indexOf() {
        InfrastructurePlanExecutionState expected = InfrastructurePlanExecutionState.random();

        assertEquals(expected.ordinal(), InfrastructurePlanExecutionState.indexOf(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanExecutionState.indexOf(null));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanExecutionState.indexOf("INVALID"));
    }

    @Test
    void isValid() {
        assertTrue(InfrastructurePlanExecutionState.isValid(InfrastructurePlanExecutionState.random().name()));
        assertFalse(InfrastructurePlanExecutionState.isValid(null));
        assertFalse(InfrastructurePlanExecutionState.isValid("INVALID"));
    }

    @Test
    void random() {
        InfrastructurePlanExecutionState state = InfrastructurePlanExecutionState.random();
        assertNotNull(state);
    }
}
