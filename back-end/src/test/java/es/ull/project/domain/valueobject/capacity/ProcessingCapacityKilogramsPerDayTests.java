package es.ull.project.domain.valueobject.capacity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProcessingCapacityKilogramsPerDayTests {

    @Test
    void constructor_validValue() {
        ProcessingCapacityKilogramsPerDay cap = new ProcessingCapacityKilogramsPerDay(500.0);
        assertEquals(500.0, cap.getKilogramsPerDay());
    }

    @Test
    void constructor_zero() {
        ProcessingCapacityKilogramsPerDay cap = new ProcessingCapacityKilogramsPerDay(0.0);
        assertEquals(0.0, cap.getKilogramsPerDay());
    }

    @Test
    void constructor_negativeValue() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProcessingCapacityKilogramsPerDay(-1.0));
    }

    @Test
    void setKilogramsPerDay_returnsNewInstance() {
        ProcessingCapacityKilogramsPerDay original = new ProcessingCapacityKilogramsPerDay(500.0);
        ProcessingCapacityKilogramsPerDay updated = original.setKilogramsPerDay(1000.0);
        assertNotSame(original, updated);
        assertEquals(500.0, original.getKilogramsPerDay());
        assertEquals(1000.0, updated.getKilogramsPerDay());
    }

    @Test
    void greaterThan_valid() {
        ProcessingCapacityKilogramsPerDay big = new ProcessingCapacityKilogramsPerDay(1000.0);
        ProcessingCapacityKilogramsPerDay small = new ProcessingCapacityKilogramsPerDay(500.0);
        assertTrue(big.greaterThan(small));
        assertFalse(small.greaterThan(big));
    }

    @Test
    void greaterThanOrEqual_valid() {
        ProcessingCapacityKilogramsPerDay a = new ProcessingCapacityKilogramsPerDay(500.0);
        ProcessingCapacityKilogramsPerDay b = new ProcessingCapacityKilogramsPerDay(500.0);
        assertTrue(a.greaterThanOrEqual(b));
    }

    @Test
    void lessThan_valid() {
        ProcessingCapacityKilogramsPerDay small = new ProcessingCapacityKilogramsPerDay(200.0);
        ProcessingCapacityKilogramsPerDay big = new ProcessingCapacityKilogramsPerDay(500.0);
        assertTrue(small.lessThan(big));
        assertFalse(big.lessThan(small));
    }

    @Test
    void greaterThan_nullThrows() {
        ProcessingCapacityKilogramsPerDay cap = new ProcessingCapacityKilogramsPerDay(500.0);
        assertThrows(IllegalArgumentException.class, () -> cap.greaterThan(null));
    }

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

    @Test
    void hashCodeMethod() {
        ProcessingCapacityKilogramsPerDay a = new ProcessingCapacityKilogramsPerDay(500.0);
        ProcessingCapacityKilogramsPerDay b = new ProcessingCapacityKilogramsPerDay(500.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringMethod() {
        ProcessingCapacityKilogramsPerDay cap = new ProcessingCapacityKilogramsPerDay(500.0);
        assertTrue(cap.toString().contains("500"));
    }
}
