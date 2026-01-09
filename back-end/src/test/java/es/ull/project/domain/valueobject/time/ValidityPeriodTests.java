package es.ull.project.domain.valueobject.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ValidityPeriodTests {

    /**
     * Constructor - valid value (only start date)
     */
    @Test
    void constructor_validValue_startOnly() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        ValidityPeriod period = new ValidityPeriod(start);

        assertEquals(start, period.getStartDate());
        assertTrue(period.getEndDate().isEmpty());
        assertTrue(period.isOpenEnded());
    }

    /**
     * Constructor - valid value (start + end)
     */
    @Test
    void constructor_validValue_startAndEnd() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        ValidityPeriod period = new ValidityPeriod(start, end);

        assertEquals(start, period.getStartDate());
        assertEquals(Optional.of(end), period.getEndDate());
        assertFalse(period.isOpenEnded());
    }

    /**
     * Constructor - start date null
     */
    @Test
    void constructor_notDefinedValue_startDate() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ValidityPeriod(null)
        );
        assertEquals("Start date of validity period is not defined", exception.getMessage());
    }

    /**
     * Constructor - end date before start date
     */
    @Test
    void constructor_endBeforeStart() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ValidityPeriod(start, end)
        );
        assertEquals("End date of validity period cannot be before start date", exception.getMessage());
    }

    /**
     * contains() method
     */
    @Test
    void containsMethod() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        ValidityPeriod period = new ValidityPeriod(start, end);

        assertTrue(period.contains(LocalDate.of(2026, 6, 1)));
        assertTrue(period.contains(start));
        assertTrue(period.contains(end));
        assertFalse(period.contains(LocalDate.of(2025, 12, 31)));
        assertFalse(period.contains(LocalDate.of(2027, 1, 1)));
        assertFalse(period.contains(null));
    }

    /**
     * isOpenEnded() for open-ended period
     */
    @Test
    void isOpenEndedMethod() {
        ValidityPeriod period = new ValidityPeriod(LocalDate.of(2026, 1, 1));
        assertTrue(period.isOpenEnded());

        ValidityPeriod period2 = new ValidityPeriod(LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31));
        assertFalse(period2.isOpenEnded());
    }

    /**
     * equals() method
     */
    @Test
    void equalsMethod() {
        ValidityPeriod p1 = new ValidityPeriod(LocalDate.of(2026, 1, 1));
        ValidityPeriod p2 = new ValidityPeriod(LocalDate.of(2026, 1, 1));
        ValidityPeriod p3 = new ValidityPeriod(LocalDate.of(2026, 2, 1));

        assertEquals(p1, p1);      // same object
        assertNotEquals(p1, null); // null
        assertNotEquals(p1, "test"); // different class
        assertEquals(p1, p2);      // same value
        assertNotEquals(p1, p3);   // different start date
    }

    /**
     * hashCode() method
     */
    @Test
    void hashCodeMethod() {
        ValidityPeriod p1 = new ValidityPeriod(LocalDate.of(2026, 1, 1));
        ValidityPeriod p2 = new ValidityPeriod(LocalDate.of(2026, 1, 1));
        ValidityPeriod p3 = new ValidityPeriod(LocalDate.of(2026, 12, 31));

        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
    }

    /**
     * toString() method
     */
    @Test
    void toStringMethod() {
        ValidityPeriod openPeriod = new ValidityPeriod(LocalDate.of(2026, 1, 1));
        assertEquals("ValidityPeriod={startDate=2026-01-01, endDate=open-ended}", openPeriod.toString());

        ValidityPeriod closedPeriod = new ValidityPeriod(LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31));
        assertEquals("ValidityPeriod={startDate=2026-01-01, endDate=2026-12-31}", closedPeriod.toString());
    }
}
