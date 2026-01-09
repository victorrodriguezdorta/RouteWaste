package es.ull.project.domain.valueobject.demand;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuantityUnitTests {

    /**
     * Constructor - valid unit
     */
    @Test
    void constructor_validUnit() {
        QuantityUnit unit = new QuantityUnit("kg");

        assertEquals("kg", unit.getValue());
    }

    /**
     * Constructor - null unit
     */
    @Test
    void constructor_nullUnit() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuantityUnit(null)
        );

        assertEquals(
                "Quantity unit is not defined",
                exception.getMessage()
        );
    }

    /**
     * Constructor - empty unit
     */
    @Test
    void constructor_emptyUnit() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuantityUnit("")
        );

        assertEquals(
                "Quantity unit cannot be empty",
                exception.getMessage()
        );
    }

    /**
     * Constructor - invalid format (contains numbers)
     */
    @Test
    void constructor_invalidFormat_numbers() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuantityUnit("kg2")
        );

        assertEquals(
                "Quantity unit format is invalid",
                exception.getMessage()
        );
    }

    /**
     * Constructor - invalid format (contains symbols)
     */
    @Test
    void constructor_invalidFormat_symbols() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuantityUnit("kg!")
        );

        assertEquals(
                "Quantity unit format is invalid",
                exception.getMessage()
        );
    }

    /**
     * Constructor - invalid format (contains spaces)
     */
    @Test
    void constructor_invalidFormat_spaces() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuantityUnit("k g")
        );

        assertEquals(
                "Quantity unit format is invalid",
                exception.getMessage()
        );
    }

    /**
     * Getter method
     */
    @Test
    void getValue() {
        QuantityUnit unit = new QuantityUnit("liters");

        assertEquals("liters", unit.getValue());
    }

    /**
     * setValue - returns new instance (immutability)
     */
    @Test
    void setValue_createsNewInstance() {
        QuantityUnit original = new QuantityUnit("kg");
        QuantityUnit updated = original.setValue("units");

        assertEquals("kg", original.getValue());
        assertEquals("units", updated.getValue());
        assertNotSame(original, updated);
    }

    /**
     * setValue - invalid new value
     */
    @Test
    void setValue_invalidValue() {
        QuantityUnit unit = new QuantityUnit("kg");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> unit.setValue("kg3")
        );

        assertEquals(
                "Quantity unit format is invalid",
                exception.getMessage()
        );
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        QuantityUnit unit1 = new QuantityUnit("kg");
        QuantityUnit unit2 = new QuantityUnit("kg");
        QuantityUnit unit3 = new QuantityUnit("units");

        assertEquals(unit1, unit1);
        assertNotEquals(unit1, null);
        assertNotEquals(unit1, Integer.valueOf(0));
        assertEquals(unit1, unit2);
        assertNotEquals(unit1, unit3);
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        QuantityUnit unit1 = new QuantityUnit("kg");
        QuantityUnit unit2 = new QuantityUnit("kg");
        QuantityUnit unit3 = new QuantityUnit("liters");

        assertEquals(unit1.hashCode(), unit2.hashCode());
        assertNotEquals(unit1.hashCode(), unit3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        QuantityUnit unit = new QuantityUnit("kg");

        assertEquals(
                "QuantityUnit={value='kg'}",
                unit.toString()
        );
    }
}
