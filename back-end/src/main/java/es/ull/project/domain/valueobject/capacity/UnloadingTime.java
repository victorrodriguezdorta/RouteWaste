package es.ull.project.domain.valueobject.capacity;

import java.util.Objects;

/**
 * UnloadingTime
 *
 * Represents the time required to unload a truck at a facility in minutes.
 * Immutable value object that encapsulates the unloading time value in minutes.
 * The unit is always minutes (fixed).
 */
public final class UnloadingTime {

    /**
     * Error messages for validation.
     */
    private static final String ERROR_TIME_NEGATIVE = "Unloading time cannot be negative";
    private static final String ERROR_OTHER_TIME_NULL = "Other UnloadingTime cannot be null";
    private static final int ZERO = 0;
    private static final int SECONDS_PER_MINUTE = 60;

    /**
     * Required.
     * Unloading time value in minutes.
     * It is a required attribute.
     */
    private final int minutes;

    /**
     * Creates a new UnloadingTime.
     *
     * @param minutes Unloading time value in minutes (must be ≥ 0)
     * @throws IllegalArgumentException if minutes is negative
     */
    public UnloadingTime(int minutes) {
        if (minutes < ZERO) {
            throw new IllegalArgumentException(ERROR_TIME_NEGATIVE);
        }
        this.minutes = minutes;
    }

    /**
     * Returns the unloading time value in minutes.
     *
     * @return Unloading time value in minutes
     */
    public int getMinutes() {
        return this.minutes;
    }

    /**
     * Returns the unloading time value in seconds.
     * Convenience method for cases where seconds are needed.
     *
     * @return Unloading time value in seconds
     */
    public int getSeconds() {
        return this.minutes * SECONDS_PER_MINUTE;
    }

    /**
     * Returns a new UnloadingTime with the updated value.
     *
     * @param newMinutes New unloading time value in minutes
     * @return New UnloadingTime
     * @throws IllegalArgumentException if newMinutes is negative
     */
    public UnloadingTime setMinutes(int newMinutes) {
        return new UnloadingTime(newMinutes);
    }

    /**
     * Compares if this time is greater than another.
     *
     * @param other Other UnloadingTime
     * @return true if greater, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThan(UnloadingTime other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_TIME_NULL);
        }
        return this.minutes > other.minutes;
    }

    /**
     * Compares if this time is greater than or equal to another.
     *
     * @param other Other UnloadingTime
     * @return true if greater or equal, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThanOrEqual(UnloadingTime other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_TIME_NULL);
        }
        return this.minutes >= other.minutes;
    }

    /**
     * Compares if this time is less than another.
     *
     * @param other Other UnloadingTime
     * @return true if less, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean lessThan(UnloadingTime other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_TIME_NULL);
        }
        return this.minutes < other.minutes;
    }

    /**
     * Compares equality of two UnloadingTime instances.
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
        UnloadingTime other = (UnloadingTime) otherObject;
        return this.minutes == other.minutes;
    }

    /**
     * Returns the hash code of the UnloadingTime.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(minutes);
    }

    /**
     * Returns the string representation of the UnloadingTime.
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format("UnloadingTime={minutes=%d}", this.minutes);
    }
}
