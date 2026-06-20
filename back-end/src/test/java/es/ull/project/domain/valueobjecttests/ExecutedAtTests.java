package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.time.ExecutedAt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ExecutedAtTests {

    private static final String VALID_EXECUTED_AT = "2026-05-07T10:00:00Z";
    private static final String DIFFERENT_EXECUTED_AT = "2026-05-07T11:00:00Z";
    private static final String NULL_EXECUTED_AT_MESSAGE = "Execution time must not be null";
    private static final String INVALID_EXECUTED_AT_MESSAGE = "Execution time must be a valid ISO-8601 format";

    /**
     * Verifies that a valid execution timestamp is stored correctly.
     */
    @Test
    void constructorValidValue() {
        ExecutedAt executedAt = new ExecutedAt(VALID_EXECUTED_AT);
        assertEquals(VALID_EXECUTED_AT, executedAt.getTimestamp());
    }

    /**
     * Verifies that null execution timestamps are rejected.
     */
    @Test
    void constructorNullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ExecutedAt(null));
        assertEquals(NULL_EXECUTED_AT_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that blank execution timestamps are rejected.
     */
    @Test
    void constructorBlankValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ExecutedAt("  "));
        assertEquals(INVALID_EXECUTED_AT_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that non ISO-8601 execution timestamps are rejected.
     */
    @Test
    void constructorInvalidFormat() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ExecutedAt("invalid-date-format"));
        assertEquals(INVALID_EXECUTED_AT_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that execution timestamps with equal values are equal and share the same hash code.
     */
    @Test
    void equalsSameValue() {
        ExecutedAt firstExecutedAt = new ExecutedAt(VALID_EXECUTED_AT);
        ExecutedAt secondExecutedAt = new ExecutedAt(VALID_EXECUTED_AT);
        assertEquals(firstExecutedAt, secondExecutedAt);
        assertEquals(firstExecutedAt.hashCode(), secondExecutedAt.hashCode());
    }

    /**
     * Verifies that execution timestamps with different values are not equal.
     */
    @Test
    void equalsDifferentValue() {
        ExecutedAt firstExecutedAt = new ExecutedAt(VALID_EXECUTED_AT);
        ExecutedAt secondExecutedAt = new ExecutedAt(DIFFERENT_EXECUTED_AT);
        assertNotEquals(firstExecutedAt, secondExecutedAt);
    }
}
