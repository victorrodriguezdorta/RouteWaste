package es.ull.project.domain.valueobject.page;

/**
 * TotalElements
 *
 * Represents the total number of elements in a paginated result set.
 * Immutable value object wrapping a non-negative long count.
 * It is a required attribute.
 */
public final class TotalElements {

    private static final String ERROR_NEGATIVE = "Total elements cannot be negative";
    private static final long ZERO = 0L;

    /**
     * The total number of elements.
     * It is a required attribute.
     */
    private final long value;

    /**
     * Creates a new TotalElements.
     *
     * @param value the total number of elements; must be &ge; 0
     * @throws IllegalArgumentException if value is negative
     */
    public TotalElements(long value) {
        if (value < ZERO) {
            throw new IllegalArgumentException(ERROR_NEGATIVE);
        }
        this.value = value;
    }

    /**
     * Returns the total number of elements.
     *
     * @return total elements count
     */
    @com.fasterxml.jackson.annotation.JsonValue
    public long getValue() {
        return value;
    }

    /**
     * Checks equality based on the value.
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
        TotalElements other = (TotalElements) otherObject;
        return value == other.value;
    }

    /**
     * Returns a hash code based on the value.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(value);
    }

    /**
     * Returns a string representation.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "TotalElements={" + value + "}";
    }
}
