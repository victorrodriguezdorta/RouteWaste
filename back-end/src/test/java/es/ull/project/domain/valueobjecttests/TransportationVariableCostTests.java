package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TransportationVariableCostTests {

    private static final String AMOUNT_NOT_DEFINED_MESSAGE = "Transportation variable cost is not defined";
    private static final String NEGATIVE_AMOUNT_MESSAGE = "Transportation variable cost cannot be negative";
    private static final String ZERO_AMOUNT_MESSAGE = "Transportation variable cost cannot be zero";
    private static final String CURRENCY_INVALID_MESSAGE = "Currency cannot be null or empty";
    private static final String CURRENCY_MISMATCH_MESSAGE = "Cannot operate on costs with different currencies";
    private static final String INVALID_RESULT_MESSAGE = "Result of operation would be zero or negative";

    /**
     * Constructor - valid value with default currency
     */
    @Test
    void constructorValidValueDefaultCurrency() {
        TransportationVariableCost cost = new TransportationVariableCost(10.5);
        assertEquals(10.50, cost.getAmount());
        assertEquals(new Currency("EUR"), cost.getCurrency().get());
    }

    /**
     * Constructor - valid value with Currency object
     */
    @Test
    void constructorValidValueWithCurrencyObject() {
        Currency currency = new Currency("USD");
        TransportationVariableCost cost = new TransportationVariableCost(20.0, currency);
        assertEquals(20.00, cost.getAmount());
        assertEquals(currency, cost.getCurrency().get());
    }

    /**
     * Constructor - valid value with currency string
     */
    @Test
    void constructorValidValueWithCurrencyString() {
        TransportationVariableCost cost = new TransportationVariableCost(15.25, "EUR");
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
                () -> new TransportationVariableCost(Double.NaN)
        );
        assertEquals(
                AMOUNT_NOT_DEFINED_MESSAGE,
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
                () -> new TransportationVariableCost(-5.0)
        );
        assertEquals(
                NEGATIVE_AMOUNT_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Constructor - zero amount
     */
    @Test
    void constructorZeroAmount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TransportationVariableCost(0.0)
        );
        assertEquals(
                ZERO_AMOUNT_MESSAGE,
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
                () -> new TransportationVariableCost(10.0, (Currency) null)
        );
        assertEquals(
                CURRENCY_INVALID_MESSAGE,
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
                () -> new TransportationVariableCost(10.0, (String) null)
        );
        assertEquals(
                CURRENCY_INVALID_MESSAGE,
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
                () -> new TransportationVariableCost(10.0, "")
        );
        assertEquals(
                CURRENCY_INVALID_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Getter methods
     */
    @Test
    void getters() {
        TransportationVariableCost cost = new TransportationVariableCost(30.75, "USD");
        assertEquals(30.75, cost.getAmount());
        assertEquals(new Currency("USD"), cost.getCurrency().get());
    }

    /**
     * Add operation - same currency
     */
    @Test
    void addSameCurrency() {
        TransportationVariableCost cost1 = new TransportationVariableCost(10.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(5.5, "EUR");
        TransportationVariableCost result = cost1.add(cost2);
        assertEquals(15.50, result.getAmount());
        assertEquals(new Currency("EUR"), result.getCurrency().get());
    }

    /**
     * Add operation - different currency
     */
    @Test
    void addDifferentCurrency() {
        TransportationVariableCost cost1 = new TransportationVariableCost(10.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(5.0, "USD");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cost1.add(cost2)
        );
        assertEquals(
                CURRENCY_MISMATCH_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Subtract operation - result positive
     */
    @Test
    void subtractPositiveResult() {
        TransportationVariableCost cost1 = new TransportationVariableCost(20.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(5.0, "EUR");
        TransportationVariableCost result = cost1.subtract(cost2);
        assertEquals(15.00, result.getAmount());
    }

    /**
     * Subtract operation - result negative throws exception
     */
    @Test
    void subtractNegativeResultThrowsException() {
        TransportationVariableCost cost1 = new TransportationVariableCost(5.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(10.0, "EUR");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cost1.subtract(cost2)
        );
        assertEquals(
                INVALID_RESULT_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * Subtract operation - result zero throws exception
     */
    @Test
    void subtractZeroResultThrowsException() {
        TransportationVariableCost cost1 = new TransportationVariableCost(10.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(10.0, "EUR");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cost1.subtract(cost2)
        );
        assertEquals(
                INVALID_RESULT_MESSAGE,
                exception.getMessage()
        );
    }

    /**
     * greaterThan and lessThan
     */
    @Test
    void comparisonMethods() {
        TransportationVariableCost lower = new TransportationVariableCost(10.0, "EUR");
        TransportationVariableCost higher = new TransportationVariableCost(20.0, "EUR");
        assertTrue(higher.greaterThan(lower));
        assertFalse(lower.greaterThan(higher));
        assertTrue(lower.lessThan(higher));
        assertFalse(higher.lessThan(lower));
    }

    /**
     * equals()
     */
    @Test
    void equalsMethod() {
        TransportationVariableCost cost1 = new TransportationVariableCost(10.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(10.0, "EUR");
        TransportationVariableCost cost3 = new TransportationVariableCost(10.0, "USD");
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
        TransportationVariableCost cost1 = new TransportationVariableCost(10.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(10.0, "EUR");
        TransportationVariableCost cost3 = new TransportationVariableCost(20.0, "EUR");
        assertEquals(cost1.hashCode(), cost2.hashCode());
        assertNotEquals(cost1.hashCode(), cost3.hashCode());
    }

    /**
     * toString()
     */
    @Test
    void toStringMethod() {
        TransportationVariableCost cost = new TransportationVariableCost(12.5, "EUR");
        assertEquals(
                String.format("TransportationVariableCost={amount=%.2f, currency='Currency={code='EUR'}'}", 12.5),
                cost.toString()
        );
    }
}
