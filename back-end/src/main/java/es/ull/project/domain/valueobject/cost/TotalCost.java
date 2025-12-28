package es.ull.project.domain.valueobject.cost;


import java.util.Objects;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * TotalCost
 * 
 * Represents the total cost in the domain, which can include
 * opening fixed costs, transportation variable costs, or other cost components.
 * This is a mandatory value object in the domain.
 */
public final class TotalCost {

    private static final String ERROR_AMOUNT_NOT_DEFINED = "Total cost is not defined";
    private static final String ERROR_AMOUNT_NEGATIVE = "Total cost cannot be negative";
    private static final String ERROR_CURRENCY_INVALID = "Currency cannot be null or empty";

    /**
     * Amount of the total cost.
     * Required attribute. Always stored with 2 decimal precision.
     */
    private final BigDecimal amount;

    /**
     * Optional currency of the cost. Default: "EUR".
     */
    private final String currency;

    /**
     * Creates a new TotalCost with a default currency (EUR).
     *
     * @param amount Amount of the total cost. Must be ≥ 0.
     */
    public TotalCost(double amount) {
        this(amount, "EUR");
    }

    /**
     * Creates a new TotalCost with a specific currency.
     *
     * @param amount   Amount of the total cost. Must be ≥ 0.
     * @param currency Currency code (e.g., "EUR", "USD"). Cannot be null/empty.
     */
    public TotalCost(double amount, String currency) {
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

    /**
     * Returns a new TotalCost with the sum of this and another cost.
     *
     * @param other Other TotalCost to add.
     * @return New TotalCost representing the sum.
     */
    public TotalCost add(TotalCost other) {
        checkCurrencyCompatibility(other);
        return new TotalCost(this.amount.add(other.amount).doubleValue(), this.currency);
    }

    /**
     * Returns a new TotalCost with the difference of this and another cost.
     *
     * @param other Other TotalCost to subtract.
     * @return New TotalCost representing the difference.
     */
    public TotalCost subtract(TotalCost other) {
        checkCurrencyCompatibility(other);
        double result = this.amount.subtract(other.amount).doubleValue();
        if (result < 0) {
            result = 0; // Prevent negative cost
        }
        return new TotalCost(result, this.currency);
    }

    /**
     * Compares this cost to another to check if greater.
     *
     * @param other Other TotalCost.
     * @return True if this cost > other.
     */
    public boolean greaterThan(TotalCost other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean greaterThan( MaximumBudget other) {
        return greaterThan(other.getAmount());
    }



    /**
     * Compares this cost to another to check if less.
     *
     * @param other Other TotalCost.
     * @return True if this cost < other.
     */
    public boolean lessThan(TotalCost other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    private void checkCurrencyCompatibility(TotalCost other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot operate on costs with different currencies");
        }
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        TotalCost other = (TotalCost) otherObject;
        return this.amount.equals(other.amount) && this.currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("TotalCost={amount=%.2f, currency='%s'}", this.amount, this.currency);
    }
}