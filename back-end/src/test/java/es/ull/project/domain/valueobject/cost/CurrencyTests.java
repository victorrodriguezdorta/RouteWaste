package es.ull.project.domain.valueobject.cost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CurrencyTests {

    /**
     * Constructor - valid value
     */
    @Test
    void constructor_validValue() {
        Currency currency = new Currency("EUR");

        assertEquals("EUR", currency.getCode());
    }

    /**
     * Constructor - not defined value
     */
    @Test
    void constructor_notDefinedValue_code() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Currency(null)
        );

        assertEquals(
                "Currency is not defined",
                exception.getMessage()
        );
    }

    /**
     * Constructor - empty value
     */
    @Test
    void constructor_emptyValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Currency("")
        );

        assertEquals(
                "Currency cannot be empty",
                exception.getMessage()
        );
    }

    /**
     * Constructor - wrong format value
     */
    @Test
    void constructor_wrongFormatValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Currency("EURO")
        );

        assertEquals(
                "Currency format is invalid",
                exception.getMessage()
        );
    }

    /**
     * Getter
     */
    @Test
    void getter_code() {
        Currency currency = new Currency("USD");

        assertEquals("USD", currency.getCode());
    }

    /**
     * Setter - required attribute
     */
    @Test
    void setter_code() {
        Currency original = new Currency("EUR");
        Currency newCurrency = original.setCode("USD");

        assertNotSame(original, newCurrency);
        assertEquals("USD", newCurrency.getCode());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> original.setCode(null)
        );

        assertEquals(
                "Currency is not defined",
                exception.getMessage()
        );
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        Currency currency1 = new Currency("EUR");
        Currency currency2 = new Currency("EUR");
        Currency currency3 = new Currency("USD");

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
        Currency currency1 = new Currency("EUR");
        Currency currency2 = new Currency("EUR");
        Currency currency3 = new Currency("USD");

        assertEquals(currency1.hashCode(), currency2.hashCode());
        assertNotEquals(currency1.hashCode(), currency3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        Currency currency = new Currency("EUR");

        assertEquals(
                "Currency={code='EUR'}",
                currency.toString()
        );
    }
}
