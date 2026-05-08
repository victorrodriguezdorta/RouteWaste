package es.ull.project.domain.valueobject.page;

/**
 * PageFlag
 *
 * Represents a boolean flag in a paginated result set (e.g. first page, last page).
 * Immutable value object wrapping a boolean.
 * It is a required attribute.
 */
public final class PageFlag {

    /**
     * The boolean flag value.
     * It is a required attribute.
     */
    private final boolean value;

    /**
     * Creates a new PageFlag.
     *
     * @param value the boolean flag value
     */
    public PageFlag(boolean value) {
        this.value = value;
    }

    /**
     * Returns the boolean flag value.
     *
     * @return the flag value
     */
    @com.fasterxml.jackson.annotation.JsonValue
    public boolean getValue() {
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
        PageFlag other = (PageFlag) otherObject;
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
        return "PageFlag={" + value + "}";
    }
}
