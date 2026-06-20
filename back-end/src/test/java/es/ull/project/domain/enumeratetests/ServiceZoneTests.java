package es.ull.project.domain.enumeratetests;

import es.ull.project.domain.enumerate.ServiceZone;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ServiceZoneTests {

    private static final int MINIMUM_VALID_INDEX = 0;
    private static final String NOT_A_ZONE_NAME = "NOT_A_ZONE";
    private static final String INVALID_ZONE_NAME = "INVALID_ZONE";

    /**
     * Verifies that service zones can be parsed from valid names and reject invalid names.
     */
    @Test
    public void fromString() {
        final String value = ServiceZone.random().name();
        ServiceZone zone = ServiceZone.fromString(value);
        assertNotNull(zone);
        assertTrue(zone instanceof ServiceZone);
        assertThrows(
                IllegalArgumentException.class,
                () -> ServiceZone.fromString(NOT_A_ZONE_NAME)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> ServiceZone.fromString(null)
        );
    }

    /**
     * Verifies that service zone names resolve to indexes within the enum bounds.
     */
    @Test
    public void indexOf() {
        final String value = ServiceZone.random().name();
        int index = ServiceZone.indexOf(value);
        assertTrue(MINIMUM_VALID_INDEX <= index && index < ServiceZone.values().length);
        assertThrows(
                IllegalArgumentException.class,
                () -> ServiceZone.indexOf(INVALID_ZONE_NAME)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> ServiceZone.indexOf(null)
        );
    }

    /**
     * Verifies that valid service zone names are accepted and invalid names are rejected.
     */
    @Test
    void isValid() {
        final String value = ServiceZone.random().name();
        assertTrue(ServiceZone.isValid(value));
        assertFalse(ServiceZone.isValid(null));
        assertFalse(ServiceZone.isValid(INVALID_ZONE_NAME));
    }

    /**
     * Verifies that random returns a service zone.
     */
    @Test
    public void random() {
        ServiceZone zone = ServiceZone.random();
        assertNotNull(zone);
        assertTrue(zone instanceof ServiceZone);
    }
}

