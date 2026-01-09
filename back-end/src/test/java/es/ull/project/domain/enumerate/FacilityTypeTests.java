package es.ull.project.domain.enumerate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FacilityTypeTests {

    /**
     * fromString()
     */
    @Test
    public void fromString() {
        final String value = FacilityType.random().name();
        FacilityType type = FacilityType.fromString(value);

        assertNotNull(type);
        assertTrue(type instanceof FacilityType);

        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityType.fromString("NOT_A_TYPE")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityType.fromString(null)
        );
    }

    /**
     * indexOf()
     */
    @Test
    public void indexOf() {
        final String value = FacilityType.random().name();
        int index = FacilityType.indexOf(value);

        assertTrue(0 <= index && index < FacilityType.values().length);

        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityType.indexOf("INVALID_TYPE")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityType.indexOf(null)
        );
    }

    /**
     * isValid() - valid value
     */
    @Test
    public void isValid_validValue() {
        final String value = FacilityType.random().name();
        assertTrue(FacilityType.isValid(value));
    }

    /**
     * isValid() - not valid value
     */
    @Test
    public void isValid_notValidValue() {
        assertFalse(FacilityType.isValid(null));
        assertFalse(FacilityType.isValid("INVALID_TYPE"));
    }

    /**
     * random()
     */
    @Test
    public void random() {
        FacilityType type = FacilityType.random();

        assertNotNull(type);
        assertTrue(type instanceof FacilityType);
    }
}
