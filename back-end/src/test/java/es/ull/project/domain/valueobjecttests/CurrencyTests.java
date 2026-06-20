package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.cost.Currency;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class CurrencyTests {

    private static final String EUR_CURRENCY_CODE = "EUR";
    private static final String USD_CURRENCY_CODE = "USD";
    private static final String INVALID_CURRENCY_CODE = "EURO";
    private static final String EMPTY_CURRENCY_CODE = "";
    private static final String CURRENCY_NOT_DEFINED_MESSAGE = "Currency is not defined";
    private static final String CURRENCY_EMPTY_MESSAGE = "Currency cannot be empty";
    private static final String CURRENCY_INVALID_FORMAT_MESSAGE = "Currency format is invalid";
    private static final String CURRENCY_STRING_REPRESENTATION = "Currency={code='EUR'}";

    /**
     * Constructor - valid value
     */
    @Test
    void constructorValidValue() {
        Currency currency = new Currency(EUR_CURRENCY_CODE);
        assertEquals(EUR_CURRENCY_CODE, currency.getCode());
    }

    /**
     * Constructor - not defined value
     */
    @Test
    void constructorNotDefinedValueCode() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Currency(null)
        );
        assertEquals(CURRENCY_NOT_DEFINED_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - empty value
     */
    @Test
    void constructorEmptyValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Currency(EMPTY_CURRENCY_CODE)
        );
        assertEquals(CURRENCY_EMPTY_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - wrong format value
     */
    @Test
    void constructorWrongFormatValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Currency(INVALID_CURRENCY_CODE)
        );
        assertEquals(CURRENCY_INVALID_FORMAT_MESSAGE, exception.getMessage());
    }

    /**
     * Getter
     */
    @Test
    void getterCode() {
        Currency currency = new Currency(USD_CURRENCY_CODE);
        assertEquals(USD_CURRENCY_CODE, currency.getCode());
    }

    /**
     * Setter - required attribute
     */
    @Test
    void setterCode() {
        Currency original = new Currency(EUR_CURRENCY_CODE);
        Currency newCurrency = original.setCode(USD_CURRENCY_CODE);
        assertNotSame(original, newCurrency);
        assertEquals(USD_CURRENCY_CODE, newCurrency.getCode());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> original.setCode(null)
        );
        assertEquals(CURRENCY_NOT_DEFINED_MESSAGE, exception.getMessage());
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        Currency currency1 = new Currency(EUR_CURRENCY_CODE);
        Currency currency2 = new Currency(EUR_CURRENCY_CODE);
        Currency currency3 = new Currency(USD_CURRENCY_CODE);
        assertEquals(currency1, currency1);
        assertNotEquals(currency1, null);
        assertNotEquals(currency1, Integer.valueOf(0));
        assertEquals(currency1, currency2);
        assertNotEquals(currency1, currency3);
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        Currency currency1 = new Currency(EUR_CURRENCY_CODE);
        Currency currency2 = new Currency(EUR_CURRENCY_CODE);
        Currency currency3 = new Currency(USD_CURRENCY_CODE);
        assertEquals(currency1.hashCode(), currency2.hashCode());
        assertNotEquals(currency1.hashCode(), currency3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        Currency currency = new Currency(EUR_CURRENCY_CODE);
        assertEquals(CURRENCY_STRING_REPRESENTATION, currency.toString());
    }
}
