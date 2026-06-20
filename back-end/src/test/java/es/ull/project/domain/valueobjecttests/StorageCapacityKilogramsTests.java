package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class StorageCapacityKilogramsTests {

    private static final String EXPECTED_TO_STRING_CAPACITY = "5000";

    /**
     * Tests that the constructor stores a valid storage capacity value.
     */
    @Test
    void constructorValidValue() {
        StorageCapacityKilograms cap = new StorageCapacityKilograms(5000.0);
        assertEquals(5000.0, cap.getKilograms());
    }

    /**
     * Tests that the constructor accepts zero kilograms.
     */
    @Test
    void constructorZero() {
        StorageCapacityKilograms cap = new StorageCapacityKilograms(0.0);
        assertEquals(0.0, cap.getKilograms());
    }

    /**
     * Tests that the constructor rejects a negative storage capacity value.
     */
    @Test
    void constructorNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new StorageCapacityKilograms(-1.0));
    }

    /**
     * Tests that setting kilograms returns a new instance with the updated value.
     */
    @Test
    void setKilogramsReturnsNewInstance() {
        StorageCapacityKilograms original = new StorageCapacityKilograms(5000.0);
        StorageCapacityKilograms updated = original.setKilograms(10000.0);
        assertNotSame(original, updated);
        assertEquals(5000.0, original.getKilograms());
        assertEquals(10000.0, updated.getKilograms());
    }

    /**
     * Tests greater-than comparison between two storage capacities.
     */
    @Test
    void greaterThanValid() {
        StorageCapacityKilograms big = new StorageCapacityKilograms(10000.0);
        StorageCapacityKilograms small = new StorageCapacityKilograms(5000.0);
        assertTrue(big.greaterThan(small));
        assertFalse(small.greaterThan(big));
    }

    /**
     * Tests greater-than-or-equal comparison between equal storage capacities.
     */
    @Test
    void greaterThanOrEqualValid() {
        StorageCapacityKilograms a = new StorageCapacityKilograms(5000.0);
        StorageCapacityKilograms b = new StorageCapacityKilograms(5000.0);
        assertTrue(a.greaterThanOrEqual(b));
    }

    /**
     * Tests less-than comparison between two storage capacities.
     */
    @Test
    void lessThanValid() {
        StorageCapacityKilograms small = new StorageCapacityKilograms(1000.0);
        StorageCapacityKilograms big = new StorageCapacityKilograms(5000.0);
        assertTrue(small.lessThan(big));
        assertFalse(big.lessThan(small));
    }

    /**
     * Tests that greater-than comparison rejects a null capacity.
     */
    @Test
    void greaterThanNullThrows() {
        StorageCapacityKilograms cap = new StorageCapacityKilograms(5000.0);
        assertThrows(IllegalArgumentException.class, () -> cap.greaterThan(null));
    }

    /**
     * Tests equality semantics for storage capacity values.
     */
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

    /**
     * Tests that equal storage capacities produce the same hash code.
     */
    @Test
    void hashCodeMethod() {
        StorageCapacityKilograms a = new StorageCapacityKilograms(5000.0);
        StorageCapacityKilograms b = new StorageCapacityKilograms(5000.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    /**
     * Tests that the string representation includes the storage capacity value.
     */
    @Test
    void toStringMethod() {
        StorageCapacityKilograms cap = new StorageCapacityKilograms(5000.0);
        assertTrue(cap.toString().contains(EXPECTED_TO_STRING_CAPACITY));
    }
}
