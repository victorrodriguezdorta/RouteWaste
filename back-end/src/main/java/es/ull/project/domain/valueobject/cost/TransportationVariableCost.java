package es.ull.project.domain.valueobject.cost;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

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
    private static final String ERROR_CURRENCY_MISMATCH = "Cannot operate on costs with different currencies";
    private static final int ZERO = 0;

    /**
     * Amount of the transportation cost.
     * Required. Always stored with 2 decimal precision.
     * @required
     */
    private final BigDecimal amount;

    /**
     * Optional. Currency of the cost. Default: "EUR".
     * @optional
     */
    private final Currency currency;

    /**
     * Creates a new TransportationVariableCost with a default currency (EUR).
     *
     * @param amount Amount of the transportation cost. Must be ≥ 0.
     */
    public TransportationVariableCost(double amount) {
        this(amount, new Currency());
    }

    /**
     * Creates a new TransportationVariableCost with a specific currency. (BETTER)
     *
     * @param amount   Amount of the transportation cost. Must be ≥ 0.
     * @param currency Currency code (e.g., "EUR", "USD"). Cannot be null/empty.
     */
    public TransportationVariableCost(double amount, Currency currency) {
        validateAmount(amount);
        validateCurrency(currency);
        this.amount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    /**
     * Creates a new TransportationVariableCost with a specific currency.
     *
     * @param amount   Amount of the transportation cost. Must be ≥ 0.
     * @param currency Currency code (e.g., "EUR", "USD"). Cannot be null/empty. It creates a Currency object internally.
     */
    public TransportationVariableCost(double amount, String currency) {
        validateAmount(amount);
        validateCurrency(currency);
        this.amount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        this.currency = new Currency(currency);
    }

    /**
     * Validates that the amount is defined and non-negative.
     *
     * @param amount Amount to validate.
     * @throws IllegalArgumentException if amount is NaN or negative.
     */
    private void validateAmount(double amount) {
        if (Double.isNaN(amount)) {
            throw new IllegalArgumentException(ERROR_AMOUNT_NOT_DEFINED);
        }
        if (amount < ZERO) {
            throw new IllegalArgumentException(ERROR_AMOUNT_NEGATIVE);
        }
    }

    /**
     * Validates that the currency object is not null.
     *
     * @param currency Currency object to validate.
     * @throws IllegalArgumentException if currency is null.
     */
    private void validateCurrency(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException(ERROR_CURRENCY_INVALID);
        }
    }

    /**
     * Validates that the currency string is not null or empty.
     *
     * @param currency Currency string to validate.
     * @throws IllegalArgumentException if currency is null or empty.
     */
    private void validateCurrency(String currency) {
        if (currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException(ERROR_CURRENCY_INVALID);
        }
    }

    /**
     * Returns the amount of the transportation cost.
     *
     * @return Amount as a double value.
     */
    public double getAmount() {
        return this.amount.doubleValue();
    }

    /**
     * Returns the currency of the transportation cost.
     *
     * @return Currency object.
     */
    public Currency getCurrency() {
        return this.currency;
    }

    /**
     * Adds another transportation cost to this one.
     * Both costs must have the same currency.
     *
     * @param other The transportation cost to add.
     * @return New TransportationVariableCost with the sum.
     * @throws IllegalArgumentException if currencies don't match.
     */
    public TransportationVariableCost add(TransportationVariableCost other) {
        checkCurrencyCompatibility(other);
        return new TransportationVariableCost(this.amount.add(other.amount).doubleValue(), this.currency);
    }

    /**
     * Subtracts another transportation cost from this one.
     * Both costs must have the same currency. Result cannot be negative (minimum is 0).
     *
     * @param other The transportation cost to subtract.
     * @return New TransportationVariableCost with the difference (minimum 0).
     * @throws IllegalArgumentException if currencies don't match.
     */
    public TransportationVariableCost subtract(TransportationVariableCost other) {
        checkCurrencyCompatibility(other);
        double result = this.amount.subtract(other.amount).doubleValue();
        if (result < ZERO) {
            result = ZERO;
        }
        return new TransportationVariableCost(result, this.currency);
    }

    /**
     * Checks if this cost is greater than another cost.
     * Both costs must have the same currency.
     *
     * @param other The transportation cost to compare with.
     * @return True if this cost is greater, false otherwise.
     * @throws IllegalArgumentException if currencies don't match.
     */
    public boolean greaterThan(TransportationVariableCost other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) > ZERO;
    }

    /**
     * Checks if this cost is less than another cost.
     * Both costs must have the same currency.
     *
     * @param other The transportation cost to compare with.
     * @return True if this cost is less, false otherwise.
     * @throws IllegalArgumentException if currencies don't match.
     */
    public boolean lessThan(TransportationVariableCost other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) < ZERO;
    }

    /**
     * Checks if the currency of this cost matches another cost's currency.
     *
     * @param other The transportation cost to check against.
     * @throws IllegalArgumentException if currencies don't match.
     */
    private void checkCurrencyCompatibility(TransportationVariableCost other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(ERROR_CURRENCY_MISMATCH);
        }
    }

    /**
     * Checks equality based on amount and currency.
     *
     * @param otherObject Object to compare with.
     * @return True if amounts and currencies are equal, false otherwise.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        TransportationVariableCost other = (TransportationVariableCost) otherObject;
        return this.amount.equals(other.amount) && this.currency.equals(other.currency);
    }

    /**
     * Generates hash code based on amount and currency.
     *
     * @return Hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    /**
     * Returns a string representation of the transportation variable cost.
     *
     * @return Formatted string with amount and currency.
     */
    @Override
    public String toString() {
        return String.format("TransportationVariableCost={amount=%.2f, currency='%s'}", this.amount, this.currency);
    }
}