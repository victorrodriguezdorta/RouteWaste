package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.infrastructureplan.InfrastructurePlanFailureReason;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class InfrastructurePlanFailureReasonTests {

    private static final String VALID_FAILURE_REASON = "Docker timeout";
    private static final String TIMEOUT_FAILURE_REASON = "timeout";
    private static final String BLANK_FAILURE_REASON = " ";
    private static final String WHITESPACE_FAILURE_REASON = "  ";
    private static final String REASON_NOT_DEFINED_MESSAGE =
            "Infrastructure plan failure reason must not be null or blank";

    /**
     * Verifies that a valid infrastructure plan failure reason is stored correctly.
     */
    @Test
    void constructorValidReason() {
        InfrastructurePlanFailureReason reason = new InfrastructurePlanFailureReason(VALID_FAILURE_REASON);
        assertEquals(VALID_FAILURE_REASON, reason.getValue());
    }

    /**
     * Verifies that blank infrastructure plan failure reasons are rejected.
     */
    @Test
    void constructorBlankReason() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlanFailureReason(BLANK_FAILURE_REASON));
        assertEquals(REASON_NOT_DEFINED_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that nullable parsing returns null for blank input.
     */
    @Test
    void fromNullableNullForBlankInput() {
        assertNull(InfrastructurePlanFailureReason.fromNullable(null));
        assertNull(InfrastructurePlanFailureReason.fromNullable(WHITESPACE_FAILURE_REASON));
    }

    /**
     * Verifies that nullable parsing returns a value object for valid text.
     */
    @Test
    void fromNullableValueObjectForText() {
        InfrastructurePlanFailureReason reason =
                InfrastructurePlanFailureReason.fromNullable(TIMEOUT_FAILURE_REASON);
        assertEquals(TIMEOUT_FAILURE_REASON, reason.getValue());
    }
}
