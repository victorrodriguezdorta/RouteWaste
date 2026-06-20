package es.ull.project.domain.enumeratetests;

import es.ull.project.domain.enumerate.FacilityStatus;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class FacilityStatusTests {

    private static final int MINIMUM_VALID_INDEX = 0;
    private static final String NOT_A_STATUS_NAME = "NOT_A_STATUS";
    private static final String INVALID_STATUS_NAME = "INVALID_STATUS";

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
                () -> FacilityStatus.fromString(NOT_A_STATUS_NAME));
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
        assertTrue(MINIMUM_VALID_INDEX <= index && index < FacilityStatus.values().length);
        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityStatus.indexOf(INVALID_STATUS_NAME));
        assertThrows(
                IllegalArgumentException.class,
                () -> FacilityStatus.indexOf(null));
    }

    /**
     * isValid()
     */
    @Test
    void isValid() {
        final String value = FacilityStatus.random().name();
        assertTrue(FacilityStatus.isValid(value));
        assertFalse(FacilityStatus.isValid(null));
        assertFalse(FacilityStatus.isValid(INVALID_STATUS_NAME));
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