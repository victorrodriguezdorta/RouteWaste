package es.ull.project.domain.valueobject.page;

/**
 * PageSize
 *
 * Represents the number of elements per page in a paginated result set.
 * Immutable value object wrapping a positive integer.
 * It is a required attribute.
 */
public final class PageSize {

    private static final String ERROR_NOT_POSITIVE = "Page size must be greater than zero";
    private static final int ZERO = 0;

    /**
     * The number of elements per page.
     * It is a required attribute.
     */
    private final int value;

    /**
     * Creates a new PageSize.
     *
     * @param value the number of elements per page; must be &gt; 0
     * @throws IllegalArgumentException if value is not positive
     */
    public PageSize(int value) {
        if (value <= ZERO) {
            throw new IllegalArgumentException(ERROR_NOT_POSITIVE);
        }
        this.value = value;
    }

    /**
     * Returns the number of elements per page.
     *
     * @return page size
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
        PageSize other = (PageSize) otherObject;
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
        return "PageSize={" + value + "}";
    }
}
