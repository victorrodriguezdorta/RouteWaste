package es.ull.project.domain.valueobject.name;

import java.util.Objects;

/**
 * Name
 *
 * Immutable value object representing a human-readable entity name.
 */
public final class Name {

    private static final String NAME_NOT_DEFINED = "Name must not be null or blank";

    /**
     * Normalized name text.
     * It is a required attribute.
     */
    private final String value;

    /**
     * Creates a validated name.
     *
     * @param value raw name text
     */
    public Name(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NAME_NOT_DEFINED);
        }
        this.value = value.trim();
    }

    /**
     * Returns the normalized name value.
     *
     * @return trimmed name text
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Compares this name with another object.
     *
     * @param otherObject object to compare with
     * @return true when both names have the same value
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Name other = (Name) otherObject;
        return Objects.equals(this.value, other.value);
    }

    /**
     * Returns the hash code for this name.
     *
     * @return hash code based on the name value
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    /**
     * Returns a string representation of this name.
     *
     * @return formatted name text
     */
    @Override
    public String toString() {
        return "Name={" + this.value + "}";
    }
}
