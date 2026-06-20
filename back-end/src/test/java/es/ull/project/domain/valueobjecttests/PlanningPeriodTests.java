package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.time.PlanningPeriod;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class PlanningPeriodTests {

    private static final String YEAR_PERIOD_VALUE = "2026";
    private static final String QUARTER_ONE_PERIOD_VALUE = "2026-Q1";
    private static final String QUARTER_TWO_PERIOD_VALUE = "2026-Q2";
    private static final String QUARTER_THREE_PERIOD_VALUE = "2026-Q3";
    private static final String QUARTER_FOUR_PERIOD_VALUE = "2026-Q4";
    private static final String QUARTER_FIVE_PERIOD_VALUE = "2026-Q5";
    private static final String INVALID_FORMAT_PERIOD_VALUE = "20Q6";
    private static final String PLANNING_PERIOD_NOT_DEFINED_MESSAGE = "Planning period is not defined";
    private static final String PLANNING_PERIOD_INVALID_FORMAT_MESSAGE =
            "Planning period format is invalid. Expected formats: YYYY or YYYY-Q[1-4]";
    private static final String PLANNING_PERIOD_TO_STRING =
            "PlanningPeriod={value='2026-Q2'}";

    /**
     * Should create planning periods with valid year and quarter formats.
     */
    @Test
    void constructorValidValue() {
        PlanningPeriod period1 = new PlanningPeriod(YEAR_PERIOD_VALUE);
        PlanningPeriod period2 = new PlanningPeriod(QUARTER_ONE_PERIOD_VALUE);
        assertEquals(YEAR_PERIOD_VALUE, period1.getValue());
        assertEquals(QUARTER_ONE_PERIOD_VALUE, period2.getValue());
    }

    /**
     * Should reject a null planning period value.
     */
    @Test
    void constructorNotDefinedValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new PlanningPeriod(null));
        assertEquals(PLANNING_PERIOD_NOT_DEFINED_MESSAGE, exception.getMessage());
    }

    /**
     * Should reject planning period values with invalid formats.
     */
    @Test
    void constructorWrongFormatValue() {
        IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> new PlanningPeriod(INVALID_FORMAT_PERIOD_VALUE));
        assertEquals(PLANNING_PERIOD_INVALID_FORMAT_MESSAGE, exception1.getMessage());
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> new PlanningPeriod(QUARTER_FIVE_PERIOD_VALUE));
        assertEquals(PLANNING_PERIOD_INVALID_FORMAT_MESSAGE, exception2.getMessage());
    }

    /**
     * Should return the configured planning period value.
     */
    @Test
    void getterValue() {
        PlanningPeriod period = new PlanningPeriod(QUARTER_THREE_PERIOD_VALUE);
        assertEquals(QUARTER_THREE_PERIOD_VALUE, period.getValue());
    }

    /**
     * Should compare planning periods by their value and type.
     */
    @Test
    void equalsMethod() {
        PlanningPeriod p1 = new PlanningPeriod(QUARTER_TWO_PERIOD_VALUE);
        PlanningPeriod p2 = new PlanningPeriod(QUARTER_TWO_PERIOD_VALUE);
        PlanningPeriod p3 = new PlanningPeriod(QUARTER_THREE_PERIOD_VALUE);
        assertEquals(p1, p1);
        assertNotEquals(p1, null);
        assertNotEquals(p1, QUARTER_TWO_PERIOD_VALUE);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
    }

    /**
     * Should generate consistent hash codes for equal planning periods.
     */
    @Test
    void hashCodeMethod() {
        PlanningPeriod p1 = new PlanningPeriod(QUARTER_ONE_PERIOD_VALUE);
        PlanningPeriod p2 = new PlanningPeriod(QUARTER_ONE_PERIOD_VALUE);
        PlanningPeriod p3 = new PlanningPeriod(QUARTER_FOUR_PERIOD_VALUE);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
    }

    /**
     * Should include the planning period value in the string representation.
     */
    @Test
    void toStringMethod() {
        PlanningPeriod period = new PlanningPeriod(QUARTER_TWO_PERIOD_VALUE);
        assertEquals(PLANNING_PERIOD_TO_STRING, period.toString());
    }
}
