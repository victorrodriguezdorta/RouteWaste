package es.ull.project.domain.valueobject.cost;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

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
    private static final String ERROR_CURRENCY_MISMATCH = "Incompatible currencies: ";
    private static final int ZERO = 0;

    /**
     * Required.
     * Amount of the fixed cost. Always stored with 2 decimal precision.
     */
    private final BigDecimal amount;

    /**
     * Optional.
     * Currency of the cost. Default: "EUR".
     */
    private final Currency currency;

    /**
     * Creates a new OpeningFixedCost with a default currency (EUR).
     *
     * @param amount Amount of the fixed cost. Must be ≥ 0.
     */
    public OpeningFixedCost(double amount) {
        this(amount, new Currency());
    }

    /**
     * Creates a new OpeningFixedCost with a specific currency. (BETTER)
     *
     * @param amount   Amount of the fixed cost. Must be ≥ 0.
     * @param currency Currency code (e.g., "EUR", "USD"). Cannot be null/empty.
     */
    public OpeningFixedCost(double amount, Currency currency) {
        validateAmount(amount);
        validateCurrency(currency);
        this.amount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    /**
     * Creates a new OpeningFixedCost with a specific currency.
     *
     * @param amount   Amount of the fixed cost. Must be ≥ 0.
     * @param currency Currency code (e.g., "EUR", "USD"). Cannot be null/empty. Is a string. and it creates a Currency object internally.
     */
    public OpeningFixedCost(double amount, String currency) {
        validateAmount(amount);
        validateCurrencyString(currency);
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
     * Validates that the currency string is not null or empty.
     *
     * @param currency Currency string to validate.
     * @throws IllegalArgumentException if currency is null or empty.
     */
    private void validateCurrencyString(String currency) {
        if (currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException(ERROR_CURRENCY_INVALID);
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
     * Returns the amount of the opening fixed cost.
     *
     * @return Amount as a double value.
     */
    public double getAmount() {
        return this.amount.doubleValue();
    }

    /**
     * Returns the currency of the opening fixed cost.
     *
     * @return Currency object.
     */
    public Currency getCurrency() {
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
        if (result < ZERO) {
            result = ZERO;
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
        return this.amount.compareTo(other.amount) > ZERO;
    }

    /**
     * Compares if this OpeningFixedCost is less than another, ensuring currency compatibility.
     *
     * @param other The other OpeningFixedCost to compare.
     * @return True if this cost is less, false otherwise.
     */
    public boolean lessThan(OpeningFixedCost other) {
        checkCurrencyCompatibility(other);
        return this.amount.compareTo(other.amount) < ZERO;
    }

    /**
     * Checks if the currency of this cost matches another cost's currency.
     *
     * @param other The opening fixed cost to check against.
     * @throws IllegalArgumentException if currencies don't match.
     */
    private void checkCurrencyCompatibility(OpeningFixedCost other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(ERROR_CURRENCY_MISMATCH + this.currency + " vs " + other.currency);
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
        OpeningFixedCost other = (OpeningFixedCost) otherObject;
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
     * Returns a string representation of the opening fixed cost.
     *
     * @return Formatted string with amount and currency.
     */
    @Override
    public String toString() {
        return String.format("OpeningFixedCost={amount=%.2f, currency='%s'}", this.amount, this.currency);
    }
}