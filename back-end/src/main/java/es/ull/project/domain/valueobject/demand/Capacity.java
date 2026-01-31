package es.ull.project.domain.valueobject.demand;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Capacity
 *
 * Represents an amount per time unit (for example, tons per day).
 * Immutable value object that encapsulates the value, the quantity unit, and the time unit.
 */
public final class Capacity {

    /**
     * Error messages.
     */
    private static final String ERROR_VALUE_NEGATIVE = "Capacity value must be greater than or equal to 0";
    private static final String ERROR_QUANTITY_UNIT_NOT_DEFINED = "Quantity unit is not defined";
    private static final String ERROR_TIME_UNIT_NOT_DEFINED = "Time unit is not defined";
    private static final String ERROR_OTHER_CAPACITY_NULL = "Other Capacity cannot be null";
    private static final String ERROR_UNITS_MUST_MATCH = "Units must be the same";
    private static final int ZERO = 0;

    /**
     * Required.
     * Amount value per time unit (e.g., 10.5).
     */
    private final double value;

    /**
     * Required.
     * Quantity unit (e.g., tons, liters).
     */
    private final QuantityUnit quantityUnit;

    /**
     * Required.
     * Time unit (e.g., DAYS, HOURS).
     */
    private final TimeUnit timeUnit;

    /**
     * Creates a new Capacity (amount per time).
     *
     * @param value        Amount value (must be ≥ 0)
     * @param quantityUnit Quantity unit
     * @param timeUnit     Time unit
     */
    public Capacity(double value, QuantityUnit quantityUnit, TimeUnit timeUnit) {
        this.validateValue(value);
        if (quantityUnit == null) {
            throw new IllegalArgumentException(ERROR_QUANTITY_UNIT_NOT_DEFINED);
        }
        if (timeUnit == null) {
            throw new IllegalArgumentException(ERROR_TIME_UNIT_NOT_DEFINED);
        }
        this.value = value;
        this.quantityUnit = quantityUnit;
        this.timeUnit = timeUnit;
    }

    /**
     * Validates that the value is greater than or equal to zero.
     *
     * @param value Value to validate
     */
    private void validateValue(double value) {
        if (value < ZERO) {
            throw new IllegalArgumentException(ERROR_VALUE_NEGATIVE);
        }
    }

    /**
     * Returns the amount value.
     *
     * @return Amount value
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns the quantity unit.
     *
     * @return QuantityUnit
     */
    public QuantityUnit getQuantityUnit() {
        return this.quantityUnit;
    }

    /**
     * Returns the time unit.
     *
     * @return TimeUnit
     */
    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    /**
     * Returns a new Capacity with the updated value.
     *
     * @param newValue New value
     * @return New Capacity
     */
    public Capacity setValue(double newValue) {
        return new Capacity(newValue, this.quantityUnit, this.timeUnit);
    }

    /**
     * Returns a new Capacity with the updated quantity unit.
     *
     * @param newQuantityUnit New quantity unit
     * @return New Capacity
     */
    public Capacity setQuantityUnit(QuantityUnit newQuantityUnit) {
        return new Capacity(this.value, newQuantityUnit, this.timeUnit);
    }

    /**
     * Returns a new Capacity with the updated time unit.
     *
     * @param newTimeUnit New time unit
     * @return New Capacity
     */
    public Capacity setTimeUnit(TimeUnit newTimeUnit) {
        return new Capacity(this.value, this.quantityUnit, newTimeUnit);
    }

    /**
     * Compares if this capacity is greater than another.
     *
     * @param other Other Capacity
     * @return true if greater, false otherwise
     * @throws IllegalArgumentException if units do not match or the other object is null
     */
    public boolean greaterThan(Capacity other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        if (!this.quantityUnit.equals(other.quantityUnit) ||
            !this.timeUnit.equals(other.timeUnit)) {
            throw new IllegalArgumentException(ERROR_UNITS_MUST_MATCH);
        }
        return this.value > other.value;
    }

    /**
     * Compares equality of two Capacity instances.
     *
     * @param otherObject Other object
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Capacity other = (Capacity) otherObject;
        return Double.compare(this.value, other.value) == ZERO &&
                this.quantityUnit.equals(other.quantityUnit) &&
                this.timeUnit.equals(other.timeUnit);
    }

    /**
     * Returns the hash code of the Capacity.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, quantityUnit, timeUnit);
    }

    /**
     * Returns the string representation of the Capacity.
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format(
                "Capacity={value=%s, unit=%s/%s}",
                this.value,
                this.quantityUnit.getValue(),
                this.timeUnit.name().toLowerCase()
        );
    }
}