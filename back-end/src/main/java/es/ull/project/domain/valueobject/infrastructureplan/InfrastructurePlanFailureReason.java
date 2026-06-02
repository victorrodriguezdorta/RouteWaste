package es.ull.project.domain.valueobject.infrastructureplan;

import java.util.Objects;

/**
 * Value object representing why an infrastructure plan execution failed.
 */
public final class InfrastructurePlanFailureReason {

    private static final String REASON_NOT_DEFINED = "Infrastructure plan failure reason must not be null or blank";

    /**
     * Human-readable failure description.
     * It is a required attribute.
     */
    private final String value;

    /**
     * Creates a failure reason from non-blank text.
     *
     * @param value failure description
     */
    public InfrastructurePlanFailureReason(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(REASON_NOT_DEFINED);
        }
        this.value = value.trim();
    }

    /**
     * Builds a failure reason when the raw text is present; otherwise returns null.
     *
     * @param value optional failure description
     * @return failure reason value object, or null when absent or blank
     */
    public static InfrastructurePlanFailureReason fromNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new InfrastructurePlanFailureReason(value);
    }

    /**
     * Returns the failure description text.
     *
     * @return failure reason
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Compares this reason with another object.
     *
     * @param otherObject object to compare
     * @return true when both reasons have the same text
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        InfrastructurePlanFailureReason other = (InfrastructurePlanFailureReason) otherObject;
        return Objects.equals(this.value, other.value);
    }

    /**
     * Returns a hash code for this reason.
     *
     * @return hash code based on the description text
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    /**
     * Returns a string representation of this reason.
     *
     * @return formatted reason
     */
    @Override
    public String toString() {
        return "InfrastructurePlanFailureReason={" + this.value + "}";
    }
}
