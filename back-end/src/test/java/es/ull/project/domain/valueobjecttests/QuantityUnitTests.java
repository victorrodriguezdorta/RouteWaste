package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.demand.QuantityUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class QuantityUnitTests {

    private static final String EMPTY_UNIT_VALUE = "";
    private static final String INVALID_FORMAT_UNIT_VALUE = "kg3";
    private static final String INVALID_NUMBER_UNIT_VALUE = "kg2";
    private static final String INVALID_SPACES_UNIT_VALUE = "k g";
    private static final String INVALID_SYMBOL_UNIT_VALUE = "kg!";
    private static final String KILOGRAMS_UNIT_VALUE = "kg";
    private static final String LITERS_UNIT_VALUE = "liters";
    private static final String QUANTITY_UNIT_EMPTY_MESSAGE = "Quantity unit cannot be empty";
    private static final String QUANTITY_UNIT_INVALID_FORMAT_MESSAGE = "Quantity unit format is invalid";
    private static final String QUANTITY_UNIT_NOT_DEFINED_MESSAGE = "Quantity unit is not defined";
    private static final String QUANTITY_UNIT_STRING_REPRESENTATION = "QuantityUnit={value='kg'}";
    private static final String UNITS_UNIT_VALUE = "units";

    /**
     * Constructor - valid unit
     */
    @Test
    void constructorValidUnit() {
        QuantityUnit unit = new QuantityUnit(KILOGRAMS_UNIT_VALUE);
        assertEquals(KILOGRAMS_UNIT_VALUE, unit.getValue());
    }

    /**
     * Constructor - null unit
     */
    @Test
    void constructorNullUnit() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuantityUnit(null)
        );
        assertEquals(
                QUANTITY_UNIT_NOT_DEFINED_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - empty unit
     */
    @Test
    void constructorEmptyUnit() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuantityUnit(EMPTY_UNIT_VALUE)
        );
        assertEquals(
                QUANTITY_UNIT_EMPTY_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - invalid format (contains numbers)
     */
    @Test
    void constructorInvalidFormatNumbers() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuantityUnit(INVALID_NUMBER_UNIT_VALUE)
        );
        assertEquals(
                QUANTITY_UNIT_INVALID_FORMAT_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - invalid format (contains symbols)
     */
    @Test
    void constructorInvalidFormatSymbols() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuantityUnit(INVALID_SYMBOL_UNIT_VALUE)
        );
        assertEquals(
                QUANTITY_UNIT_INVALID_FORMAT_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - invalid format (contains spaces)
     */
    @Test
    void constructorInvalidFormatSpaces() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new QuantityUnit(INVALID_SPACES_UNIT_VALUE)
        );
        assertEquals(
                QUANTITY_UNIT_INVALID_FORMAT_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Getter method
     */
    @Test
    void getValue() {
        QuantityUnit unit = new QuantityUnit(LITERS_UNIT_VALUE);
        assertEquals(LITERS_UNIT_VALUE, unit.getValue());
    }

    /**
     * setValue - returns new instance (immutability)
     */
    @Test
    void setValueCreatesNewInstance() {
        QuantityUnit original = new QuantityUnit(KILOGRAMS_UNIT_VALUE);
        QuantityUnit updated = original.setValue(UNITS_UNIT_VALUE);
        assertEquals(KILOGRAMS_UNIT_VALUE, original.getValue());
        assertEquals(UNITS_UNIT_VALUE, updated.getValue());
        assertNotSame(original, updated);
    }

    /**
     * setValue - invalid new value
     */
    @Test
    void setValueInvalidValue() {
        QuantityUnit unit = new QuantityUnit(KILOGRAMS_UNIT_VALUE);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> unit.setValue(INVALID_FORMAT_UNIT_VALUE)
        );
        assertEquals(
                QUANTITY_UNIT_INVALID_FORMAT_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        QuantityUnit unit1 = new QuantityUnit(KILOGRAMS_UNIT_VALUE);
        QuantityUnit unit2 = new QuantityUnit(KILOGRAMS_UNIT_VALUE);
        QuantityUnit unit3 = new QuantityUnit(UNITS_UNIT_VALUE);
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
        QuantityUnit unit1 = new QuantityUnit(KILOGRAMS_UNIT_VALUE);
        QuantityUnit unit2 = new QuantityUnit(KILOGRAMS_UNIT_VALUE);
        QuantityUnit unit3 = new QuantityUnit(LITERS_UNIT_VALUE);
        assertEquals(unit1.hashCode(), unit2.hashCode());
        assertNotEquals(unit1.hashCode(), unit3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        QuantityUnit unit = new QuantityUnit(KILOGRAMS_UNIT_VALUE);
        assertEquals(
                QUANTITY_UNIT_STRING_REPRESENTATION,
                unit.toString()
        );
    }
}
