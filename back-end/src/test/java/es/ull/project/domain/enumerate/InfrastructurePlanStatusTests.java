package es.ull.project.domain.enumerate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InfrastructurePlanStatusTests {

    @Test
    void fromString() {
        InfrastructurePlanStatus expected = InfrastructurePlanStatus.random();

        assertEquals(expected, InfrastructurePlanStatus.fromString(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanStatus.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanStatus.fromString("INVALID"));
    }

    @Test
    void indexOf() {
        InfrastructurePlanStatus expected = InfrastructurePlanStatus.random();

        assertEquals(expected.ordinal(), InfrastructurePlanStatus.indexOf(expected.name()));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanStatus.indexOf(null));
        assertThrows(IllegalArgumentException.class, () -> InfrastructurePlanStatus.indexOf("INVALID"));
    }

    @Test
    void isValid() {
        assertTrue(InfrastructurePlanStatus.isValid(InfrastructurePlanStatus.random().name()));
        assertFalse(InfrastructurePlanStatus.isValid(null));
        assertFalse(InfrastructurePlanStatus.isValid("INVALID"));
    }

    @Test
    void random() {
        InfrastructurePlanStatus status = InfrastructurePlanStatus.random();

        assertNotNull(status);
        assertTrue(status instanceof InfrastructurePlanStatus);
    }
}
