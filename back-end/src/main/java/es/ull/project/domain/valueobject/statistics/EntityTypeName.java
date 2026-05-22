package es.ull.project.domain.valueobject.statistics;

import java.util.Objects;

/**
 * Serialized name of an enumerated entity type (e.g. {@code COLLECTION_TRUCK}).
 */
public final class EntityTypeName {

    private static final String ERROR_BLANK = "Entity type name must not be null or blank";

    private final String value;

    /**
     * Creates a validated type name.
     *
     * @param value enum constant name
     */
    public EntityTypeName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(ERROR_BLANK);
        }
        this.value = value;
    }

    /**
     * Returns the serialized type name.
     *
     * @return enum constant name
     */
    @com.fasterxml.jackson.annotation.JsonValue
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
        EntityTypeName other = (EntityTypeName) otherObject;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "EntityTypeName={" + value + "}";
    }
}
