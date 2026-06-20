package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.TotalCost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TotalCostTests {

    private static final String TOTAL_COST_NOT_DEFINED_MESSAGE = "Total cost is not defined";
    private static final String NEGATIVE_TOTAL_COST_MESSAGE = "Total cost cannot be negative";
    private static final String INVALID_CURRENCY_MESSAGE = "Currency cannot be null or empty";
    private static final String DIFFERENT_CURRENCIES_MESSAGE = "Cannot operate on costs with different currencies";
    private static final String TOTAL_COST_STRING_TEMPLATE =
            "TotalCost={amount=%.2f, currency='Currency={code='EUR'}'}";

    /**
     * Constructor - valid value with default currency
     */
    @Test
    void constructorValidValueDefaultCurrency() {
        TotalCost cost = new TotalCost(10.5);
        assertEquals(10.50, cost.getAmount());
        assertEquals(new Currency("EUR"), cost.getCurrency().get());
    }

    /**
     * Constructor - valid value with Currency object
     */
    @Test
    void constructorValidValueWithCurrencyObject() {
        Currency currency = new Currency("USD");
        TotalCost cost = new TotalCost(20.0, currency);
        assertEquals(20.00, cost.getAmount());
        assertEquals(currency, cost.getCurrency().get());
    }

    /**
     * Constructor - valid value with currency string
     */
    @Test
    void constructorValidValueWithCurrencyString() {
        TotalCost cost = new TotalCost(15.25, "EUR");
        assertEquals(15.25, cost.getAmount());
        assertEquals(new Currency("EUR"), cost.getCurrency().get());
    }

    /**
     * Constructor - amount not defined (NaN)
     */
    @Test
    void constructorAmountNotDefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TotalCost(Double.NaN)
        );
        assertEquals(TOTAL_COST_NOT_DEFINED_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - negative amount
     */
    @Test
    void constructorNegativeAmount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TotalCost(-5.0)
        );
        assertEquals(NEGATIVE_TOTAL_COST_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - null currency object
     */
    @Test
    void constructorNullCurrencyObject() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TotalCost(10.0, (Currency) null)
        );
        assertEquals(INVALID_CURRENCY_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - null currency string
     */
    @Test
    void constructorNullCurrencyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TotalCost(10.0, (String) null)
        );
        assertEquals(INVALID_CURRENCY_MESSAGE, exception.getMessage());
    }

    /**
     * Constructor - empty currency string
     */
    @Test
    void constructorEmptyCurrencyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TotalCost(10.0, "")
        );
        assertEquals(INVALID_CURRENCY_MESSAGE, exception.getMessage());
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
    void addSameCurrency() {
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
    void addDifferentCurrency() {
        TotalCost cost1 = new TotalCost(10.0, "EUR");
        TotalCost cost2 = new TotalCost(5.0, "USD");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cost1.add(cost2)
        );
        assertEquals(DIFFERENT_CURRENCIES_MESSAGE, exception.getMessage());
    }

    /**
     * Subtract operation - result positive
     */
    @Test
    void subtractPositiveResult() {
        TotalCost cost1 = new TotalCost(20.0, "EUR");
        TotalCost cost2 = new TotalCost(5.0, "EUR");
        TotalCost result = cost1.subtract(cost2);
        assertEquals(15.00, result.getAmount());
    }

    /**
     * Subtract operation - result negative becomes zero
     */
    @Test
    void subtractNegativeResultBecomesZero() {
        TotalCost cost1 = new TotalCost(5.0, "EUR");
        TotalCost cost2 = new TotalCost(10.0, "EUR");
        TotalCost result = cost1.subtract(cost2);
        assertEquals(0.00, result.getAmount());
    }

    /**
     * greaterThan and lessThan with TotalCost
     */
    @Test
    void comparisonWithTotalCost() {
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
    void comparisonWithMaximumBudget() {
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
                String.format(TOTAL_COST_STRING_TEMPLATE, 12.5),
                cost.toString()
        );
    }
}
