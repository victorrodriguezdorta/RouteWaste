package es.ull.project.domain.valueobject.demand;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * QuantityUnit
 *
 * Represents a unit of measurement for quantities in the domain.
 * The unit must contain only alphabetic characters.
 * This is an immutable value object in the domain.
 */
public final class QuantityUnit {

    /**
     * Pattern to validate unit format (only alphabetic characters).
     */
    private static final String UNIT_REGEX = "^[a-zA-Z]+$";
    private static final Pattern UNIT_PATTERN = Pattern.compile(UNIT_REGEX);

    private static final String ERROR_UNIT_NOT_DEFINED = "Quantity unit is not defined";
    private static final String ERROR_UNIT_EMPTY = "Quantity unit cannot be empty";
    private static final String ERROR_UNIT_FORMAT = "Quantity unit format is invalid";
    private static final int ZERO = 0;

    /**
     * Required. The unit value. Must be non-empty and contain only letters. E.g., "kg", "liters", "units"
     * It is a required attribute.
     */
    private final String value;

    /**
     * Creates a new QuantityUnit with the specified value.
     *
     * @param value Unit value. Must be non-null, non-empty, and contain only letters.
     * @throws IllegalArgumentException if value is null, empty, or has invalid format.
     */
    public QuantityUnit(String value) {
        this.validateValue(value);
        this.value = value;
    }

    /**
     * Validates that the unit value is not null, not empty, and contains only letters.
     *
     * @param value Value to validate.
     * @throws IllegalArgumentException if validation fails.
     */
    private void validateValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException(ERROR_UNIT_NOT_DEFINED);
        }
        if (value.length() == ZERO) {
            throw new IllegalArgumentException(ERROR_UNIT_EMPTY);
        }
        if (!UNIT_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(ERROR_UNIT_FORMAT);
        }
    }

    /**
     * Returns the unit value.
     *
     * @return The unit value as a string.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Creates a new QuantityUnit with a different value.
     * This maintains immutability of the value object.
     *
     * @param newValue New unit value.
     * @return New QuantityUnit instance with the specified value.
     * @throws IllegalArgumentException if newValue is invalid.
     */
    public QuantityUnit setValue(String newValue) {
        return new QuantityUnit(newValue);
    }

    /**
     * Checks equality based on unit value.
     *
     * @param otherObject Object to compare with.
     * @return True if values are equal, false otherwise.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        QuantityUnit other = (QuantityUnit) otherObject;
        return this.value.equals(other.value);
    }

    /**
     * Generates hash code based on the unit value.
     *
     * @return Hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns a string representation of the quantity unit.
     *
     * @return Formatted string with the unit value.
     */
    @Override
    public String toString() {
        return String.format("QuantityUnit={value='%s'}", this.value);
    }
}