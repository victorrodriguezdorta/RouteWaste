package es.ull.project.domain.valueobject.demand;

import java.util.Objects;

/**
 * DailyWasteDemandLitersPerDay
 *
 * Represents the approximate waste demand of a container per day in liters.
 * Immutable value object that encapsulates the daily demand value in liters per day.
 * The unit is always liters per day (fixed).
 */
public final class DailyWasteDemandLitersPerDay {

    /**
     * Error messages for validation.
     */
    private static final String ERROR_DEMAND_NEGATIVE = "Daily waste demand cannot be negative";
    private static final String ERROR_OTHER_DEMAND_NULL = "Other DailyWasteDemandLitersPerDay cannot be null";
    private static final int ZERO = 0;

    /**
     * Required.
     * Demand value in liters per day.
     * It is a required attribute and always measured in liters per day.
     */
    private final double litersPerDay;

    /**
     * Creates a new DailyWasteDemandLitersPerDay.
     *
     * @param litersPerDay Demand value in liters per day (must be ≥ 0)
     * @throws IllegalArgumentException if litersPerDay is negative
     */
    public DailyWasteDemandLitersPerDay(double litersPerDay) {
        if (litersPerDay < ZERO) {
            throw new IllegalArgumentException(ERROR_DEMAND_NEGATIVE);
        }
        this.litersPerDay = litersPerDay;
    }

    /**
     * Returns the demand value in liters per day.
     *
     * @return Demand value in liters per day
     */
    @com.fasterxml.jackson.annotation.JsonValue
    public double getLitersPerDay() {
        return this.litersPerDay;
    }

    /**
     * Returns a new DailyWasteDemandLitersPerDay with the updated value.
     *
     * @param newLitersPerDay New demand value in liters per day
     * @return New DailyWasteDemandLitersPerDay
     * @throws IllegalArgumentException if newLitersPerDay is negative
     */
    public DailyWasteDemandLitersPerDay setLitersPerDay(double newLitersPerDay) {
        return new DailyWasteDemandLitersPerDay(newLitersPerDay);
    }

    /**
     * Adds two DailyWasteDemandLitersPerDay instances.
     *
     * @param other Other DailyWasteDemandLitersPerDay
     * @return New DailyWasteDemandLitersPerDay with the sum
     * @throws IllegalArgumentException if the other object is null
     */
    public DailyWasteDemandLitersPerDay add(DailyWasteDemandLitersPerDay other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_DEMAND_NULL);
        }
        return new DailyWasteDemandLitersPerDay(this.litersPerDay + other.litersPerDay);
    }

    /**
     * Compares if this demand is greater than another.
     *
     * @param other Other DailyWasteDemandLitersPerDay
     * @return true if greater, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThan(DailyWasteDemandLitersPerDay other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_DEMAND_NULL);
        }
        return this.litersPerDay > other.litersPerDay;
    }

    /**
     * Compares if this demand is greater than or equal to another.
     *
     * @param other Other DailyWasteDemandLitersPerDay
     * @return true if greater or equal, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThanOrEqual(DailyWasteDemandLitersPerDay other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_DEMAND_NULL);
        }
        return this.litersPerDay >= other.litersPerDay;
    }

    /**
     * Compares if this demand is less than another.
     *
     * @param other Other DailyWasteDemandLitersPerDay
     * @return true if less, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean lessThan(DailyWasteDemandLitersPerDay other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_DEMAND_NULL);
        }
        return this.litersPerDay < other.litersPerDay;
    }

    /**
     * Compares equality of two DailyWasteDemandLitersPerDay instances.
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
        DailyWasteDemandLitersPerDay other = (DailyWasteDemandLitersPerDay) otherObject;
        return Double.compare(this.litersPerDay, other.litersPerDay) == ZERO;
    }

    /**
     * Returns the hash code of the DailyWasteDemandLitersPerDay.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(litersPerDay);
    }

    /**
     * Returns the string representation of the DailyWasteDemandLitersPerDay.
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format("DailyWasteDemandLitersPerDay={litersPerDay=%s}", this.litersPerDay);
    }
}
