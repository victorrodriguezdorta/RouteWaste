package es.ull.project.domain.valueobject.demand;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class CapacityTests {

    /**
     * Constructor - valid value
     */
    @Test
    void constructor_validValue() {
        QuantityUnit unit = new QuantityUnit("kg");
        Capacity capacity = new Capacity(10.0, unit, TimeUnit.DAYS);

        assertEquals(10.0, capacity.getValue());
        assertEquals(unit, capacity.getQuantityUnit());
        assertEquals(TimeUnit.DAYS, capacity.getTimeUnit());
    }

    /**
     * Constructor - negative value
     */
    @Test
    void constructor_negativeValue() {
        QuantityUnit unit = new QuantityUnit("kg");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Capacity(-1.0, unit, TimeUnit.DAYS)
        );

        assertEquals(
                "Capacity value must be greater than or equal to 0",
                exception.getMessage()
        );
    }

    /**
     * Constructor - quantity unit not defined
     */
    @Test
    void constructor_quantityUnitNotDefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Capacity(5.0, null, TimeUnit.DAYS)
        );

        assertEquals(
                "Quantity unit is not defined",
                exception.getMessage()
        );
    }

    /**
     * Constructor - time unit not defined
     */
    @Test
    void constructor_timeUnitNotDefined() {
        QuantityUnit unit = new QuantityUnit("kg");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Capacity(5.0, unit, null)
        );

        assertEquals(
                "Time unit is not defined",
                exception.getMessage()
        );
    }

    /**
     * Getter methods
     */
    @Test
    void getters() {
        QuantityUnit unit = new QuantityUnit("tons");
        Capacity capacity = new Capacity(3.5, unit, TimeUnit.HOURS);

        assertEquals(3.5, capacity.getValue());
        assertEquals(unit, capacity.getQuantityUnit());
        assertEquals(TimeUnit.HOURS, capacity.getTimeUnit());
    }

    /**
     * Setter - value
     */
    @Test
    void setter_value() {
        Capacity original = new Capacity(5.0, new QuantityUnit("kg"), TimeUnit.DAYS);
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
    void setter_quantityUnit() {
        Capacity original = new Capacity(5.0, new QuantityUnit("kg"), TimeUnit.DAYS);
        QuantityUnit newUnit = new QuantityUnit("tons");

        Capacity updated = original.setQuantityUnit(newUnit);

        assertNotSame(original, updated);
        assertEquals(newUnit, updated.getQuantityUnit());
    }

    /**
     * Setter - time unit
     */
    @Test
    void setter_timeUnit() {
        Capacity original = new Capacity(5.0, new QuantityUnit("kg"), TimeUnit.DAYS);

        Capacity updated = original.setTimeUnit(TimeUnit.HOURS);

        assertNotSame(original, updated);
        assertEquals(TimeUnit.HOURS, updated.getTimeUnit());
    }

    /**
     * greaterThan - valid comparison
     */
    @Test
    void greaterThan_validComparison() {
        QuantityUnit unit = new QuantityUnit("kg");

        Capacity lower = new Capacity(5.0, unit, TimeUnit.DAYS);
        Capacity higher = new Capacity(10.0, unit, TimeUnit.DAYS);

        assertTrue(higher.greaterThan(lower));
        assertFalse(lower.greaterThan(higher));
    }

    /**
     * greaterThan - other capacity is null
     */
    @Test
    void greaterThan_otherIsNull() {
        Capacity capacity = new Capacity(5.0, new QuantityUnit("kg"), TimeUnit.DAYS);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> capacity.greaterThan(null)
        );

        assertEquals(
                "Other Capacity cannot be null",
                exception.getMessage()
        );
    }

    /**
     * greaterThan - units do not match
     */
    @Test
    void greaterThan_unitsDoNotMatch() {
        Capacity capacity1 = new Capacity(
                5.0,
                new QuantityUnit("kg"),
                TimeUnit.DAYS
        );

        Capacity capacity2 = new Capacity(
                3.0,
                new QuantityUnit("tons"),
                TimeUnit.DAYS
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> capacity1.greaterThan(capacity2)
        );

        assertEquals(
                "Units must be the same",
                exception.getMessage()
        );
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        QuantityUnit unit = new QuantityUnit("kg");

        Capacity capacity1 = new Capacity(5.0, unit, TimeUnit.DAYS);
        Capacity capacity2 = new Capacity(5.0, unit, TimeUnit.DAYS);
        Capacity capacity3 = new Capacity(10.0, unit, TimeUnit.DAYS);

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

        Capacity capacity1 = new Capacity(5.0, unit, TimeUnit.DAYS);
        Capacity capacity2 = new Capacity(5.0, unit, TimeUnit.DAYS);
        Capacity capacity3 = new Capacity(10.0, unit, TimeUnit.DAYS);

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
                TimeUnit.DAYS
        );

        assertEquals(
                "Capacity={value=5.0, unit=kg/days}",
                capacity.toString()
        );
    }
}
