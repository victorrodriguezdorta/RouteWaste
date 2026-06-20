package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UnloadingTimeTests {

    private static final String UNLOADING_TIME_TEXT = "45";

    /**
     * Should create an unloading time with a valid positive value.
     */
    @Test
    void constructorValidValue() {
        UnloadingTime t = new UnloadingTime(45);
        assertEquals(45, t.getMinutes());
    }

    /**
     * Should create an unloading time with zero minutes.
     */
    @Test
    void constructorZero() {
        UnloadingTime t = new UnloadingTime(0);
        assertEquals(0, t.getMinutes());
    }

    /**
     * Should reject a negative unloading time value.
     */
    @Test
    void constructorNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new UnloadingTime(-1));
    }

    /**
     * Should convert unloading minutes to seconds.
     */
    @Test
    void getSeconds() {
        UnloadingTime t = new UnloadingTime(45);
        assertEquals(2700, t.getSeconds());
    }

    /**
     * Should return a new instance when updating minutes.
     */
    @Test
    void setMinutesReturnsNewInstance() {
        UnloadingTime original = new UnloadingTime(45);
        UnloadingTime updated = original.setMinutes(90);
        assertNotSame(original, updated);
        assertEquals(45, original.getMinutes());
        assertEquals(90, updated.getMinutes());
    }

    /**
     * Should compare whether one unloading time is greater than another.
     */
    @Test
    void greaterThanValid() {
        UnloadingTime big = new UnloadingTime(90);
        UnloadingTime small = new UnloadingTime(45);
        assertTrue(big.greaterThan(small));
    }

    /**
     * Should compare whether one unloading time is less than another.
     */
    @Test
    void lessThanValid() {
        UnloadingTime small = new UnloadingTime(15);
        UnloadingTime big = new UnloadingTime(90);
        assertTrue(small.lessThan(big));
    }

    /**
     * Should compare unloading times by their minutes.
     */
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

    /**
     * Should generate the same hash code for equal unloading times.
     */
    @Test
    void hashCodeMethod() {
        UnloadingTime a = new UnloadingTime(45);
        UnloadingTime b = new UnloadingTime(45);
        assertEquals(a.hashCode(), b.hashCode());
    }

    /**
     * Should include the minutes value in the string representation.
     */
    @Test
    void toStringMethod() {
        UnloadingTime t = new UnloadingTime(45);
        assertTrue(t.toString().contains(UNLOADING_TIME_TEXT));
    }
}
