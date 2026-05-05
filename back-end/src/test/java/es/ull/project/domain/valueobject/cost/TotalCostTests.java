package es.ull.project.domain.valueobject.cost;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TotalCostTests {

    /**
     * Constructor - valid value with default currency
     */
    @Test
    void constructor_validValue_defaultCurrency() {
        TotalCost cost = new TotalCost(10.5);

        assertEquals(10.50, cost.getAmount());
        assertEquals(new Currency("EUR"), cost.getCurrency().get());
    }

    /**
     * Constructor - valid value with Currency object
     */
    @Test
    void constructor_validValue_withCurrencyObject() {
        Currency currency = new Currency("USD");
        TotalCost cost = new TotalCost(20.0, currency);

        assertEquals(20.00, cost.getAmount());
        assertEquals(currency, cost.getCurrency().get());
    }

    /**
     * Constructor - valid value with currency string
     */
    @Test
    void constructor_validValue_withCurrencyString() {
        TotalCost cost = new TotalCost(15.25, "EUR");

        assertEquals(15.25, cost.getAmount());
        assertEquals(new Currency("EUR"), cost.getCurrency().get());
    }

    /**
     * Constructor - amount not defined (NaN)
     */
    @Test
    void constructor_amountNotDefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TotalCost(Double.NaN)
        );

        assertEquals(
                "Total cost is not defined",
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
                () -> new TotalCost(-5.0)
        );

        assertEquals(
                "Total cost cannot be negative",
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
                () -> new TotalCost(10.0, (Currency) null)
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
                () -> new TotalCost(10.0, (String) null)
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
                () -> new TotalCost(10.0, "")
        );

        assertEquals(
                "Currency cannot be null or empty",
                exception.getMessage()
        );
    }

    /**
     * Getter methods
     */
    @Test
    void getters() {
        TotalCost cost = new TotalCost(30.75, "USD");

        assertEquals(30.75, cost.getAmount());
        assertEquals(new Currency("USD"), cost.getCurrency().get());
    }

    /**
     * Add operation - same currency
     */
    @Test
    void add_sameCurrency() {
        TotalCost cost1 = new TotalCost(10.0, "EUR");
        TotalCost cost2 = new TotalCost(5.5, "EUR");

        TotalCost result = cost1.add(cost2);

        assertEquals(15.50, result.getAmount());
        assertEquals(new Currency("EUR"), result.getCurrency().get());
    }

    /**
     * Add operation - different currency
     */
    @Test
    void add_differentCurrency() {
        TotalCost cost1 = new TotalCost(10.0, "EUR");
        TotalCost cost2 = new TotalCost(5.0, "USD");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cost1.add(cost2)
        );

        assertEquals(
                "Cannot operate on costs with different currencies",
                exception.getMessage()
        );
    }

    /**
     * Subtract operation - result positive
     */
    @Test
    void subtract_positiveResult() {
        TotalCost cost1 = new TotalCost(20.0, "EUR");
        TotalCost cost2 = new TotalCost(5.0, "EUR");

        TotalCost result = cost1.subtract(cost2);

        assertEquals(15.00, result.getAmount());
    }

    /**
     * Subtract operation - result negative becomes zero
     */
    @Test
    void subtract_negativeResultBecomesZero() {
        TotalCost cost1 = new TotalCost(5.0, "EUR");
        TotalCost cost2 = new TotalCost(10.0, "EUR");

        TotalCost result = cost1.subtract(cost2);

        assertEquals(0.00, result.getAmount());
    }

    /**
     * greaterThan and lessThan with TotalCost
     */
    @Test
    void comparison_withTotalCost() {
        TotalCost lower = new TotalCost(10.0, "EUR");
        TotalCost higher = new TotalCost(20.0, "EUR");

        assertTrue(higher.greaterThan(lower));
        assertFalse(lower.greaterThan(higher));

        assertTrue(lower.lessThan(higher));
        assertFalse(higher.lessThan(lower));
    }

    /**
     * greaterThan and lessThan with MaximumBudget
     */
    @Test
    void comparison_withMaximumBudget() {
        TotalCost cost = new TotalCost(50.0, "EUR");
        MaximumBudget budget = new MaximumBudget(100.0, "EUR");

        assertFalse(cost.greaterThan(budget));
        assertTrue(cost.lessThan(budget));
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        TotalCost cost1 = new TotalCost(10.0, "EUR");
        TotalCost cost2 = new TotalCost(10.0, "EUR");
        TotalCost cost3 = new TotalCost(10.0, "USD");

        assertEquals(cost1, cost1);
        assertNotEquals(cost1, null);
        assertNotEquals(cost1, Integer.valueOf(0));
        assertEquals(cost1, cost2);
        assertNotEquals(cost1, cost3);
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        TotalCost cost1 = new TotalCost(10.0, "EUR");
        TotalCost cost2 = new TotalCost(10.0, "EUR");
        TotalCost cost3 = new TotalCost(20.0, "EUR");

        assertEquals(cost1.hashCode(), cost2.hashCode());
        assertNotEquals(cost1.hashCode(), cost3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        TotalCost cost = new TotalCost(12.5, "EUR");

        assertEquals(
                String.format("TotalCost={amount=%.2f, currency='Currency={code='EUR'}'}", 12.5),
                cost.toString()
        );
    }
}
