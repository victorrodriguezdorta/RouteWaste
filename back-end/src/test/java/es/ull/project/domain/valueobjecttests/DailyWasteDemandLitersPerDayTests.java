package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class DailyWasteDemandLitersPerDayTests {

    private static final String LITERS_PER_DAY_TEXT = "50";

    /**
     * Should create a daily waste demand with a valid positive value.
     */
    @Test
    void constructorValidValue() {
        DailyWasteDemandLitersPerDay d = new DailyWasteDemandLitersPerDay(50.0);
        assertEquals(50.0, d.getLitersPerDay());
    }

    /**
     * Should create a daily waste demand with zero liters per day.
     */
    @Test
    void constructorZero() {
        DailyWasteDemandLitersPerDay d = new DailyWasteDemandLitersPerDay(0.0);
        assertEquals(0.0, d.getLitersPerDay());
    }

    /**
     * Should reject a negative daily waste demand value.
     */
    @Test
    void constructorNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new DailyWasteDemandLitersPerDay(-1.0));
    }

    /**
     * Should return a new instance when updating the liters per day.
     */
    @Test
    void setLitersPerDayReturnsNewInstance() {
        DailyWasteDemandLitersPerDay original = new DailyWasteDemandLitersPerDay(50.0);
        DailyWasteDemandLitersPerDay updated = original.setLitersPerDay(100.0);
        assertNotSame(original, updated);
        assertEquals(50.0, original.getLitersPerDay());
        assertEquals(100.0, updated.getLitersPerDay());
    }

    /**
     * Should add two daily waste demand values.
     */
    @Test
    void addValid() {
        DailyWasteDemandLitersPerDay a = new DailyWasteDemandLitersPerDay(30.0);
        DailyWasteDemandLitersPerDay b = new DailyWasteDemandLitersPerDay(20.0);
        assertEquals(50.0, a.add(b).getLitersPerDay());
    }

    /**
     * Should reject adding a null daily waste demand.
     */
    @Test
    void addNullThrows() {
        DailyWasteDemandLitersPerDay d = new DailyWasteDemandLitersPerDay(50.0);
        assertThrows(IllegalArgumentException.class, () -> d.add(null));
    }

    /**
     * Should compare whether one daily waste demand is greater than another.
     */
    @Test
    void greaterThanValid() {
        DailyWasteDemandLitersPerDay big = new DailyWasteDemandLitersPerDay(100.0);
        DailyWasteDemandLitersPerDay small = new DailyWasteDemandLitersPerDay(50.0);
        assertTrue(big.greaterThan(small));
    }

    /**
     * Should compare daily waste demand values by their liters per day.
     */
    @Test
    void equalsMethod() {
        DailyWasteDemandLitersPerDay a = new DailyWasteDemandLitersPerDay(50.0);
        DailyWasteDemandLitersPerDay b = new DailyWasteDemandLitersPerDay(50.0);
        DailyWasteDemandLitersPerDay c = new DailyWasteDemandLitersPerDay(99.0);
        assertEquals(a, a);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, null);
    }

    /**
     * Should generate the same hash code for equal daily waste demands.
     */
    @Test
    void hashCodeMethod() {
        DailyWasteDemandLitersPerDay a = new DailyWasteDemandLitersPerDay(50.0);
        DailyWasteDemandLitersPerDay b = new DailyWasteDemandLitersPerDay(50.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    /**
     * Should include the liters per day value in the string representation.
     */
    @Test
    void toStringMethod() {
        DailyWasteDemandLitersPerDay d = new DailyWasteDemandLitersPerDay(50.0);
        assertTrue(d.toString().contains(LITERS_PER_DAY_TEXT));
    }
}
