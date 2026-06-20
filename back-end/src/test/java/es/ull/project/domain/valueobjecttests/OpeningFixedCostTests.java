package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class OpeningFixedCostTests {

    private static final String OPENING_FIXED_COST_NOT_DEFINED_MESSAGE = "Opening fixed cost is not defined";
    private static final String NEGATIVE_OPENING_FIXED_COST_MESSAGE = "Opening fixed cost cannot be negative";
    private static final String INVALID_CURRENCY_MESSAGE = "Currency cannot be null or empty";
    private static final String INCOMPATIBLE_CURRENCIES_MESSAGE_PREFIX = "Incompatible currencies: ";
    private static final String INCOMPATIBLE_CURRENCIES_MESSAGE_SEPARATOR = " vs ";

    /**
     * Constructor - valid amount with default currency
     */
    @Test
    void constructorValidAmountDefaultCurrency() {
        OpeningFixedCost cost = new OpeningFixedCost(120.5);
        assertEquals(120.50, cost.getAmount());
        assertEquals(new Currency(), cost.getCurrency().get());
    }

    /**
     * Constructor - valid amount with Currency object
     */
    @Test
    void constructorValidAmountCurrencyObject() {
        Currency currency = new Currency("USD");
        OpeningFixedCost cost = new OpeningFixedCost(300, currency);
        assertEquals(300.00, cost.getAmount());
        assertEquals(currency, cost.getCurrency().get());
    }

    /**
     * Constructor - valid amount with currency string
     */
    @Test
    void constructorValidAmountCurrencyString() {
        OpeningFixedCost cost = new OpeningFixedCost(75, "EUR");
        assertEquals(75.00, cost.getAmount());
        assertEquals(new Currency("EUR"), cost.getCurrency().get());
    }

    /**
     * Constructor - amount not defined (NaN)
     */
    @Test
    void constructorAmountNotDefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningFixedCost(Double.NaN)
        );
        assertEquals(
                OPENING_FIXED_COST_NOT_DEFINED_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - negative amount
     */
    @Test
    void constructorNegativeAmount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningFixedCost(-5)
        );
        assertEquals(
                NEGATIVE_OPENING_FIXED_COST_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - null currency object
     */
    @Test
    void constructorNullCurrencyObject() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningFixedCost(100, (Currency) null)
        );
        assertEquals(
                INVALID_CURRENCY_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - null currency string
     */
    @Test
    void constructorNullCurrencyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningFixedCost(100, (String) null)
        );
        assertEquals(
                INVALID_CURRENCY_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - empty currency string
     */
    @Test
    void constructorEmptyCurrencyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningFixedCost(100, "")
        );
        assertEquals(
                INVALID_CURRENCY_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Add costs with same currency
     */
    @Test
    void addSameCurrency() {
        OpeningFixedCost c1 = new OpeningFixedCost(200, "EUR");
        OpeningFixedCost c2 = new OpeningFixedCost(50, "EUR");
        OpeningFixedCost result = c1.add(c2);
        assertEquals(250.00, result.getAmount());
        assertEquals(c1.getCurrency(), result.getCurrency());
    }

    /**
     * Add costs with different currency
     */
    @Test
    void addDifferentCurrency() {
        OpeningFixedCost c1 = new OpeningFixedCost(100, "EUR");
        OpeningFixedCost c2 = new OpeningFixedCost(50, "USD");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> c1.add(c2)
        );
        assertEquals(
                INCOMPATIBLE_CURRENCIES_MESSAGE_PREFIX + c1.getCurrency().get()
                        + INCOMPATIBLE_CURRENCIES_MESSAGE_SEPARATOR + c2.getCurrency().get(),
                exception.getMessage()
        );
    }

    /**
     * Subtract costs resulting in positive value
     */
    @Test
    void subtractPositiveResult() {
        OpeningFixedCost c1 = new OpeningFixedCost(150, "EUR");
        OpeningFixedCost c2 = new OpeningFixedCost(40, "EUR");
        OpeningFixedCost result = c1.subtract(c2);
        assertEquals(110.00, result.getAmount());
    }

    /**
     * Subtract costs resulting in negative value (clamped to zero)
     */
    @Test
    void subtractNegativeResultClampedToZero() {
        OpeningFixedCost c1 = new OpeningFixedCost(30, "EUR");
        OpeningFixedCost c2 = new OpeningFixedCost(60, "EUR");
        OpeningFixedCost result = c1.subtract(c2);
        assertEquals(0.00, result.getAmount());
    }

    /**
     * greaterThan comparison
     */
    @Test
    void greaterThanComparison() {
        OpeningFixedCost bigger = new OpeningFixedCost(100, "EUR");
        OpeningFixedCost smaller = new OpeningFixedCost(20, "EUR");
        assertTrue(bigger.greaterThan(smaller));
        assertFalse(smaller.greaterThan(bigger));
    }

    /**
     * lessThan comparison
     */
    @Test
    void lessThanComparison() {
        OpeningFixedCost smaller = new OpeningFixedCost(10, "EUR");
        OpeningFixedCost bigger = new OpeningFixedCost(90, "EUR");
        assertTrue(smaller.lessThan(bigger));
        assertFalse(bigger.lessThan(smaller));
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        OpeningFixedCost c1 = new OpeningFixedCost(100, "EUR");
        OpeningFixedCost c2 = new OpeningFixedCost(100, "EUR");
        OpeningFixedCost c3 = new OpeningFixedCost(100, "USD");
        assertEquals(c1, c1);
        assertNotEquals(c1, null);
        assertNotEquals(c1, Integer.valueOf(0));
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        OpeningFixedCost c1 = new OpeningFixedCost(100, "EUR");
        OpeningFixedCost c2 = new OpeningFixedCost(100, "EUR");
        OpeningFixedCost c3 = new OpeningFixedCost(50, "EUR");
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1.hashCode(), c3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        OpeningFixedCost cost = new OpeningFixedCost(99.99, "EUR");
        assertEquals(
                String.format("OpeningFixedCost={amount=%.2f, currency='Currency={code='EUR'}'}", 99.99),
                cost.toString()
        );
    }
}
