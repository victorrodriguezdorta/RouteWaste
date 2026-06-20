package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class MaximumBudgetTests {

    private static final String MAXIMUM_BUDGET_NOT_DEFINED_MESSAGE = "Maximum budget is not defined";
    private static final String NEGATIVE_MAXIMUM_BUDGET_MESSAGE = "Maximum budget cannot be negative";
    private static final String INVALID_CURRENCY_MESSAGE = "Currency cannot be null or empty";
    private static final String DIFFERENT_CURRENCIES_MESSAGE = "Cannot operate on budgets with different currencies";
    private static final String MAXIMUM_BUDGET_STRING_TEMPLATE =
            "MaximumBudget={amount=%.2f, currency='Currency={code='EUR'}'}";

    /**
     * Constructor - valid amount with default currency
     */
    @Test
    void constructorValidAmountDefaultCurrency() {
        MaximumBudget budget = new MaximumBudget(100.5);
        assertEquals(100.50, budget.getAmount());
        assertEquals(new Currency(), budget.getCurrency().get());
    }

    /**
     * Constructor - valid amount with Currency object
     */
    @Test
    void constructorValidAmountCurrencyObject() {
        Currency currency = new Currency("USD");
        MaximumBudget budget = new MaximumBudget(200, currency);
        assertEquals(200.00, budget.getAmount());
        assertEquals(currency, budget.getCurrency().get());
    }

    /**
     * Constructor - valid amount with currency string
     */
    @Test
    void constructorValidAmountCurrencyString() {
        MaximumBudget budget = new MaximumBudget(50, "EUR");
        assertEquals(50.00, budget.getAmount());
        assertEquals(new Currency("EUR"), budget.getCurrency().get());
    }

    /**
     * Constructor - amount not defined (NaN)
     */
    @Test
    void constructorAmountNotDefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new MaximumBudget(Double.NaN)
        );
        assertEquals(
                MAXIMUM_BUDGET_NOT_DEFINED_MESSAGE,
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
                () -> new MaximumBudget(-10)
        );
        assertEquals(
                NEGATIVE_MAXIMUM_BUDGET_MESSAGE,
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
                () -> new MaximumBudget(100, (Currency) null)
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
                () -> new MaximumBudget(100, (String) null)
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
                () -> new MaximumBudget(100, "")
        );
        assertEquals(
                INVALID_CURRENCY_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Add budgets with same currency
     */
    @Test
    void addSameCurrency() {
        MaximumBudget b1 = new MaximumBudget(100, "EUR");
        MaximumBudget b2 = new MaximumBudget(50, "EUR");
        MaximumBudget result = b1.add(b2);
        assertEquals(150.00, result.getAmount());
        assertEquals(b1.getCurrency(), result.getCurrency());
    }

    /**
     * Add budgets with different currency
     */
    @Test
    void addDifferentCurrency() {
        MaximumBudget b1 = new MaximumBudget(100, "EUR");
        MaximumBudget b2 = new MaximumBudget(50, "USD");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> b1.add(b2)
        );
        assertEquals(
                DIFFERENT_CURRENCIES_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Subtract budgets resulting in positive value
     */
    @Test
    void subtractPositiveResult() {
        MaximumBudget b1 = new MaximumBudget(100, "EUR");
        MaximumBudget b2 = new MaximumBudget(40, "EUR");
        MaximumBudget result = b1.subtract(b2);
        assertEquals(60.00, result.getAmount());
    }

    /**
     * Subtract budgets resulting in negative value (clamped to zero)
     */
    @Test
    void subtractNegativeResultClampedToZero() {
        MaximumBudget b1 = new MaximumBudget(30, "EUR");
        MaximumBudget b2 = new MaximumBudget(50, "EUR");
        MaximumBudget result = b1.subtract(b2);
        assertEquals(0.00, result.getAmount());
    }

    /**
     * greaterThan comparison
     */
    @Test
    void greaterThanComparison() {
        MaximumBudget bigger = new MaximumBudget(100, "EUR");
        MaximumBudget smaller = new MaximumBudget(50, "EUR");
        assertTrue(bigger.greaterThan(smaller));
        assertFalse(smaller.greaterThan(bigger));
    }

    /**
     * lessThan comparison
     */
    @Test
    void lessThanComparison() {
        MaximumBudget smaller = new MaximumBudget(20, "EUR");
        MaximumBudget bigger = new MaximumBudget(80, "EUR");
        assertTrue(smaller.lessThan(bigger));
        assertFalse(bigger.lessThan(smaller));
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        MaximumBudget b1 = new MaximumBudget(100, "EUR");
        MaximumBudget b2 = new MaximumBudget(100, "EUR");
        MaximumBudget b3 = new MaximumBudget(100, "USD");
        assertEquals(b1, b1);
        assertNotEquals(b1, null);
        assertNotEquals(b1, Integer.valueOf(0));
        assertEquals(b1, b2);
        assertNotEquals(b1, b3);
    }

    /**
     * hashCode()
     */
    @Test
    void hashCodeMethod() {
        MaximumBudget b1 = new MaximumBudget(100, "EUR");
        MaximumBudget b2 = new MaximumBudget(100, "EUR");
        MaximumBudget b3 = new MaximumBudget(50, "EUR");
        assertEquals(b1.hashCode(), b2.hashCode());
        assertNotEquals(b1.hashCode(), b3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        MaximumBudget budget = new MaximumBudget(99.99, "EUR");
        assertEquals(
                String.format(MAXIMUM_BUDGET_STRING_TEMPLATE, 99.99),
                budget.toString()
        );
    }
}
