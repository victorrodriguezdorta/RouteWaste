package es.ull.project.domain.valueobject.cost;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Currency
 *
 * Represents a currency value object using ISO 4217 currency codes.
 * This is a value object in the domain.
 */
public final class Currency {

    private static final int ZERO = 0;
    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");

    private static final String ERROR_CURRENCY_NOT_DEFINED = "Currency is not defined";
    private static final String ERROR_CURRENCY_EMPTY = "Currency cannot be empty";
    private static final String ERROR_CURRENCY_FORMAT = "Currency format is invalid";

    /**
     * ISO 4217 currency code (e.g. EUR, USD).
     * Defaults to EUR if not specified.
     * It is a required attribute.
     */
    private final String code;

    /**
     * Default constructor that creates a Currency with EUR code.
     */
    public Currency() {
        this.code = "EUR";
    }

    /**
     * It creates a new Currency value object.
     *
     * @param code ISO 4217 currency code.
     */
    public Currency(String code) {
        this.validateCode(code);
        this.code = code;
    }

    /**
     * Validates the currency code format.
     *
     * @param code the currency code to validate
     * @throws IllegalArgumentException if the code is null, empty, or invalid
     *                                  format
     */
    private void validateCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException(ERROR_CURRENCY_NOT_DEFINED);
        }
        if (code.length() == ZERO) {
            throw new IllegalArgumentException(ERROR_CURRENCY_EMPTY);
        }
        if (!CURRENCY_CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException(ERROR_CURRENCY_FORMAT);
        }
    }

    /**
     * It returns the currency code.
     *
     * @return Currency code.
     */
    public String getCode() {
        return this.code;
    }

    /**
     * It returns a new Currency with the new code.
     *
     * @param newCode New currency code.
     * @return New Currency object.
     */
    public Currency setCode(String newCode) {
        return new Currency(newCode);
    }

    /**
     * Compares this Currency to another object for equality.
     *
     * @param otherObject the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || this.getClass() != otherObject.getClass()) {
            return false;
        }
        Currency other = (Currency) otherObject;
        return this.code.equals(other.code);
    }

    /**
     * Returns the hash code for this Currency.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    /**
     * Returns a string representation of this Currency.
     *
     * @return a formatted string containing the currency code
     */
    @Override
    public String toString() {
        return String.format("Currency={code='%s'}", this.code);
    }
}