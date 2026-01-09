package es.ull.project.domain.valueobject.time;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlanningPeriodTests {

    /**
     * Constructor - valid value
     */
    @Test
    void constructor_validValue() {
        PlanningPeriod period1 = new PlanningPeriod("2026");
        PlanningPeriod period2 = new PlanningPeriod("2026-Q1");

        assertEquals("2026", period1.getValue());
        assertEquals("2026-Q1", period2.getValue());
    }

    /**
     * Constructor - not defined (null)
     */
    @Test
    void constructor_notDefinedValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new PlanningPeriod(null)
        );
        assertEquals("Planning period is not defined", exception.getMessage());
    }

    /**
     * Constructor - wrong format
     */
    @Test
    void constructor_wrongFormatValue() {
        IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> new PlanningPeriod("20Q6")
        );
        assertEquals("Planning period format is invalid. Expected formats: YYYY or YYYY-Q[1-4]", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> new PlanningPeriod("2026-Q5")
        );
        assertEquals("Planning period format is invalid. Expected formats: YYYY or YYYY-Q[1-4]", exception2.getMessage());
    }

    /**
     * Getter
     */
    @Test
    void getter_value() {
        PlanningPeriod period = new PlanningPeriod("2026-Q3");
        assertEquals("2026-Q3", period.getValue());
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        PlanningPeriod p1 = new PlanningPeriod("2026-Q2");
        PlanningPeriod p2 = new PlanningPeriod("2026-Q2");
        PlanningPeriod p3 = new PlanningPeriod("2026-Q3");

        assertEquals(p1, p1);           // same object
        assertNotEquals(p1, null);      // null
        assertNotEquals(p1, "2026-Q2"); // different class
        assertEquals(p1, p2);           // same value
        assertNotEquals(p1, p3);        // different value
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        PlanningPeriod p1 = new PlanningPeriod("2026-Q1");
        PlanningPeriod p2 = new PlanningPeriod("2026-Q1");
        PlanningPeriod p3 = new PlanningPeriod("2026-Q4");

        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        PlanningPeriod period = new PlanningPeriod("2026-Q2");
        assertEquals("PlanningPeriod={value='2026-Q2'}", period.toString());
    }
}
