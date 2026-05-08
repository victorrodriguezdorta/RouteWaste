package es.ull.project.domain.valueobject.algorithm;

/**
 * AveragePickupTimeMinutes
 *
 * Represents the average pickup time per stop in minutes for an algorithm execution.
 * Immutable value object that encapsulates a non-negative integer.
 * It is a required attribute.
 */
public final class AveragePickupTimeMinutes {

    private static final String ERROR_MINUTES_NEGATIVE = "Average pickup time in minutes cannot be negative";
    private static final int ZERO = 0;

    /**
     * The average pickup time in minutes.
     * It is a required attribute.
     */
    private final int minutes;

    /**
     * Creates a new AveragePickupTimeMinutes.
     *
     * @param minutes the average pickup time in minutes; must be &ge; 0
     * @throws IllegalArgumentException if minutes is negative
     */
    public AveragePickupTimeMinutes(int minutes) {
        if (minutes < ZERO) {
            throw new IllegalArgumentException(ERROR_MINUTES_NEGATIVE);
        }
        this.minutes = minutes;
    }

    /**
     * Returns the average pickup time in minutes.
     *
     * @return average pickup time in minutes
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
        AveragePickupTimeMinutes other = (AveragePickupTimeMinutes) otherObject;
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
        return "AveragePickupTimeMinutes={" + minutes + "}";
    }
}
