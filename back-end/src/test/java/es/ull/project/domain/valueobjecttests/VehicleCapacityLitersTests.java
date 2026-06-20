package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class VehicleCapacityLitersTests {

    private static final String EXPECTED_TO_STRING_CAPACITY = "8000";

    /**
     * Verifies that a valid vehicle capacity value is stored in liters.
     */
    @Test
    void constructorValidValue() {
        VehicleCapacityLiters cap = new VehicleCapacityLiters(8000.0);
        assertEquals(8000.0, cap.getLiters());
    }

    /**
     * Verifies that zero liters is accepted as a vehicle capacity.
     */
    @Test
    void constructorZero() {
        VehicleCapacityLiters cap = new VehicleCapacityLiters(0.0);
        assertEquals(0.0, cap.getLiters());
    }

    /**
     * Verifies that negative vehicle capacity values are rejected.
     */
    @Test
    void constructorNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new VehicleCapacityLiters(-1.0));
    }

    /**
     * Verifies that setting liters returns a new instance with the updated value.
     */
    @Test
    void setlitersReturnsNewInstance() {
        VehicleCapacityLiters original = new VehicleCapacityLiters(8000.0);
        VehicleCapacityLiters updated = original.setliters(10000.0);
        assertNotSame(original, updated);
        assertEquals(8000.0, original.getLiters());
        assertEquals(10000.0, updated.getLiters());
    }

    /**
     * Verifies that greaterThan compares two vehicle capacities correctly.
     */
    @Test
    void greaterThanValid() {
        VehicleCapacityLiters big = new VehicleCapacityLiters(10000.0);
        VehicleCapacityLiters small = new VehicleCapacityLiters(5000.0);
        assertTrue(big.greaterThan(small));
        assertFalse(small.greaterThan(big));
    }

    /**
     * Verifies that greaterThanOrEqual accepts equal vehicle capacities.
     */
    @Test
    void greaterThanOrEqualValid() {
        VehicleCapacityLiters a = new VehicleCapacityLiters(8000.0);
        VehicleCapacityLiters b = new VehicleCapacityLiters(8000.0);
        assertTrue(a.greaterThanOrEqual(b));
    }

    /**
     * Verifies that lessThan compares two vehicle capacities correctly.
     */
    @Test
    void lessThanValid() {
        VehicleCapacityLiters small = new VehicleCapacityLiters(3000.0);
        VehicleCapacityLiters big = new VehicleCapacityLiters(8000.0);
        assertTrue(small.lessThan(big));
        assertFalse(big.lessThan(small));
    }

    /**
     * Verifies that greaterThan rejects a null comparison value.
     */
    @Test
    void greaterThanNullThrows() {
        VehicleCapacityLiters cap = new VehicleCapacityLiters(8000.0);
        assertThrows(IllegalArgumentException.class, () -> cap.greaterThan(null));
    }

    /**
     * Verifies equality for matching and different vehicle capacities.
     */
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

    /**
     * Verifies that equal vehicle capacities produce the same hash code.
     */
    @Test
    void hashCodeMethod() {
        VehicleCapacityLiters a = new VehicleCapacityLiters(8000.0);
        VehicleCapacityLiters b = new VehicleCapacityLiters(8000.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    /**
     * Verifies that the string representation includes the capacity value.
     */
    @Test
    void toStringMethod() {
        VehicleCapacityLiters cap = new VehicleCapacityLiters(8000.0);
        assertTrue(cap.toString().contains(EXPECTED_TO_STRING_CAPACITY));
    }
}
