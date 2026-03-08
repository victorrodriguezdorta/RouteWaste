package es.ull.project.domain.valueobject.demand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import es.ull.project.domain.enumerate.TimeUnit;

public class WasteDemandTests {

    /**
     * Constructor - valid value (full constructor)
     */
    @Test
    void constructor_validValue() {
        QuantityUnit unit = new QuantityUnit("tons");
        TimeUnit timeUnit = TimeUnit.DAY;

        WasteDemand demand = new WasteDemand(10.5, unit, timeUnit);

        assertEquals(10.5, demand.getValue());
        assertEquals(unit, demand.getQuantityUnit());
        assertEquals(timeUnit, demand.getTimeUnit());
    }

    /**
     * Constructor - valid value (default units)
     */
    @Test
    void constructor_validValue_defaultUnits() {
        WasteDemand demand = new WasteDemand(5.0);

        assertEquals(5.0, demand.getValue());
        assertEquals(new QuantityUnit("tons"), demand.getQuantityUnit());
        assertEquals(TimeUnit.DAY, demand.getTimeUnit());
    }

    /**
     * Constructor - negative value
     */
    @Test
    void constructor_negativeValue() {
        QuantityUnit unit = new QuantityUnit("tons");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new WasteDemand(-1.0, unit, TimeUnit.DAY)
        );

        assertEquals(
                "Waste demand cannot be negative",
                exception.getMessage()
        );
    }

    /**
     * Constructor - quantityUnit is null
     */
    @Test
    void constructor_quantityUnitNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new WasteDemand(5.0, null, TimeUnit.DAY)
        );

        assertEquals(
                "Units cannot be null",
                exception.getMessage()
        );
    }

    /**
     * Constructor - timeUnit is null
     */
    @Test
    void constructor_timeUnitNull() {
        QuantityUnit unit = new QuantityUnit("tons");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new WasteDemand(5.0, unit, null)
        );

        assertEquals(
                "Units cannot be null",
                exception.getMessage()
        );
    }

    /**
     * Getter methods
     */
    @Test
    void getters() {
        QuantityUnit unit = new QuantityUnit("kg");
        TimeUnit timeUnit = TimeUnit.WEEK;

        WasteDemand demand = new WasteDemand(3.2, unit, timeUnit);

        assertEquals(3.2, demand.getValue());
        assertEquals(unit, demand.getQuantityUnit());
        assertEquals(timeUnit, demand.getTimeUnit());
    }

    /**
     * Setter - value (immutability)
     */
    @Test
    void setter_value() {
        WasteDemand original = new WasteDemand(5.0);
        WasteDemand updated = original.setValue(10.0);

        assertNotSame(original, updated);
        assertEquals(5.0, original.getValue());
        assertEquals(10.0, updated.getValue());
    }

    /**
     * Setter - quantityUnit
     */
    @Test
    void setter_quantityUnit() {
        WasteDemand original = new WasteDemand(5.0);
        QuantityUnit newUnit = new QuantityUnit("kg");

        WasteDemand updated = original.setQuantityUnit(newUnit);

        assertNotSame(original, updated);
        assertEquals(newUnit, updated.getQuantityUnit());
    }

    /**
     * Setter - timeUnit
     */
    @Test
    void setter_timeUnit() {
        WasteDemand original = new WasteDemand(5.0);

        WasteDemand updated = original.setTimeUnit(TimeUnit.WEEK);

        assertNotSame(original, updated);
        assertEquals(TimeUnit.WEEK, updated.getTimeUnit());
    }

    /**
     * Add - valid
     */
    @Test
    void add_valid() {
        QuantityUnit unit = new QuantityUnit("tons");
        WasteDemand d1 = new WasteDemand(5.0, unit, TimeUnit.DAY);
        WasteDemand d2 = new WasteDemand(3.0, unit, TimeUnit.DAY);

        WasteDemand result = d1.add(d2);

        assertEquals(8.0, result.getValue());
    }

    /**
     * Add - other is null
     */
    @Test
    void add_otherNull() {
        WasteDemand demand = new WasteDemand(5.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> demand.add(null)
        );

        assertEquals(
                "Other WasteDemand cannot be null",
                exception.getMessage()
        );
    }

    /**
     * Add - units do not match
     */
    @Test
    void add_unitsDoNotMatch() {
        WasteDemand d1 = new WasteDemand(5.0, new QuantityUnit("tons"), TimeUnit.DAY);
        WasteDemand d2 = new WasteDemand(3.0, new QuantityUnit("kg"), TimeUnit.DAY);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> d1.add(d2)
        );

        assertEquals(
                "Units must be the same",
                exception.getMessage()
        );
    }

    /**
     * greaterThan - valid
     */
    @Test
    void greaterThan_valid() {
        WasteDemand d1 = new WasteDemand(10.0);
        WasteDemand d2 = new WasteDemand(5.0);

        assertTrue(d1.greaterThan(d2));
        assertFalse(d2.greaterThan(d1));
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        WasteDemand d1 = new WasteDemand(5.0);
        WasteDemand d2 = new WasteDemand(5.0);
        WasteDemand d3 = new WasteDemand(6.0);

        assertEquals(d1, d1);
        assertNotEquals(d1, null);
        assertNotEquals(d1, Integer.valueOf(0));
        assertEquals(d1, d2);
        assertNotEquals(d1, d3);
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        WasteDemand d1 = new WasteDemand(5.0);
        WasteDemand d2 = new WasteDemand(5.0);
        WasteDemand d3 = new WasteDemand(6.0);

        assertEquals(d1.hashCode(), d2.hashCode());
        assertNotEquals(d1.hashCode(), d3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        WasteDemand demand = new WasteDemand(5.0);

        assertEquals(
                "WasteDemand=5.0 QuantityUnit={value='tons'}/DAY",
                demand.toString()
        );
    }
}

