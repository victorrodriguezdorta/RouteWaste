package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.enumerate.TimeUnit;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class WasteDemandTests {

    private static final String NEGATIVE_WASTE_DEMAND_MESSAGE = "Waste demand cannot be negative";
    private static final String OTHER_WASTE_DEMAND_NULL_MESSAGE = "Other WasteDemand cannot be null";
    private static final String UNITS_CANNOT_BE_NULL_MESSAGE = "Units cannot be null";
    private static final String UNITS_MUST_BE_THE_SAME_MESSAGE = "Units must be the same";
    private static final String WASTE_DEMAND_STRING_REPRESENTATION = "WasteDemand=5.0 QuantityUnit={value='tons'}/DAY";

    /**
     * Constructor - valid value (full constructor)
     */
    @Test
    void constructorValidValue() {
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
    void constructorValidValueDefaultUnits() {
        WasteDemand demand = new WasteDemand(5.0);
        assertEquals(5.0, demand.getValue());
        assertEquals(new QuantityUnit("tons"), demand.getQuantityUnit());
        assertEquals(TimeUnit.DAY, demand.getTimeUnit());
    }

    /**
     * Constructor - negative value
     */
    @Test
    void constructorNegativeValue() {
        QuantityUnit unit = new QuantityUnit("tons");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new WasteDemand(-1.0, unit, TimeUnit.DAY)
        );
        assertEquals(
                NEGATIVE_WASTE_DEMAND_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - quantityUnit is null
     */
    @Test
    void constructorQuantityUnitNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new WasteDemand(5.0, null, TimeUnit.DAY)
        );
        assertEquals(
                UNITS_CANNOT_BE_NULL_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - timeUnit is null
     */
    @Test
    void constructorTimeUnitNull() {
        QuantityUnit unit = new QuantityUnit("tons");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new WasteDemand(5.0, unit, null)
        );
        assertEquals(
                UNITS_CANNOT_BE_NULL_MESSAGE,
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
    void setterValue() {
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
    void setterQuantityUnit() {
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
    void setterTimeUnit() {
        WasteDemand original = new WasteDemand(5.0);
        WasteDemand updated = original.setTimeUnit(TimeUnit.WEEK);
        assertNotSame(original, updated);
        assertEquals(TimeUnit.WEEK, updated.getTimeUnit());
    }

    /**
     * Add - valid
     */
    @Test
    void addValid() {
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
    void addOtherNull() {
        WasteDemand demand = new WasteDemand(5.0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> demand.add(null)
        );
        assertEquals(
                OTHER_WASTE_DEMAND_NULL_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Add - units do not match
     */
    @Test
    void addUnitsDoNotMatch() {
        WasteDemand d1 = new WasteDemand(5.0, new QuantityUnit("tons"), TimeUnit.DAY);
        WasteDemand d2 = new WasteDemand(3.0, new QuantityUnit("kg"), TimeUnit.DAY);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> d1.add(d2)
        );
        assertEquals(
                UNITS_MUST_BE_THE_SAME_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * greaterThan - valid
     */
    @Test
    void greaterThanValid() {
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
                WASTE_DEMAND_STRING_REPRESENTATION,
                demand.toString()
        );
    }
}

