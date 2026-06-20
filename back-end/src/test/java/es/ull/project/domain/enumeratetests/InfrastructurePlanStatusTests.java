package es.ull.project.domain.enumeratetests;

import es.ull.project.domain.enumerate.InfrastructurePlanStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class InfrastructurePlanStatusTests {

    private static final String INVALID_STATUS_NAME = "INVALID";

    /**
     * Verifies conversion from valid names and rejection of invalid names.
     */
    @Test
    void fromString() {
        InfrastructurePlanStatus expected = InfrastructurePlanStatus.random();
        assertEquals(expected, InfrastructurePlanStatus.fromString(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanStatus.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanStatus.fromString(INVALID_STATUS_NAME));
    }

    /**
     * Verifies index lookup for valid names and rejection of invalid names.
     */
    @Test
    void indexOf() {
        InfrastructurePlanStatus expected = InfrastructurePlanStatus.random();
        assertEquals(expected.ordinal(), InfrastructurePlanStatus.indexOf(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanStatus.indexOf(null));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanStatus.indexOf(INVALID_STATUS_NAME));
    }

    /**
     * Verifies validity checks for valid, null, and invalid status names.
     */
    @Test
    void isValid() {
        assertTrue(InfrastructurePlanStatus.isValid(InfrastructurePlanStatus.random().name()));
        assertFalse(InfrastructurePlanStatus.isValid(null));
        assertFalse(InfrastructurePlanStatus.isValid(INVALID_STATUS_NAME));
    }

    /**
     * Verifies that random returns a valid infrastructure plan status.
     */
    @Test
    void random() {
        InfrastructurePlanStatus status = InfrastructurePlanStatus.random();
        assertNotNull(status);
        assertTrue(status instanceof InfrastructurePlanStatus);
    }
}
