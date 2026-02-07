package es.ull.project.domain.valueobject.cost;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * MaximumBudget
 * 
 * Represents the maximum allowed budget in the domain.
 * This is a mandatory value object in the domain.
 */
public final class MaximumBudget {

    private static final String ERROR_AMOUNT_NOT_DEFINED = "Maximum budget is not defined";
    private static final String ERROR_AMOUNT_NEGATIVE = "Maximum budget cannot be negative";
    private static final String ERROR_CURRENCY_INVALID = "Currency cannot be null or empty";
    private static final String ERROR_CURRENCY_MISMATCH = "Cannot operate on budgets with different currencies";
    private static final int ZERO = 0;

    /**
     * Required. Amount of the maximum budget.
     * Always stored with 2 decimal precision.
     * @required
     */
    private final BigDecimal amount;

    /**
     * Optional. Currency of the budget. Default: "EUR" when not provided.
     * @optional
     */
    private final Currency currency;

    /**
     * Creates a new MaximumBudget with default currency (EUR).
     *
     * @param amount Amount of the maximum budget. Must be ≥ 0.
     */
    public MaximumBudget(double amount) {
        this(amount, new Currency());
    }

    /**
     * Creates a new MaximumBudget with specific Currency object. (BETTER)
     *
     * @param amount   Amount of the maximum budget. Must be ≥ 0.
     * @param currency Currency object. Cannot be null.
     */
    public MaximumBudget(double amount, Currency currency) {
        validateAmount(amount);
        validateCurrency(currency);
        Objects.requireNonNull(currency, ERROR_CURRENCY_INVALID);
        this.amount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    /**
     * Creates a new MaximumBudget with specific currency.
     *
     * @param amount   Amount of the maximum budget. Must be ≥ 0.
     * @param currency Currency but as String. Cannot be null or empty. it creates a Currency object internally.
     */
    public MaximumBudget(double amount, String currency) {
        validateAmount(amount);
        validateCurrencyString(currency);
        this.amount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        this.currency = new Currency(currency);
    }

    /**
     * Validates the amount is defined and non-negative.
     * @param amount Amount to validate.
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
     * Validates the currency is not null or empty.
     * @param currency Currency to validate.
     */
    private void validateCurrency(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException(ERROR_CURRENCY_INVALID);
        }
    }

    /**
     * Validates the currency string is not null or empty.
     * @param currency Currency string to validate.
     */
    private void validateCurrencyString(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_CURRENCY_INVALID);
        }
    }

    /**
     * Returns the amount of the maximum budget.
     * @return Amount as double.
     */
    public double getAmount() {
        return this.amount.doubleValue();
    }

    /**
     * Returns the currency of the maximum budget.
     * @return Currency object.
     */
    public Currency getCurrency() {
        return this.currency;
    }

    /**
     * Returns a new MaximumBudget with the sum of this and another budget.
     *
     * @param other Other MaximumBudget to add.
     * @return New MaximumBudget representing the sum.
     */
    public MaximumBudget add(MaximumBudget other) {
        checkCurrencyCompatibility(other);
        return new MaximumBudget(this.amount.add(other.amount).doubleValue(), this.currency);
    }

    /**
     * Returns a new MaximumBudget with the difference of this and another budget.
     * Prevents negative budget by returning 0 if the result would be negative.
     *
     * @param other Other MaximumBudget to subtract.
     * @return New MaximumBudget representing the difference.
     */
    public MaximumBudget subtract(MaximumBudget other) {
        checkCurrencyCompatibility(other);
        double result = this.amount.subtract(other.amount).doubleValue();
        if (result < ZERO) {
            result = ZERO;
        }
        return new MaximumBudget(result, this.currency);
    }

    /**
     * Compares this budget to another to check if greater.
     *
     * @param other Other MaximumBudget.
     * @return True if this budget > other.
     */
    public boolean greaterThan(MaximumBudget other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) > ZERO;
    }

    /**
     * Compares this budget to another to check if less.
     *
     * @param other Other MaximumBudget.
     * @return True if this budget < other.
     */
    public boolean lessThan(MaximumBudget other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) < ZERO;
    }

    /**
     * Checks if the currency of another MaximumBudget is compatible with this one.
     *
     * @param other Other MaximumBudget to check.
     */
    private void checkCurrencyCompatibility(MaximumBudget other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(ERROR_CURRENCY_MISMATCH);
        }
    }

    /**
     * Checks equality between this and another object.
     * @param otherObject Other object to compare.
     * @return True if equal, false otherwise.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        MaximumBudget other = (MaximumBudget) otherObject;
        return this.amount.equals(other.amount) && this.currency.equals(other.currency);
    }

    /**
     * Returns the hash code of this MaximumBudget.
     * @return Hash code as int.
     */
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    /**
     * Returns the string representation of the MaximumBudget.
     * @return String representation.
     */
    @Override
    public String toString() {
        return String.format("MaximumBudget={amount=%.2f, currency='%s'}", this.amount, this.currency);
    }
}