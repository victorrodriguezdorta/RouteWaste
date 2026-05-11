package es.ull.project.domain.enumerate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ContainerStatusTests {

    @Test
    void fromString() {
        ContainerStatus expected = ContainerStatus.random();
        ContainerStatus actual = ContainerStatus.fromString(expected.name());

        assertNotNull(actual);
        assertEquals(expected, actual);

        assertEquals(ContainerStatus.CORRECT, ContainerStatus.fromString(null));
        assertEquals(ContainerStatus.CORRECT, ContainerStatus.fromString("NOT_A_STATUS"));
    }

    @Test
    void random() {
        ContainerStatus status = ContainerStatus.random();

        assertNotNull(status);
        assertTrue(status instanceof ContainerStatus);
        assertFalse(status.name().isBlank());
    }
}
