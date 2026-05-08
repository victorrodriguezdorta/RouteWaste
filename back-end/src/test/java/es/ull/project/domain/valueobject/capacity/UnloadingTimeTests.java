package es.ull.project.domain.valueobject.capacity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UnloadingTimeTests {

    @Test
    void constructor_validValue() {
        UnloadingTime t = new UnloadingTime(45);
        assertEquals(45, t.getMinutes());
    }

    @Test
    void constructor_zero() {
        UnloadingTime t = new UnloadingTime(0);
        assertEquals(0, t.getMinutes());
    }

    @Test
    void constructor_negativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new UnloadingTime(-1));
    }

    @Test
    void getSeconds() {
        UnloadingTime t = new UnloadingTime(45);
        assertEquals(2700, t.getSeconds());
    }

    @Test
    void setMinutes_returnsNewInstance() {
        UnloadingTime original = new UnloadingTime(45);
        UnloadingTime updated = original.setMinutes(90);
        assertNotSame(original, updated);
        assertEquals(45, original.getMinutes());
        assertEquals(90, updated.getMinutes());
    }

    @Test
    void greaterThan_valid() {
        UnloadingTime big = new UnloadingTime(90);
        UnloadingTime small = new UnloadingTime(45);
        assertTrue(big.greaterThan(small));
    }

    @Test
    void lessThan_valid() {
        UnloadingTime small = new UnloadingTime(15);
        UnloadingTime big = new UnloadingTime(90);
        assertTrue(small.lessThan(big));
    }

    @Test
    void equalsMethod() {
        UnloadingTime a = new UnloadingTime(45);
        UnloadingTime b = new UnloadingTime(45);
        UnloadingTime c = new UnloadingTime(90);
        assertEquals(a, a);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, null);
    }

    @Test
    void hashCodeMethod() {
        UnloadingTime a = new UnloadingTime(45);
        UnloadingTime b = new UnloadingTime(45);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringMethod() {
        UnloadingTime t = new UnloadingTime(45);
        assertTrue(t.toString().contains("45"));
    }
}
