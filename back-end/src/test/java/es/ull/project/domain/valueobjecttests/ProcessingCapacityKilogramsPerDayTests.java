package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ProcessingCapacityKilogramsPerDayTests {

    private static final String EXPECTED_TO_STRING_CAPACITY = "500";

    /**
     * Verifies that a valid processing capacity value is stored in kilograms per day.
     */
    @Test
    void constructorValidValue() {
        ProcessingCapacityKilogramsPerDay cap = new ProcessingCapacityKilogramsPerDay(500.0);
        assertEquals(500.0, cap.getKilogramsPerDay());
    }

    /**
     * Verifies that zero kilograms per day is accepted as processing capacity.
     */
    @Test
    void constructorZero() {
        ProcessingCapacityKilogramsPerDay cap = new ProcessingCapacityKilogramsPerDay(0.0);
        assertEquals(0.0, cap.getKilogramsPerDay());
    }

    /**
     * Verifies that negative processing capacity values are rejected.
     */
    @Test
    void constructorNegativeValue() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProcessingCapacityKilogramsPerDay(-1.0));
    }

    /**
     * Verifies that setting kilograms per day returns a new instance with the updated value.
     */
    @Test
    void setKilogramsPerDayReturnsNewInstance() {
        ProcessingCapacityKilogramsPerDay original = new ProcessingCapacityKilogramsPerDay(500.0);
        ProcessingCapacityKilogramsPerDay updated = original.setKilogramsPerDay(1000.0);
        assertNotSame(original, updated);
        assertEquals(500.0, original.getKilogramsPerDay());
        assertEquals(1000.0, updated.getKilogramsPerDay());
    }

    /**
     * Verifies that greaterThan compares two processing capacities correctly.
     */
    @Test
    void greaterThanValid() {
        ProcessingCapacityKilogramsPerDay big = new ProcessingCapacityKilogramsPerDay(1000.0);
        ProcessingCapacityKilogramsPerDay small = new ProcessingCapacityKilogramsPerDay(500.0);
        assertTrue(big.greaterThan(small));
        assertFalse(small.greaterThan(big));
    }

    /**
     * Verifies that greaterThanOrEqual accepts equal processing capacities.
     */
    @Test
    void greaterThanOrEqualValid() {
        ProcessingCapacityKilogramsPerDay a = new ProcessingCapacityKilogramsPerDay(500.0);
        ProcessingCapacityKilogramsPerDay b = new ProcessingCapacityKilogramsPerDay(500.0);
        assertTrue(a.greaterThanOrEqual(b));
    }

    /**
     * Verifies that lessThan compares two processing capacities correctly.
     */
    @Test
    void lessThanValid() {
        ProcessingCapacityKilogramsPerDay small = new ProcessingCapacityKilogramsPerDay(200.0);
        ProcessingCapacityKilogramsPerDay big = new ProcessingCapacityKilogramsPerDay(500.0);
        assertTrue(small.lessThan(big));
        assertFalse(big.lessThan(small));
    }

    /**
     * Verifies that greaterThan rejects a null comparison value.
     */
    @Test
    void greaterThanNullThrows() {
        ProcessingCapacityKilogramsPerDay cap = new ProcessingCapacityKilogramsPerDay(500.0);
        assertThrows(IllegalArgumentException.class, () -> cap.greaterThan(null));
    }

    /**
     * Verifies equality for matching and different processing capacities.
     */
    @Test
    void equalsMethod() {
        ProcessingCapacityKilogramsPerDay a = new ProcessingCapacityKilogramsPerDay(500.0);
        ProcessingCapacityKilogramsPerDay b = new ProcessingCapacityKilogramsPerDay(500.0);
        ProcessingCapacityKilogramsPerDay c = new ProcessingCapacityKilogramsPerDay(999.0);
        assertEquals(a, a);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, Integer.valueOf(0));
    }

    /**
     * Verifies that equal processing capacities produce the same hash code.
     */
    @Test
    void hashCodeMethod() {
        ProcessingCapacityKilogramsPerDay a = new ProcessingCapacityKilogramsPerDay(500.0);
        ProcessingCapacityKilogramsPerDay b = new ProcessingCapacityKilogramsPerDay(500.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    /**
     * Verifies that the string representation includes the capacity value.
     */
    @Test
    void toStringMethod() {
        ProcessingCapacityKilogramsPerDay cap = new ProcessingCapacityKilogramsPerDay(500.0);
        assertTrue(cap.toString().contains(EXPECTED_TO_STRING_CAPACITY));
    }
}
