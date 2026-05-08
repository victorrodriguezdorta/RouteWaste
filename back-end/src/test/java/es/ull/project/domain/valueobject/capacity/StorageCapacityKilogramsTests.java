package es.ull.project.domain.valueobject.capacity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StorageCapacityKilogramsTests {

    @Test
    void constructor_validValue() {
        StorageCapacityKilograms cap = new StorageCapacityKilograms(5000.0);
        assertEquals(5000.0, cap.getKilograms());
    }

    @Test
    void constructor_zero() {
        StorageCapacityKilograms cap = new StorageCapacityKilograms(0.0);
        assertEquals(0.0, cap.getKilograms());
    }

    @Test
    void constructor_negativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new StorageCapacityKilograms(-1.0));
    }

    @Test
    void setKilograms_returnsNewInstance() {
        StorageCapacityKilograms original = new StorageCapacityKilograms(5000.0);
        StorageCapacityKilograms updated = original.setKilograms(10000.0);
        assertNotSame(original, updated);
        assertEquals(5000.0, original.getKilograms());
        assertEquals(10000.0, updated.getKilograms());
    }

    @Test
    void greaterThan_valid() {
        StorageCapacityKilograms big = new StorageCapacityKilograms(10000.0);
        StorageCapacityKilograms small = new StorageCapacityKilograms(5000.0);
        assertTrue(big.greaterThan(small));
        assertFalse(small.greaterThan(big));
    }

    @Test
    void greaterThanOrEqual_valid() {
        StorageCapacityKilograms a = new StorageCapacityKilograms(5000.0);
        StorageCapacityKilograms b = new StorageCapacityKilograms(5000.0);
        assertTrue(a.greaterThanOrEqual(b));
    }

    @Test
    void lessThan_valid() {
        StorageCapacityKilograms small = new StorageCapacityKilograms(1000.0);
        StorageCapacityKilograms big = new StorageCapacityKilograms(5000.0);
        assertTrue(small.lessThan(big));
        assertFalse(big.lessThan(small));
    }

    @Test
    void greaterThan_nullThrows() {
        StorageCapacityKilograms cap = new StorageCapacityKilograms(5000.0);
        assertThrows(IllegalArgumentException.class, () -> cap.greaterThan(null));
    }

    @Test
    void equalsMethod() {
        StorageCapacityKilograms a = new StorageCapacityKilograms(5000.0);
        StorageCapacityKilograms b = new StorageCapacityKilograms(5000.0);
        StorageCapacityKilograms c = new StorageCapacityKilograms(9999.0);
        assertEquals(a, a);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, Integer.valueOf(0));
    }

    @Test
    void hashCodeMethod() {
        StorageCapacityKilograms a = new StorageCapacityKilograms(5000.0);
        StorageCapacityKilograms b = new StorageCapacityKilograms(5000.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringMethod() {
        StorageCapacityKilograms cap = new StorageCapacityKilograms(5000.0);
        assertTrue(cap.toString().contains("5000"));
    }
}
