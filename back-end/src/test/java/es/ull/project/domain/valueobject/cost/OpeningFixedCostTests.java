package es.ull.project.domain.valueobject.cost;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpeningFixedCostTests {

    /**
     * Constructor - valid amount with default currency
     */
    @Test
    void constructor_validAmount_defaultCurrency() {
        OpeningFixedCost cost = new OpeningFixedCost(120.5);

        assertEquals(120.50, cost.getAmount());
        assertEquals(new Currency(), cost.getCurrency());
    }

    /**
     * Constructor - valid amount with Currency object
     */
    @Test
    void constructor_validAmount_currencyObject() {
        Currency currency = new Currency("USD");
        OpeningFixedCost cost = new OpeningFixedCost(300, currency);

        assertEquals(300.00, cost.getAmount());
        assertEquals(currency, cost.getCurrency());
    }

    /**
     * Constructor - valid amount with currency string
     */
    @Test
    void constructor_validAmount_currencyString() {
        OpeningFixedCost cost = new OpeningFixedCost(75, "EUR");

        assertEquals(75.00, cost.getAmount());
        assertEquals(new Currency("EUR"), cost.getCurrency());
    }

    /**
     * Constructor - amount not defined (NaN)
     */
    @Test
    void constructor_amountNotDefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningFixedCost(Double.NaN)
        );

        assertEquals(
                "Opening fixed cost is not defined",
                exception.getMessage()
        );
    }

    /**
     * Constructor - negative amount
     */
    @Test
    void constructor_negativeAmount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningFixedCost(-5)
        );

        assertEquals(
                "Opening fixed cost cannot be negative",
                exception.getMessage()
        );
    }

    /**
     * Constructor - null currency object
     */
    @Test
    void constructor_nullCurrencyObject() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningFixedCost(100, (Currency) null)
        );

        assertEquals(
                "Currency cannot be null or empty",
                exception.getMessage()
        );
    }

    /**
     * Constructor - null currency string
     */
    @Test
    void constructor_nullCurrencyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningFixedCost(100, (String) null)
        );

        assertEquals(
                "Currency cannot be null or empty",
                exception.getMessage()
        );
    }

    /**
     * Constructor - empty currency string
     */
    @Test
    void constructor_emptyCurrencyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningFixedCost(100, "")
        );

        assertEquals(
                "Currency cannot be null or empty",
                exception.getMessage()
        );
    }

    /**
     * Add costs with same currency
     */
    @Test
    void add_sameCurrency() {
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
    void add_differentCurrency() {
        OpeningFixedCost c1 = new OpeningFixedCost(100, "EUR");
        OpeningFixedCost c2 = new OpeningFixedCost(50, "USD");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> c1.add(c2)
        );

        assertEquals(
                "Incompatible currencies: " + c1.getCurrency() + " vs " + c2.getCurrency(),
                exception.getMessage()
        );
    }

    /**
     * Subtract costs resulting in positive value
     */
    @Test
    void subtract_positiveResult() {
        OpeningFixedCost c1 = new OpeningFixedCost(150, "EUR");
        OpeningFixedCost c2 = new OpeningFixedCost(40, "EUR");

        OpeningFixedCost result = c1.subtract(c2);

        assertEquals(110.00, result.getAmount());
    }

    /**
     * Subtract costs resulting in negative value (clamped to zero)
     */
    @Test
    void subtract_negativeResultClampedToZero() {
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
                "OpeningFixedCost={amount=99.99, currency='Currency={code='EUR'}'}",
                cost.toString()
        );
    }
}
