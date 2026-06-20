package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.enumerate.TimeUnit;
import es.ull.project.domain.valueobject.capacity.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CapacityTests {

    private static final String NEGATIVE_VALUE_MESSAGE = "Capacity value must be greater than or equal to 0";
    private static final String QUANTITY_UNIT_NOT_DEFINED_MESSAGE = "Quantity unit is not defined";
    private static final String TIME_UNIT_NOT_DEFINED_MESSAGE = "Time unit is not defined";
    private static final String OTHER_CAPACITY_NULL_MESSAGE = "Other Capacity cannot be null";
    private static final String UNITS_MUST_BE_THE_SAME_MESSAGE = "Units must be the same";
    private static final String CAPACITY_STRING_REPRESENTATION = "Capacity={value=5.0, unit=kg/day}";

    /**
     * Constructor - valid value
     */
    @Test
    void constructorValidValue() {
        QuantityUnit unit = new QuantityUnit("kg");
        Capacity capacity = new Capacity(10.0, unit, TimeUnit.DAY);
        assertEquals(10.0, capacity.getValue());
        assertEquals(unit, capacity.getQuantityUnit());
        assertEquals(TimeUnit.DAY, capacity.getTimeUnit());
    }

    /**
     * Constructor - negative value
     */
    @Test
    void constructorNegativeValue() {
        QuantityUnit unit = new QuantityUnit("kg");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Capacity(-1.0, unit, TimeUnit.DAY)
        );
        assertEquals(
                NEGATIVE_VALUE_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - quantity unit not defined
     */
    @Test
    void constructorQuantityUnitNotDefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Capacity(5.0, null, TimeUnit.DAY)
        );
        assertEquals(
                QUANTITY_UNIT_NOT_DEFINED_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - time unit not defined
     */
    @Test
    void constructorTimeUnitNotDefined() {
        QuantityUnit unit = new QuantityUnit("kg");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Capacity(5.0, unit, null)
        );
        assertEquals(
                TIME_UNIT_NOT_DEFINED_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Getter methods
     */
    @Test
    void getters() {
        QuantityUnit unit = new QuantityUnit("tons");
        Capacity capacity = new Capacity(3.5, unit, TimeUnit.WEEK);
        assertEquals(3.5, capacity.getValue());
        assertEquals(unit, capacity.getQuantityUnit());
        assertEquals(TimeUnit.WEEK, capacity.getTimeUnit());
    }

    /**
     * Setter - value
     */
    @Test
    void setterValue() {
        Capacity original = new Capacity(5.0, new QuantityUnit("kg"), TimeUnit.DAY);
        Capacity updated = original.setValue(10.0);
        assertNotSame(original, updated);
        assertEquals(10.0, updated.getValue());
        assertEquals(original.getQuantityUnit(), updated.getQuantityUnit());
        assertEquals(original.getTimeUnit(), updated.getTimeUnit());
    }

    /**
     * Setter - quantity unit
     */
    @Test
    void setterQuantityUnit() {
        Capacity original = new Capacity(5.0, new QuantityUnit("kg"), TimeUnit.DAY);
        QuantityUnit newUnit = new QuantityUnit("tons");
        Capacity updated = original.setQuantityUnit(newUnit);
        assertNotSame(original, updated);
        assertEquals(newUnit, updated.getQuantityUnit());
    }

    /**
     * Setter - time unit
     */
    @Test
    void setterTimeUnit() {
        Capacity original = new Capacity(5.0, new QuantityUnit("kg"), TimeUnit.DAY);
        Capacity updated = original.setTimeUnit(TimeUnit.WEEK);
        assertNotSame(original, updated);
        assertEquals(TimeUnit.WEEK, updated.getTimeUnit());
    }

    /**
     * greaterThan - valid comparison
     */
    @Test
    void greaterThanValidComparison() {
        QuantityUnit unit = new QuantityUnit("kg");
        Capacity lower = new Capacity(5.0, unit, TimeUnit.DAY);
        Capacity higher = new Capacity(10.0, unit, TimeUnit.DAY);
        assertTrue(higher.greaterThan(lower));
        assertFalse(lower.greaterThan(higher));
    }

    /**
     * greaterThan - other capacity is null
     */
    @Test
    void greaterThanOtherIsNull() {
        Capacity capacity = new Capacity(5.0, new QuantityUnit("kg"), TimeUnit.DAY);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> capacity.greaterThan(null)
        );
        assertEquals(
                OTHER_CAPACITY_NULL_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * greaterThan - units do not match
     */
    @Test
    void greaterThanUnitsDoNotMatch() {
        Capacity capacity1 = new Capacity(
                5.0,
                new QuantityUnit("kg"),
                TimeUnit.DAY
        );
        Capacity capacity2 = new Capacity(
                3.0,
                new QuantityUnit("tons"),
                TimeUnit.DAY
        );
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> capacity1.greaterThan(capacity2)
        );
        assertEquals(
                UNITS_MUST_BE_THE_SAME_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        QuantityUnit unit = new QuantityUnit("kg");
        Capacity capacity1 = new Capacity(5.0, unit, TimeUnit.DAY);
        Capacity capacity2 = new Capacity(5.0, unit, TimeUnit.DAY);
        Capacity capacity3 = new Capacity(10.0, unit, TimeUnit.DAY);
        assertEquals(capacity1, capacity1);
        assertNotEquals(capacity1, null);
        assertNotEquals(capacity1, Integer.valueOf(0));
        assertEquals(capacity1, capacity2);
        assertNotEquals(capacity1, capacity3);
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        QuantityUnit unit = new QuantityUnit("kg");
        Capacity capacity1 = new Capacity(5.0, unit, TimeUnit.DAY);
        Capacity capacity2 = new Capacity(5.0, unit, TimeUnit.DAY);
        Capacity capacity3 = new Capacity(10.0, unit, TimeUnit.DAY);
        assertEquals(capacity1.hashCode(), capacity2.hashCode());
        assertNotEquals(capacity1.hashCode(), capacity3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        Capacity capacity = new Capacity(
                5.0,
                new QuantityUnit("kg"),
                TimeUnit.DAY
        );
        assertEquals(
                CAPACITY_STRING_REPRESENTATION,
                capacity.toString()
        );
    }
}

