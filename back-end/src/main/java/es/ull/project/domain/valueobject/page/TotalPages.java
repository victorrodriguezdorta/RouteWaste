package es.ull.project.domain.valueobject.page;

/**
 * TotalPages
 *
 * Represents the total number of pages in a paginated result set.
 * Immutable value object wrapping a non-negative integer count.
 * It is a required attribute.
 */
public final class TotalPages {

    private static final String ERROR_NEGATIVE = "Total pages cannot be negative";
    private static final int ZERO = 0;

    /**
     * The total number of pages.
     * It is a required attribute.
     */
    private final int value;

    /**
     * Creates a new TotalPages.
     *
     * @param value the total number of pages; must be &ge; 0
     * @throws IllegalArgumentException if value is negative
     */
    public TotalPages(int value) {
        if (value < ZERO) {
            throw new IllegalArgumentException(ERROR_NEGATIVE);
        }
        this.value = value;
    }

    /**
     * Returns the total number of pages.
     *
     * @return total pages count
     */
    @com.fasterxml.jackson.annotation.JsonValue
    public int getValue() {
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
        TotalPages other = (TotalPages) otherObject;
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
        return "TotalPages={" + value + "}";
    }
}
