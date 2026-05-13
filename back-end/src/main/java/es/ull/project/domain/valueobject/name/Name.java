package es.ull.project.domain.valueobject.name;

import java.util.Objects;

/**
 * Name
 *
 * Immutable value object representing a human-readable entity name.
 */
public final class Name {

    private static final String NAME_NOT_DEFINED = "Name must not be null or blank";

    private final String value;

    public Name(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NAME_NOT_DEFINED);
        }
        this.value = value.trim();
    }

    public String getValue() {
        return this.value;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return "Name={" + this.value + "}";
    }
}
