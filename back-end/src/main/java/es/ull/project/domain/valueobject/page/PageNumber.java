package es.ull.project.domain.valueobject.page;

/**
 * PageNumber
 *
 * Represents a zero-based page index in a paginated result set.
 * Immutable value object wrapping a non-negative integer.
 * It is a required attribute.
 */
public final class PageNumber {

    private static final String ERROR_NEGATIVE = "Page number cannot be negative";
    private static final int ZERO = 0;

    /**
     * The zero-based page index.
     * It is a required attribute.
     */
    private final int value;

    /**
     * Creates a new PageNumber.
     *
     * @param value the zero-based page index; must be &ge; 0
     * @throws IllegalArgumentException if value is negative
     */
    public PageNumber(int value) {
        if (value < ZERO) {
            throw new IllegalArgumentException(ERROR_NEGATIVE);
        }
        this.value = value;
    }

    /**
     * Returns the zero-based page index.
     *
     * @return page number
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
        PageNumber other = (PageNumber) otherObject;
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
        return "PageNumber={" + value + "}";
    }
}
