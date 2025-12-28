package es.ull.project.domain.valueobject.cost;

import java.util.Objects;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * TransportationVariableCost
 * 
 * Represents the variable cost of transportation per unit distance or per operation.
 * This is a mandatory value object in the domain.
 */
public final class TransportationVariableCost {

    private static final String ERROR_AMOUNT_NOT_DEFINED = "Transportation variable cost is not defined";
    private static final String ERROR_AMOUNT_NEGATIVE = "Transportation variable cost cannot be negative";
    private static final String ERROR_CURRENCY_INVALID = "Currency cannot be null or empty";

    /**
     * Amount of the transportation cost.
     * Required attribute. Always stored with 2 decimal precision.
     */
    private final BigDecimal amount;

    /**
     * Optional currency of the cost. Default: "EUR".
     */
    private final String currency;

    /**
     * Creates a new TransportationVariableCost with a default currency (EUR).
     *
     * @param amount Amount of the transportation cost. Must be ≥ 0.
     */
    public TransportationVariableCost(double amount) {
        this(amount, "EUR");
    }

    /**
     * Creates a new TransportationVariableCost with a specific currency.
     *
     * @param amount   Amount of the transportation cost. Must be ≥ 0.
     * @param currency Currency code (e.g., "EUR", "USD"). Cannot be null/empty.
     */
    public TransportationVariableCost(double amount, String currency) {
        validateAmount(amount);
        validateCurrency(currency);
        this.amount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    private void validateAmount(double amount) {
        if (Double.isNaN(amount)) {
            throw new IllegalArgumentException(ERROR_AMOUNT_NOT_DEFINED);
        }
        if (amount < 0) {
            throw new IllegalArgumentException(ERROR_AMOUNT_NEGATIVE);
        }
    }

    private void validateCurrency(String currency) {
        if (currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException(ERROR_CURRENCY_INVALID);
        }
    }

    public double getAmount() {
        return this.amount.doubleValue();
    }

    public String getCurrency() {
        return this.currency;
    }

    public TransportationVariableCost add(TransportationVariableCost other) {
        checkCurrencyCompatibility(other);
        return new TransportationVariableCost(this.amount.add(other.amount).doubleValue(), this.currency);
    }

    public TransportationVariableCost subtract(TransportationVariableCost other) {
        checkCurrencyCompatibility(other);
        double result = this.amount.subtract(other.amount).doubleValue();
        if (result < 0) {
            result = 0;
        }
        return new TransportationVariableCost(result, this.currency);
    }

    public boolean greaterThan(TransportationVariableCost other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean lessThan(TransportationVariableCost other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    private void checkCurrencyCompatibility(TransportationVariableCost other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot operate on costs with different currencies");
        }
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        TransportationVariableCost other = (TransportationVariableCost) otherObject;
        return this.amount.equals(other.amount) && this.currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("TransportationVariableCost={amount=%.2f, currency='%s'}", this.amount, this.currency);
    }
}