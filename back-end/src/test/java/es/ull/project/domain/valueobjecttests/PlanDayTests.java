package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.time.PlanDay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class PlanDayTests {

    private static final String INVALID_PLAN_DAY_MESSAGE = "Plan day must be a non-negative integer";

    /**
     * Verifies that a valid plan day is stored correctly.
     */
    @Test
    void constructorValidValue() {
        PlanDay day = new PlanDay(1);
        assertEquals(1, day.getDay());
    }

    /**
     * Verifies that a valid plan day can be created from an integer.
     */
    @Test
    void fromIntegerValidValue() {
        PlanDay day = PlanDay.fromInteger(1);
        assertEquals(1, day.getDay());
    }

    /**
     * Verifies that null plan days are rejected.
     */
    @Test
    void constructorNullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new PlanDay(null));
        assertEquals(INVALID_PLAN_DAY_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that negative plan days are rejected.
     */
    @Test
    void constructorNegativeValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new PlanDay(-1));
        assertEquals(INVALID_PLAN_DAY_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that plan days with equal values are equal and share the same hash code.
     */
    @Test
    void equalsSameValue() {
        PlanDay firstDay = new PlanDay(1);
        PlanDay secondDay = new PlanDay(1);
        assertEquals(firstDay, secondDay);
        assertEquals(firstDay.hashCode(), secondDay.hashCode());
    }

    /**
     * Verifies that plan days with different values are not equal.
     */
    @Test
    void equalsDifferentValue() {
        PlanDay firstDay = new PlanDay(1);
        PlanDay secondDay = new PlanDay(2);
        assertNotEquals(firstDay, secondDay);
    }
}
