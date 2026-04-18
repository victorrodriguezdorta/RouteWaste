package es.ull.project.domain.valueobject.capacity;

import java.util.Objects;

/**
 * ProcessingCapacityKilogramsPerDay
 *
 * Represents the processing capacity of a facility in kilograms per day.
 * Immutable value object that encapsulates the daily processing capacity in kilograms per day.
 * The unit is always kilograms per day (fixed).
 */
public final class ProcessingCapacityKilogramsPerDay {

    /**
     * Error messages for validation.
     */
    private static final String ERROR_CAPACITY_NEGATIVE = "Processing capacity cannot be negative";
    private static final String ERROR_OTHER_CAPACITY_NULL = "Other ProcessingCapacityKilogramsPerDay cannot be null";
    private static final int ZERO = 0;

    /**
     * Required.
     * Processing capacity value in kilograms per day.
     * It is a required attribute and always measured in kilograms per day.
     */
    private final double kilogramsPerDay;

    /**
     * Creates a new ProcessingCapacityKilogramsPerDay.
     *
     * @param kilogramsPerDay Processing capacity value in kilograms per day (must be ≥ 0)
     * @throws IllegalArgumentException if kilogramsPerDay is negative
     */
    public ProcessingCapacityKilogramsPerDay(double kilogramsPerDay) {
        if (kilogramsPerDay < ZERO) {
            throw new IllegalArgumentException(ERROR_CAPACITY_NEGATIVE);
        }
        this.kilogramsPerDay = kilogramsPerDay;
    }

    /**
     * Returns the processing capacity value in kilograms per day.
     *
     * @return Processing capacity value in kilograms per day
     */
    public double getKilogramsPerDay() {
        return this.kilogramsPerDay;
    }

    /**
     * Returns a new ProcessingCapacityKilogramsPerDay with the updated value.
     *
     * @param newKilogramsPerDay New processing capacity value in kilograms per day
     * @return New ProcessingCapacityKilogramsPerDay
     * @throws IllegalArgumentException if newKilogramsPerDay is negative
     */
    public ProcessingCapacityKilogramsPerDay setKilogramsPerDay(double newKilogramsPerDay) {
        return new ProcessingCapacityKilogramsPerDay(newKilogramsPerDay);
    }

    /**
     * Compares if this capacity is greater than another.
     *
     * @param other Other ProcessingCapacityKilogramsPerDay
     * @return true if greater, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThan(ProcessingCapacityKilogramsPerDay other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.kilogramsPerDay > other.kilogramsPerDay;
    }

    /**
     * Compares if this capacity is greater than or equal to another.
     *
     * @param other Other ProcessingCapacityKilogramsPerDay
     * @return true if greater or equal, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThanOrEqual(ProcessingCapacityKilogramsPerDay other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.kilogramsPerDay >= other.kilogramsPerDay;
    }

    /**
     * Compares if this capacity is less than another.
     *
     * @param other Other ProcessingCapacityKilogramsPerDay
     * @return true if less, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean lessThan(ProcessingCapacityKilogramsPerDay other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.kilogramsPerDay < other.kilogramsPerDay;
    }

    /**
     * Compares equality of two ProcessingCapacityKilogramsPerDay instances.
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
        ProcessingCapacityKilogramsPerDay other = (ProcessingCapacityKilogramsPerDay) otherObject;
        return Double.compare(this.kilogramsPerDay, other.kilogramsPerDay) == ZERO;
    }

    /**
     * Returns the hash code of the ProcessingCapacityKilogramsPerDay.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(kilogramsPerDay);
    }

    /**
     * Returns the string representation of the ProcessingCapacityKilogramsPerDay.
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format("ProcessingCapacityKilogramsPerDay={kilogramsPerDay=%s}", this.kilogramsPerDay);
    }
}
