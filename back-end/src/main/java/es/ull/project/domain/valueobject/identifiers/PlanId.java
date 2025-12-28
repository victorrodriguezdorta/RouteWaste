package es.ull.project.domain.valueobject.identifiers;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing the unique identifier of an infrastructure plan.
 * Defined only by its value (UUID). No behavior apart from equality.
 */
public final class PlanId {

    private static final String ERROR_ID_NOT_DEFINED = "PlanId is not defined";

    private final UUID value;

    /**
     * Creates a new PlanId from a UUID.
     *
     * @param value UUID of the plan.
     */
    public PlanId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException(ERROR_ID_NOT_DEFINED);
        }
        this.value = value;
    }

    /**
     * Creates a new PlanId with a random UUID.
     */
    public PlanId() {
        this.value = UUID.randomUUID();
    }

    /**
     * Returns the UUID value of the PlanId.
     *
     * @return UUID value
     */
    public UUID getValue() {
        return this.value;
    }

    /**
     * Checks equality based on UUID value.
     *
     * @param otherObject Object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        PlanId otherPlanId = (PlanId) otherObject;
        return value.equals(otherPlanId.value);
    }

    /**
     * Returns the hash code of the PlanId based on UUID value.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns the string representation of the PlanId.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("PlanId={value='%s'}", this.value.toString());
    }
}