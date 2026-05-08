package es.ull.project.domain.valueobject.capacity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VehicleCapacityLitersTests {

    @Test
    void constructor_validValue() {
        VehicleCapacityLiters cap = new VehicleCapacityLiters(8000.0);
        assertEquals(8000.0, cap.getLiters());
    }

    @Test
    void constructor_zero() {
        VehicleCapacityLiters cap = new VehicleCapacityLiters(0.0);
        assertEquals(0.0, cap.getLiters());
    }

    @Test
    void constructor_negativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new VehicleCapacityLiters(-1.0));
    }

    @Test
    void setliters_returnsNewInstance() {
        VehicleCapacityLiters original = new VehicleCapacityLiters(8000.0);
        VehicleCapacityLiters updated = original.setliters(10000.0);
        assertNotSame(original, updated);
        assertEquals(8000.0, original.getLiters());
        assertEquals(10000.0, updated.getLiters());
    }

    @Test
    void greaterThan_valid() {
        VehicleCapacityLiters big = new VehicleCapacityLiters(10000.0);
        VehicleCapacityLiters small = new VehicleCapacityLiters(5000.0);
        assertTrue(big.greaterThan(small));
        assertFalse(small.greaterThan(big));
    }

    @Test
    void greaterThanOrEqual_valid() {
        VehicleCapacityLiters a = new VehicleCapacityLiters(8000.0);
        VehicleCapacityLiters b = new VehicleCapacityLiters(8000.0);
        assertTrue(a.greaterThanOrEqual(b));
    }

    @Test
    void lessThan_valid() {
        VehicleCapacityLiters small = new VehicleCapacityLiters(3000.0);
        VehicleCapacityLiters big = new VehicleCapacityLiters(8000.0);
        assertTrue(small.lessThan(big));
        assertFalse(big.lessThan(small));
    }

    @Test
    void greaterThan_nullThrows() {
        VehicleCapacityLiters cap = new VehicleCapacityLiters(8000.0);
        assertThrows(IllegalArgumentException.class, () -> cap.greaterThan(null));
    }

    @Test
    void equalsMethod() {
        VehicleCapacityLiters a = new VehicleCapacityLiters(8000.0);
        VehicleCapacityLiters b = new VehicleCapacityLiters(8000.0);
        VehicleCapacityLiters c = new VehicleCapacityLiters(9000.0);
        assertEquals(a, a);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, Integer.valueOf(0));
    }

    @Test
    void hashCodeMethod() {
        VehicleCapacityLiters a = new VehicleCapacityLiters(8000.0);
        VehicleCapacityLiters b = new VehicleCapacityLiters(8000.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringMethod() {
        VehicleCapacityLiters cap = new VehicleCapacityLiters(8000.0);
        assertTrue(cap.toString().contains("8000"));
    }
}
