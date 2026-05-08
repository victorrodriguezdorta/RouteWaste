package es.ull.project.domain.valueobject.demand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DailyWasteDemandLitersPerDayTests {

    @Test
    void constructor_validValue() {
        DailyWasteDemandLitersPerDay d = new DailyWasteDemandLitersPerDay(50.0);
        assertEquals(50.0, d.getLitersPerDay());
    }

    @Test
    void constructor_zero() {
        DailyWasteDemandLitersPerDay d = new DailyWasteDemandLitersPerDay(0.0);
        assertEquals(0.0, d.getLitersPerDay());
    }

    @Test
    void constructor_negativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new DailyWasteDemandLitersPerDay(-1.0));
    }

    @Test
    void setLitersPerDay_returnsNewInstance() {
        DailyWasteDemandLitersPerDay original = new DailyWasteDemandLitersPerDay(50.0);
        DailyWasteDemandLitersPerDay updated = original.setLitersPerDay(100.0);
        assertNotSame(original, updated);
        assertEquals(50.0, original.getLitersPerDay());
        assertEquals(100.0, updated.getLitersPerDay());
    }

    @Test
    void add_valid() {
        DailyWasteDemandLitersPerDay a = new DailyWasteDemandLitersPerDay(30.0);
        DailyWasteDemandLitersPerDay b = new DailyWasteDemandLitersPerDay(20.0);
        assertEquals(50.0, a.add(b).getLitersPerDay());
    }

    @Test
    void add_nullThrows() {
        DailyWasteDemandLitersPerDay d = new DailyWasteDemandLitersPerDay(50.0);
        assertThrows(IllegalArgumentException.class, () -> d.add(null));
    }

    @Test
    void greaterThan_valid() {
        DailyWasteDemandLitersPerDay big = new DailyWasteDemandLitersPerDay(100.0);
        DailyWasteDemandLitersPerDay small = new DailyWasteDemandLitersPerDay(50.0);
        assertTrue(big.greaterThan(small));
    }

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

    @Test
    void hashCodeMethod() {
        DailyWasteDemandLitersPerDay a = new DailyWasteDemandLitersPerDay(50.0);
        DailyWasteDemandLitersPerDay b = new DailyWasteDemandLitersPerDay(50.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringMethod() {
        DailyWasteDemandLitersPerDay d = new DailyWasteDemandLitersPerDay(50.0);
        assertTrue(d.toString().contains("50"));
    }
}
