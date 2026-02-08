package es.ull.project.domain.valueobject.demand;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * WasteDemand
 *
 * Represents the expected waste demand per time unit (for example, tons per day).
 * Immutable value object that encapsulates the value, the quantity unit, and the time unit.
 */
public final class WasteDemand {

    private static final int ZERO = 0;

    /**
     * Error messages for validation.
     */
    private static final String ERROR_DEMAND_NEGATIVE = "Waste demand cannot be negative";
    private static final String ERROR_UNITS_NULL = "Units cannot be null";
    private static final String ERROR_OTHER_DEMAND_NULL = "Other WasteDemand cannot be null";
    private static final String ERROR_CAPACITY_NULL = "Capacity cannot be null";
    private static final String ERROR_UNITS_MUST_MATCH = "Units must be the same";
    private static final String ERROR_UNITS_MUST_MATCH_CAPACITY = "Units must be the same to compare WasteDemand and Capacity";

    /**
     * Required.
     * Value of the waste demand per time unit.
     * It is a required attribute.
     */
    private final double value;

    /**
     * Required.
     * Quantity unit (for example, tons).
     * It is a required attribute.
     */
    private final QuantityUnit quantityUnit;

    /**
     * Required.
     * Time unit (for example, DAYS).
     * It is a required attribute.
     */
    private final TimeUnit timeUnit;

    /**
     * Creates a new WasteDemand (waste demand per time).
     *
     * @param value        Demand value (must be ≥ 0)
     * @param quantityUnit Quantity unit
     * @param timeUnit     Time unit
     */
    public WasteDemand(double value, QuantityUnit quantityUnit, TimeUnit timeUnit) {
        if (value < ZERO) {
            throw new IllegalArgumentException(ERROR_DEMAND_NEGATIVE);
        }
        if (quantityUnit == null || timeUnit == null) {
            throw new IllegalArgumentException(ERROR_UNITS_NULL);
        }
        this.value = value;
        this.quantityUnit = quantityUnit;
        this.timeUnit = timeUnit;
    }

    /**
     * Creates a new WasteDemand with default values (tons/day).
     *
     * @param value Demand value
     */
    public WasteDemand(double value) {
        this(value, new QuantityUnit("tons"), TimeUnit.DAYS);
    }

    /**
     * Returns the demand value.
     *
     * @return Demand value
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the quantity unit.
     *
     * @return QuantityUnit
     */
    public QuantityUnit getQuantityUnit() {
        return quantityUnit;
    }

    /**
     * Returns the time unit.
     *
     * @return TimeUnit
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * Returns a new WasteDemand with the updated value.
     *
     * @param newValue New value
     * @return New WasteDemand
     */
    public WasteDemand setValue(double newValue) {
        return new WasteDemand(newValue, this.quantityUnit, this.timeUnit);
    }

    /**
     * Returns a new WasteDemand with the updated quantity unit.
     *
     * @param newQuantityUnit New quantity unit
     * @return New WasteDemand
     */
    public WasteDemand setQuantityUnit(QuantityUnit newQuantityUnit) {
        return new WasteDemand(this.value, newQuantityUnit, this.timeUnit);
    }

    /**
     * Returns a new WasteDemand with the updated time unit.
     *
     * @param newTimeUnit New time unit
     * @return New WasteDemand
     */
    public WasteDemand setTimeUnit(TimeUnit newTimeUnit) {
        return new WasteDemand(this.value, this.quantityUnit, newTimeUnit);
    }

    /**
     * Adds two WasteDemand instances if they have the same units.
     *
     * @param other Other WasteDemand
     * @return New WasteDemand with the sum
     * @throws IllegalArgumentException if units do not match or the other object is null
     */
    public WasteDemand add(WasteDemand other) {
        validateSameUnit(other);
        return new WasteDemand(this.value + other.value, quantityUnit, timeUnit);
    }

    /**
     * Compares if this demand is greater than another.
     *
     * @param other Other WasteDemand
     * @return true if greater, false otherwise
     * @throws IllegalArgumentException if units do not match or the other object is null
     */
    public boolean greaterThan(WasteDemand other) {
        validateSameUnit(other);
        return this.value > other.value;
    }

    /**
     * Compares if this demand is greater than a capacity.
     *
     * @param capacity Capacity to compare
     * @return true if greater, false otherwise
     * @throws IllegalArgumentException if units do not match or the object is null
     */
    public boolean greaterThan(Capacity capacity) {
        if (capacity == null) {
            throw new IllegalArgumentException(ERROR_CAPACITY_NULL);
        }
        if (!this.quantityUnit.equals(capacity.getQuantityUnit()) ||
            !this.timeUnit.equals(capacity.getTimeUnit())) {
            throw new IllegalArgumentException(ERROR_UNITS_MUST_MATCH_CAPACITY);
        }
        return this.value > capacity.getValue();
    }

    /**
     * Validates that the units are the same between two WasteDemand instances.
     *
     * @param other Other WasteDemand
     * @throws IllegalArgumentException if units do not match or the other object is null
     */
    private void validateSameUnit(WasteDemand other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_DEMAND_NULL);
        }
        if (!this.quantityUnit.equals(other.quantityUnit) ||
            !this.timeUnit.equals(other.timeUnit)) {
            throw new IllegalArgumentException(ERROR_UNITS_MUST_MATCH);
        }
    }

    /**
     * Compares equality of two WasteDemand instances.
     *
     * @param otherObject Other object to compare
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
        WasteDemand that = (WasteDemand) otherObject;
        return Double.compare(that.value, value) == ZERO &&
                quantityUnit.equals(that.quantityUnit) &&
                timeUnit.equals(that.timeUnit);
    }

    /**
     * Returns the hash code of the WasteDemand.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, quantityUnit, timeUnit);
    }

    /**
     * Returns the string representation of the WasteDemand.
     *
     * @return String
     */
    @Override
    public String toString() {
        return "WasteDemand=" + value + " " + quantityUnit + "/" + timeUnit;
    }
}