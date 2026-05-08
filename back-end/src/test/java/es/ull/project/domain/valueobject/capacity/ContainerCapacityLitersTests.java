package es.ull.project.domain.valueobject.capacity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ContainerCapacityLitersTests {

    @Test
    void constructor_validValue() {
        ContainerCapacityLiters cap = new ContainerCapacityLiters(100.0);
        assertEquals(100.0, cap.getLiters());
    }

    @Test
    void constructor_zero() {
        ContainerCapacityLiters cap = new ContainerCapacityLiters(0.0);
        assertEquals(0.0, cap.getLiters());
    }

    @Test
    void constructor_negativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new ContainerCapacityLiters(-1.0));
    }

    @Test
    void setLiters_returnsNewInstance() {
        ContainerCapacityLiters original = new ContainerCapacityLiters(100.0);
        ContainerCapacityLiters updated = original.setLiters(200.0);
        assertNotSame(original, updated);
        assertEquals(100.0, original.getLiters());
        assertEquals(200.0, updated.getLiters());
    }

    @Test
    void greaterThan_valid() {
        ContainerCapacityLiters big = new ContainerCapacityLiters(200.0);
        ContainerCapacityLiters small = new ContainerCapacityLiters(100.0);
        assertTrue(big.greaterThan(small));
        assertFalse(small.greaterThan(big));
    }

    @Test
    void greaterThanOrEqual_valid() {
        ContainerCapacityLiters a = new ContainerCapacityLiters(100.0);
        ContainerCapacityLiters b = new ContainerCapacityLiters(100.0);
        assertTrue(a.greaterThanOrEqual(b));
    }

    @Test
    void lessThan_valid() {
        ContainerCapacityLiters small = new ContainerCapacityLiters(50.0);
        ContainerCapacityLiters big = new ContainerCapacityLiters(100.0);
        assertTrue(small.lessThan(big));
        assertFalse(big.lessThan(small));
    }

    @Test
    void greaterThan_nullThrows() {
        ContainerCapacityLiters cap = new ContainerCapacityLiters(100.0);
        assertThrows(IllegalArgumentException.class, () -> cap.greaterThan(null));
    }

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

    @Test
    void hashCodeMethod() {
        ContainerCapacityLiters a = new ContainerCapacityLiters(100.0);
        ContainerCapacityLiters b = new ContainerCapacityLiters(100.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringMethod() {
        ContainerCapacityLiters cap = new ContainerCapacityLiters(100.0);
        assertTrue(cap.toString().contains("100"));
    }
}
