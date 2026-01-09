package es.ull.project.domain.enumerate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FacilityStatusTests {

    /**
     * fromString()
     */
    @Test
    public void fromString() {
        final String value = FacilityStatus.random().name();
        FacilityStatus status = FacilityStatus.fromString(value);

        assertNotNull(status);
        assertTrue(status instanceof FacilityStatus);

        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityStatus.fromString("NOT_A_STATUS"));

        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityStatus.fromString(null));
    }

    /**
     * indexOf()
     */
    @Test
    public void indexOf() {
        final String value = FacilityStatus.random().name();
        int index = FacilityStatus.indexOf(value);

        assertTrue(0 <= index && index < FacilityStatus.values().length);

        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityStatus.indexOf("INVALID_STATUS"));

        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityStatus.indexOf(null));
    }

    /**
     * isValid() - valid value
     */
    @Test
    public void isValid_validValue() {
        final String value = FacilityStatus.random().name();
        assertTrue(FacilityStatus.isValid(value));
    }

    /**
     * isValid() - not valid value
     */
    @Test
    public void isValid_notValidValue() {
        assertFalse(FacilityStatus.isValid(null));
        assertFalse(FacilityStatus.isValid("INVALID_STATUS"));
    }

    /**
     * random()
     */
    @Test
    public void random() {
        FacilityStatus status = FacilityStatus.random();

        assertNotNull(status);
        assertTrue(status instanceof FacilityStatus);
    }

    /**
     * allowsServiceAssignments()
     */
    @Test
    public void allowsServiceAssignments() {
        assertTrue(FacilityStatus.PLANNED.allowsServiceAssignments());
        assertTrue(FacilityStatus.OPEN.allowsServiceAssignments());

        assertFalse(FacilityStatus.CANDIDATE.allowsServiceAssignments());
        assertFalse(FacilityStatus.DISCARDED.allowsServiceAssignments());
    }

    /**
     * isDiscarded()
     */
    @Test
    public void isDiscarded() {
        assertTrue(FacilityStatus.DISCARDED.isDiscarded());

        assertFalse(FacilityStatus.CANDIDATE.isDiscarded());
        assertFalse(FacilityStatus.PLANNED.isDiscarded());
        assertFalse(FacilityStatus.OPEN.isDiscarded());
    }
}