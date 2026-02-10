package es.ull.project.domain.enumerate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceZoneTests {

    @Test
    public void fromString() {
        final String value = ServiceZone.random().name();
        ServiceZone zone = ServiceZone.fromString(value);
        assertNotNull(zone);
        assertTrue(zone instanceof ServiceZone);

        assertThrows(
                IllegalArgumentException.class,
                () -> ServiceZone.fromString("NOT_A_ZONE")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> ServiceZone.fromString(null)
        );
    }

    @Test
    public void indexOf() {
        final String value = ServiceZone.random().name();
        int index = ServiceZone.indexOf(value);

        assertTrue(0 <= index && index < ServiceZone.values().length);

        assertThrows(
                IllegalArgumentException.class,
                () -> ServiceZone.indexOf("INVALID_ZONE")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> ServiceZone.indexOf(null)
        );
    }

    @Test
    public void isValid_validValue() {
        final String value = ServiceZone.random().name();
        assertTrue(ServiceZone.isValid(value));
    }

    @Test
    public void isValid_notValidValue() {
        assertFalse(ServiceZone.isValid(null));
        assertFalse(ServiceZone.isValid("INVALID_ZONE"));
    }

    @Test
    public void random() {
        ServiceZone zone = ServiceZone.random();

        assertNotNull(zone);
        assertTrue(zone instanceof ServiceZone);
    }
}

