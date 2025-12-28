package es.ull.project.domain.valueobject.cost;


import java.util.Objects;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * OpeningFixedCost
 * 
 * Represents the fixed cost incurred when opening a facility.
 * This is a mandatory value object in the domain.
 */
public final class OpeningFixedCost {

    private static final String ERROR_AMOUNT_NOT_DEFINED = "Opening fixed cost is not defined";
    private static final String ERROR_AMOUNT_NEGATIVE = "Opening fixed cost cannot be negative";
    private static final String ERROR_CURRENCY_INVALID = "Currency cannot be null or empty";

    /**
     * Amount of the fixed cost.
     * Required attribute. Always stored with 2 decimal precision.
     */
    private final BigDecimal amount;

    /**
     * Optional currency of the cost. Default: "EUR".
     */
    private final String currency;

    /**
     * Creates a new OpeningFixedCost with a default currency (EUR).
     *
     * @param amount Amount of the fixed cost. Must be ≥ 0.
     */
    public OpeningFixedCost(double amount) {
        this(amount, "EUR");
    }

    /**
     * Creates a new OpeningFixedCost with a specific currency.
     *
     * @param amount   Amount of the fixed cost. Must be ≥ 0.
     * @param currency Currency code (e.g., "EUR", "USD"). Cannot be null/empty.
     */
    public OpeningFixedCost(double amount, String currency) {
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
     * Adds two OpeningFixedCost values, ensuring currency compatibility.
     *
     * @param other The other OpeningFixedCost to add.
     * @return A new OpeningFixedCost representing the sum.
     */
    public OpeningFixedCost add(OpeningFixedCost other) {
        checkCurrencyCompatibility(other);
        return new OpeningFixedCost(this.amount.add(other.amount).doubleValue(), this.currency);
    }

    /**
     * Subtracts another OpeningFixedCost from this one, ensuring currency compatibility.
     * If the result is negative, it returns zero.
     *
     * @param other The other OpeningFixedCost to subtract.
     * @return A new OpeningFixedCost representing the difference.
     */
    public OpeningFixedCost subtract(OpeningFixedCost other) {
        checkCurrencyCompatibility(other);
        double result = this.amount.subtract(other.amount).doubleValue();
        if (result < 0) {
            result = 0;
        }
        return new OpeningFixedCost(result, this.currency);
    }

    /**
     * Compares if this OpeningFixedCost is greater than another, ensuring currency compatibility.
     *
     * @param other The other OpeningFixedCost to compare.
     * @return True if this cost is greater, false otherwise.
     */
    public boolean greaterThan(OpeningFixedCost other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Compares if this OpeningFixedCost is less than another, ensuring currency compatibility.
     *
     * @param other The other OpeningFixedCost to compare.
     * @return True if this cost is less, false otherwise.
     */
    public boolean lessThan(OpeningFixedCost other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    private void checkCurrencyCompatibility(OpeningFixedCost other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot operate on costs with different currencies");
        }
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        OpeningFixedCost other = (OpeningFixedCost) otherObject;
        return this.amount.equals(other.amount) && this.currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("OpeningFixedCost={amount=%.2f, currency='%s'}", this.amount, this.currency);
    }
}