package es.ull.project.domain.enumeratetests;

import es.ull.project.domain.enumerate.InfrastructurePlanExecutionState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class InfrastructurePlanExecutionStateTests {

    private static final String INVALID_EXECUTION_STATE = "INVALID";
    private static final String EMPTY_STORED_VALUE = "";
    private static final String UNKNOWN_STORED_VALUE = "unknown";
    private static final String RUNNING_STORED_VALUE = "RUNNING";

    /**
     * Should convert a valid state name and reject invalid names.
     */
    @Test
    void fromString() {
        InfrastructurePlanExecutionState expected = InfrastructurePlanExecutionState.random();
        assertEquals(expected, InfrastructurePlanExecutionState.fromString(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanExecutionState.fromString(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> InfrastructurePlanExecutionState.fromString(INVALID_EXECUTION_STATE));
    }

    /**
     * Should default to completed for legacy stored values and parse known states.
     */
    @Test
    void fromStoredStringDefaultsToCompletedForLegacyDocuments() {
        assertEquals(
                InfrastructurePlanExecutionState.COMPLETED,
                InfrastructurePlanExecutionState.fromStoredString(null));
        assertEquals(
                InfrastructurePlanExecutionState.COMPLETED,
                InfrastructurePlanExecutionState.fromStoredString(EMPTY_STORED_VALUE));
        assertEquals(
                InfrastructurePlanExecutionState.COMPLETED,
                InfrastructurePlanExecutionState.fromStoredString(UNKNOWN_STORED_VALUE));
        assertEquals(
                InfrastructurePlanExecutionState.RUNNING,
                InfrastructurePlanExecutionState.fromStoredString(RUNNING_STORED_VALUE));
    }

    /**
     * Should return the ordinal index for a valid state name and reject invalid names.
     */
    @Test
    void indexOf() {
        InfrastructurePlanExecutionState expected = InfrastructurePlanExecutionState.random();
        assertEquals(expected.ordinal(), InfrastructurePlanExecutionState.indexOf(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanExecutionState.indexOf(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> InfrastructurePlanExecutionState.indexOf(INVALID_EXECUTION_STATE));
    }

    /**
     * Should report whether an execution state name can be parsed.
     */
    @Test
    void isValid() {
        assertTrue(InfrastructurePlanExecutionState.isValid(InfrastructurePlanExecutionState.random().name()));
        assertFalse(InfrastructurePlanExecutionState.isValid(null));
        assertFalse(InfrastructurePlanExecutionState.isValid(INVALID_EXECUTION_STATE));
    }

    /**
     * Should return a random infrastructure plan execution state.
     */
    @Test
    void random() {
        InfrastructurePlanExecutionState state = InfrastructurePlanExecutionState.random();
        assertNotNull(state);
    }
}
