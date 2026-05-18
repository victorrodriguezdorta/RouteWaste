package es.ull.project.domain.valueobject.alert;

import java.util.Objects;

/**
 * Value object representing the type of an alert generated in a stop.
 */
public final class StopAlertType {

    private static final String TYPE_NOT_DEFINED = "Stop alert type must not be null or blank";

    /**
     * Validated alert type text.
     * It is a required attribute.
     */
    private final String value;

    /**
     * Creates a validated stop alert type.
     *
     * @param value raw alert type
     */
    public StopAlertType(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(TYPE_NOT_DEFINED);
        }
        this.value = value.trim();
    }

    /**
     * Returns the alert type value.
     *
     * @return alert type text
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Compares this type with another object.
     *
     * @param otherObject object to compare
     * @return true when both types have the same value
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        StopAlertType other = (StopAlertType) otherObject;
        return Objects.equals(this.value, other.value);
    }

    /**
     * Returns a hash code for this type.
     *
     * @return hash code based on the type value
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    /**
     * Returns a string representation of this type.
     *
     * @return formatted type
     */
    @Override
    public String toString() {
        return "StopAlertType={" + this.value + "}";
    }
}
