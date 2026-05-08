package es.ull.project.domain.valueobject.algorithm;

/**
 * NumberOfDays
 *
 * Represents the number of days in an algorithm planning period.
 * Immutable value object that encapsulates a positive integer number of days.
 * It is a required attribute.
 */
public final class NumberOfDays {

    private static final String ERROR_DAYS_NOT_POSITIVE = "Number of days must be greater than zero";
    private static final int ZERO = 0;

    /**
     * The number of days.
     * It is a required attribute.
     */
    private final int days;

    /**
     * Creates a new NumberOfDays.
     *
     * @param days the number of days; must be greater than zero
     * @throws IllegalArgumentException if days is not positive
     */
    public NumberOfDays(int days) {
        if (days <= ZERO) {
            throw new IllegalArgumentException(ERROR_DAYS_NOT_POSITIVE);
        }
        this.days = days;
    }

    /**
     * Returns the number of days.
     *
     * @return number of days
     */
    public int getValue() {
        return days;
    }

    /**
     * Checks equality based on the days value.
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
        NumberOfDays other = (NumberOfDays) otherObject;
        return days == other.days;
    }

    /**
     * Returns a hash code based on the days value.
     *
     * @return hash code for this instance
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(days);
    }

    /**
     * Returns a string representation of this value object.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "NumberOfDays={" + days + "}";
    }
}
