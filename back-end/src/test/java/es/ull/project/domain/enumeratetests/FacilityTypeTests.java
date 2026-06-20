package es.ull.project.domain.enumeratetests;

import es.ull.project.domain.enumerate.FacilityType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class FacilityTypeTests {

    private static final int MINIMUM_VALID_INDEX = 0;
    private static final String NOT_A_TYPE_NAME = "NOT_A_TYPE";
    private static final String INVALID_TYPE_NAME = "INVALID_TYPE";

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
                () -> FacilityType.fromString(NOT_A_TYPE_NAME));
        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityType.fromString(null));
    }

    /**
     * indexOf()
     */
    @Test
    public void indexOf() {
        final String value = FacilityType.random().name();
        int index = FacilityType.indexOf(value);
        assertTrue(MINIMUM_VALID_INDEX <= index && index < FacilityType.values().length);
        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityType.indexOf(INVALID_TYPE_NAME));
        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityType.indexOf(null));
    }

    /**
     * isValid()
     */
    @Test
    void isValid() {
        final String value = FacilityType.random().name();
        assertTrue(FacilityType.isValid(value));
        assertFalse(FacilityType.isValid(null));
        assertFalse(FacilityType.isValid(INVALID_TYPE_NAME));
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
