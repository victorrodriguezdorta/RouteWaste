package es.ull.project.domain.valueobject.identifier;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier value object for containers referenced by other aggregates.
 */
public final class ContainerId {

    private static final String ERROR_ID_NOT_DEFINED = "Container id is not defined";

    /**
     * Container UUID value.
     * It is a required attribute.
     */
    private final UUID value;

    /**
     * Creates a container identifier.
     *
     * @param value container UUID
     */
    public ContainerId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException(ERROR_ID_NOT_DEFINED);
        }
        this.value = value;
    }

    /**
     * Returns the UUID value.
     *
     * @return container UUID
     */
    public UUID getValue() {
        return this.value;
    }

    /**
     * Compares identifiers by UUID value.
     *
     * @param otherObject object to compare
     * @return true when both identifiers contain the same UUID
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        ContainerId other = (ContainerId) otherObject;
        return Objects.equals(this.value, other.value);
    }

    /**
     * Returns a hash code based on the UUID value.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    /**
     * Formats this identifier as text.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return this.value.toString();
    }
}
