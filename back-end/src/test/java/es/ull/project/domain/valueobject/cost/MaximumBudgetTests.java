package es.ull.project.domain.valueobject.cost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MaximumBudgetTests {

    /**
     * Constructor - valid amount with default currency
     */
    @Test
    void constructor_validAmount_defaultCurrency() {
        MaximumBudget budget = new MaximumBudget(100.5);

        assertEquals(100.50, budget.getAmount());
        assertEquals(new Currency(), budget.getCurrency().get());
    }

    /**
     * Constructor - valid amount with Currency object
     */
    @Test
    void constructor_validAmount_currencyObject() {
        Currency currency = new Currency("USD");
        MaximumBudget budget = new MaximumBudget(200, currency);

        assertEquals(200.00, budget.getAmount());
        assertEquals(currency, budget.getCurrency().get());
    }

    /**
     * Constructor - valid amount with currency string
     */
    @Test
    void constructor_validAmount_currencyString() {
        MaximumBudget budget = new MaximumBudget(50, "EUR");

        assertEquals(50.00, budget.getAmount());
        assertEquals(new Currency("EUR"), budget.getCurrency().get());
    }

    /**
     * Constructor - amount not defined (NaN)
     */
    @Test
    void constructor_amountNotDefined() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new MaximumBudget(Double.NaN)
        );

        assertEquals(
                "Maximum budget is not defined",
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
                () -> new MaximumBudget(-10)
        );

        assertEquals(
                "Maximum budget cannot be negative",
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
                () -> new MaximumBudget(100, (Currency) null)
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
                () -> new MaximumBudget(100, (String) null)
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
                () -> new MaximumBudget(100, "")
        );

        assertEquals(
                "Currency cannot be null or empty",
                exception.getMessage()
        );
    }

    /**
     * Add budgets with same currency
     */
    @Test
    void add_sameCurrency() {
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
    void add_differentCurrency() {
        MaximumBudget b1 = new MaximumBudget(100, "EUR");
        MaximumBudget b2 = new MaximumBudget(50, "USD");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> b1.add(b2)
        );

        assertEquals(
                "Cannot operate on budgets with different currencies",
                exception.getMessage()
        );
    }

    /**
     * Subtract budgets resulting in positive value
     */
    @Test
    void subtract_positiveResult() {
        MaximumBudget b1 = new MaximumBudget(100, "EUR");
        MaximumBudget b2 = new MaximumBudget(40, "EUR");

        MaximumBudget result = b1.subtract(b2);

        assertEquals(60.00, result.getAmount());
    }

    /**
     * Subtract budgets resulting in negative value (clamped to zero)
     */
    @Test
    void subtract_negativeResultClampedToZero() {
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
        System.out.println(budget.toString());

        assertEquals(
                "MaximumBudget={amount=99.99, currency='Currency={code='EUR'}'}",
                budget.toString()
        );
    }
}
