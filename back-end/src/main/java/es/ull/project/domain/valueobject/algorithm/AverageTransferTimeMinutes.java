package es.ull.project.domain.valueobject.algorithm;

/**
 * AverageTransferTimeMinutes
 *
 * Represents the average travelling time, in minutes, a vehicle needs to move
 * from one point to another during an algorithm execution.
 * Immutable value object that encapsulates a non-negative integer.
 * It is an optional attribute.
 */
public final class AverageTransferTimeMinutes {

    private static final String ERROR_MINUTES_NEGATIVE = "Average transfer time in minutes cannot be negative";
    private static final int ZERO = 0;

    /**
     * The average transfer time in minutes.
     * It is a required attribute.
     */
    private final int minutes;

    /**
     * Creates a new AverageTransferTimeMinutes.
     *
     * @param minutes the average transfer time in minutes; must be &ge; 0
     * @throws IllegalArgumentException if minutes is negative
     */
    public AverageTransferTimeMinutes(int minutes) {
        if (minutes < ZERO) {
            throw new IllegalArgumentException(ERROR_MINUTES_NEGATIVE);
        }
        this.minutes = minutes;
    }

    /**
     * Returns the average transfer time in minutes.
     *
     * @return average transfer time in minutes
     */
    public int getValue() {
        return minutes;
    }

    /**
     * Checks equality based on the minutes value.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if the other object has the same value
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        AverageTransferTimeMinutes other = (AverageTransferTimeMinutes) otherObject;
        return minutes == other.minutes;
    }

    /**
     * Returns a hash code based on the minutes value.
     *
     * @return hash code for this instance
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(minutes);
    }

    /**
     * Returns a string representation of this value object.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "AverageTransferTimeMinutes={" + minutes + "}";
    }
}
