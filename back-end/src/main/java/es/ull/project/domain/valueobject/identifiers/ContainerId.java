package es.ull.project.domain.valueobject.identifiers;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object that represents the unique identifier of a container.
 * It is defined only by its UUID value.
 */
public final class ContainerId {

    private static final String ERROR_ID_NOT_DEFINED = "ContainerId is not defined";
    private static final String ERROR_ID_INVALID = "ContainerId is not a valid UUID";

    /**
     * Unique identifier of the container.
     * Required attribute.
     */
    private final UUID value;

    /**
     * It creates a new ContainerId.
     *
     * @param value UUID value of the container identifier.
     */
    public ContainerId(UUID value) {
        this.validate(value);
        this.value = value;
    }

    /**
     * It creates a new ContainerId from a string representation.
     *
     * @param value String representation of the UUID.
     */
    public ContainerId(String value) {
        this.validate(value);
        this.value = UUID.fromString(value);
    }

    /**
     * Validates the UUID value.
     *
     * @param value UUID to validate.
     */
    private void validate(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException(ERROR_ID_NOT_DEFINED);
        }
    }

    /**
     * Validates the string representation of the UUID.
     *
     * @param value String UUID to validate.
     */
    private void validate(String value) {
        if (value == null) {
            throw new IllegalArgumentException(ERROR_ID_NOT_DEFINED);
        }
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(ERROR_ID_INVALID);
        }
    }

    /**
     * It returns the UUID value.
     *
     * @return UUID value.
     */
    public UUID getValue() {
        return this.value;
    }

    /**
     * Compares two ContainerId objects by value.
     *
     * @param otherObject Object to compare.
     * @return True if both ContainerId are equal, false otherwise.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (this.getClass() != otherObject.getClass()) {
            return false;
        }
        ContainerId otherContainerId = (ContainerId) otherObject;
        return this.value.equals(otherContainerId.value);
    }

    /**
     * It returns the hash code of the ContainerId.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    /**
     * It returns the string representation of the ContainerId.
     *
     * @return String representation.
     */
    @Override
    public String toString() {
        return String.format("ContainerId={value='%s'}", this.value);
    }
}
