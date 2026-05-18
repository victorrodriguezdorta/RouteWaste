package es.ull.project.domain.valueobject.infrastructureplan;

import java.util.Objects;

/**
 * Value object indicating whether an infrastructure plan references an entity.
 */
public final class PlanReferencePresence {

    /**
     * Wrapped boolean value indicating if a plan reference exists.
     * It is a required attribute.
     */
    private final boolean value;

    /**
     * Creates a plan reference presence value.
     *
     * @param value true when a plan references the entity
     */
    public PlanReferencePresence(boolean value) {
        this.value = value;
    }

    /**
     * Returns the wrapped boolean value.
     *
     * @return true when a plan references the entity
     */
    public boolean getValue() {
        return this.value;
    }

    /**
     * Compares this value with another object.
     *
     * @param otherObject object to compare
     * @return true when both values are equal
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        PlanReferencePresence other = (PlanReferencePresence) otherObject;
        return this.value == other.value;
    }

    /**
     * Returns a hash code for this value.
     *
     * @return hash code based on the wrapped value
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    /**
     * Returns a string representation of this value.
     *
     * @return formatted value
     */
    @Override
    public String toString() {
        return "PlanReferencePresence={" + this.value + "}";
    }
}
