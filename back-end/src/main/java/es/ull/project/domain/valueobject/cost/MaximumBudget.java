package es.ull.project.domain.valueobject.cost;


import java.util.Objects;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

    /**
     * Amount of the maximum budget.
     * Required attribute. Always stored with 2 decimal precision.
     */
    private final BigDecimal amount;

    /**
     * Optional currency of the budget. Default: "EUR".
     */
    private final String currency;

    /**
     * Creates a new MaximumBudget with default currency (EUR).
     *
     * @param amount Amount of the maximum budget. Must be ≥ 0.
     */
    public MaximumBudget(double amount) {
        this(amount, "EUR");
    }

    /**
     * Creates a new MaximumBudget with specific currency.
     *
     * @param amount   Amount of the maximum budget. Must be ≥ 0.
     * @param currency Currency code (e.g., "EUR", "USD"). Cannot be null/empty.
     */
    public MaximumBudget(double amount, String currency) {
        validateAmount(amount);
        validateCurrency(currency);
        this.amount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    /**
     * Validates the amount is defined and non-negative.
     * @param amount Amount to validate.
     */
    private void validateAmount(double amount) {
        if (Double.isNaN(amount)) {
            throw new IllegalArgumentException(ERROR_AMOUNT_NOT_DEFINED);
        }
        if (amount < 0) {
            throw new IllegalArgumentException(ERROR_AMOUNT_NEGATIVE);
        }
    }

    /**
     * Validates the currency is not null or empty.
     * @param currency Currency to validate.
     */
    private void validateCurrency(String currency) {
        if (currency == null || currency.isEmpty()) {
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
     * @return Currency as String.
     */
    public String getCurrency() {
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
     *
     * @param other Other MaximumBudget to subtract.
     * @return New MaximumBudget representing the difference.
     */
    public MaximumBudget subtract(MaximumBudget other) {
        checkCurrencyCompatibility(other);
        double result = this.amount.subtract(other.amount).doubleValue();
        if (result < 0) {
            result = 0; // Prevent negative budget
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
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Compares this budget to another to check if less.
     *
     * @param other Other MaximumBudget.
     * @return True if this budget < other.
     */
    public boolean lessThan(MaximumBudget other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Checks if the currency of another MaximumBudget is compatible with this one.
     *
     * @param other Other MaximumBudget to check.
     */
    private void checkCurrencyCompatibility(MaximumBudget other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot operate on budgets with different currencies");
        }
    }

    /**
     * Checks equality between this and another object.
     * @param otherObject Other object to compare.
     * @return True if equal, false otherwise.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
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