package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ContainerCapacityLitersTests {

    private static final String CAPACITY_LITERS_TEXT = "100";

    /**
     * Should create a container capacity with a valid positive value.
     */
    @Test
    void constructorValidValue() {
        ContainerCapacityLiters cap = new ContainerCapacityLiters(100.0);
        assertEquals(100.0, cap.getLiters());
    }

    /**
     * Should create a container capacity with zero liters.
     */
    @Test
    void constructorZero() {
        ContainerCapacityLiters cap = new ContainerCapacityLiters(0.0);
        assertEquals(0.0, cap.getLiters());
    }

    /**
     * Should reject a negative container capacity value.
     */
    @Test
    void constructorNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new ContainerCapacityLiters(-1.0));
    }

    /**
     * Should return a new instance when updating liters.
     */
    @Test
    void setLitersReturnsNewInstance() {
        ContainerCapacityLiters original = new ContainerCapacityLiters(100.0);
        ContainerCapacityLiters updated = original.setLiters(200.0);
        assertNotSame(original, updated);
        assertEquals(100.0, original.getLiters());
        assertEquals(200.0, updated.getLiters());
    }

    /**
     * Should compare whether one container capacity is greater than another.
     */
    @Test
    void greaterThanValid() {
        ContainerCapacityLiters big = new ContainerCapacityLiters(200.0);
        ContainerCapacityLiters small = new ContainerCapacityLiters(100.0);
        assertTrue(big.greaterThan(small));
        assertFalse(small.greaterThan(big));
    }

    /**
     * Should compare whether one container capacity is greater than or equal to another.
     */
    @Test
    void greaterThanOrEqualValid() {
        ContainerCapacityLiters a = new ContainerCapacityLiters(100.0);
        ContainerCapacityLiters b = new ContainerCapacityLiters(100.0);
        assertTrue(a.greaterThanOrEqual(b));
    }

    /**
     * Should compare whether one container capacity is less than another.
     */
    @Test
    void lessThanValid() {
        ContainerCapacityLiters small = new ContainerCapacityLiters(50.0);
        ContainerCapacityLiters big = new ContainerCapacityLiters(100.0);
        assertTrue(small.lessThan(big));
        assertFalse(big.lessThan(small));
    }

    /**
     * Should reject comparing against a null container capacity.
     */
    @Test
    void greaterThanNullThrows() {
        ContainerCapacityLiters cap = new ContainerCapacityLiters(100.0);
        assertThrows(IllegalArgumentException.class, () -> cap.greaterThan(null));
    }

    /**
     * Should compare container capacities by their liters value.
     */
    @Test
    void equalsMethod() {
        ContainerCapacityLiters a = new ContainerCapacityLiters(100.0);
        ContainerCapacityLiters b = new ContainerCapacityLiters(100.0);
        ContainerCapacityLiters c = new ContainerCapacityLiters(200.0);
        assertEquals(a, a);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, Integer.valueOf(0));
    }

    /**
     * Should generate the same hash code for equal container capacities.
     */
    @Test
    void hashCodeMethod() {
        ContainerCapacityLiters a = new ContainerCapacityLiters(100.0);
        ContainerCapacityLiters b = new ContainerCapacityLiters(100.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    /**
     * Should include the liters value in the string representation.
     */
    @Test
    void toStringMethod() {
        ContainerCapacityLiters cap = new ContainerCapacityLiters(100.0);
        assertTrue(cap.toString().contains(CAPACITY_LITERS_TEXT));
    }
}
