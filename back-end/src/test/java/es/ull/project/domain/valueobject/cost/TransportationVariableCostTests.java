package es.ull.project.domain.valueobject.cost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TransportationVariableCostTests {

    /**
     * Constructor - valid value with default currency
     */
    @Test
    void constructor_validValue_defaultCurrency() {
        TransportationVariableCost cost = new TransportationVariableCost(10.5);

        assertEquals(10.50, cost.getAmount());
        assertEquals(new Currency("EUR"), cost.getCurrency().get());
    }

    /**
     * Constructor - valid value with Currency object
     */
    @Test
    void constructor_validValue_withCurrencyObject() {
        Currency currency = new Currency("USD");
        TransportationVariableCost cost = new TransportationVariableCost(20.0, currency);

        assertEquals(20.00, cost.getAmount());
        assertEquals(currency, cost.getCurrency().get());
    }

    /**
     * Constructor - valid value with currency string
     */
    @Test
    void constructor_validValue_withCurrencyString() {
        TransportationVariableCost cost = new TransportationVariableCost(15.25, "EUR");

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
                () -> new TransportationVariableCost(Double.NaN)
        );

        assertEquals(
                "Transportation variable cost is not defined",
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
                () -> new TransportationVariableCost(-5.0)
        );

        assertEquals(
                "Transportation variable cost cannot be negative",
                exception.getMessage()
        );
    }

    /**
     * Constructor - zero amount
     */
    @Test
    void constructor_zeroAmount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TransportationVariableCost(0.0)
        );

        assertEquals(
                "Transportation variable cost cannot be zero",
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
                () -> new TransportationVariableCost(10.0, (Currency) null)
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
                () -> new TransportationVariableCost(10.0, (String) null)
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
                () -> new TransportationVariableCost(10.0, "")
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
        TransportationVariableCost cost = new TransportationVariableCost(30.75, "USD");

        assertEquals(30.75, cost.getAmount());
        assertEquals(new Currency("USD"), cost.getCurrency().get());
    }

    /**
     * Add operation - same currency
     */
    @Test
    void add_sameCurrency() {
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
    void add_differentCurrency() {
        TransportationVariableCost cost1 = new TransportationVariableCost(10.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(5.0, "USD");

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
        TransportationVariableCost cost1 = new TransportationVariableCost(20.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(5.0, "EUR");

        TransportationVariableCost result = cost1.subtract(cost2);

        assertEquals(15.00, result.getAmount());
    }

    /**
     * Subtract operation - result negative throws exception
     */
    @Test
    void subtract_negativeResultThrowsException() {
        TransportationVariableCost cost1 = new TransportationVariableCost(5.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(10.0, "EUR");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cost1.subtract(cost2)
        );

        assertEquals(
                "Result of operation would be zero or negative",
                exception.getMessage()
        );
    }

    /**
     * Subtract operation - result zero throws exception
     */
    @Test
    void subtract_zeroResultThrowsException() {
        TransportationVariableCost cost1 = new TransportationVariableCost(10.0, "EUR");
        TransportationVariableCost cost2 = new TransportationVariableCost(10.0, "EUR");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cost1.subtract(cost2)
        );

        assertEquals(
                "Result of operation would be zero or negative",
                exception.getMessage()
        );
    }

    /**
     * greaterThan and lessThan
     */
    @Test
    void comparison_methods() {
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
