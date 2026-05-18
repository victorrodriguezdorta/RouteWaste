package es.ull.project.domain.valueobject.alert;

import java.util.Objects;

/**
 * Value object representing an optional numeric context value for a stop alert.
 */
public final class StopAlertValue {

    public static final int COMPARE_EQUALS = 0;

    /**
     * Numeric context value attached to a stop alert.
     * It is a required attribute.
     */
    private final double value;

    /**
     * Creates a stop alert numeric value.
     *
     * @param value numeric context value
     */
    public StopAlertValue(double value) {
        this.value = value;
    }

    /**
     * Returns the numeric context value.
     *
     * @return alert numeric value
     */
    public double getValue() {
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
        StopAlertValue other = (StopAlertValue) otherObject;
        return Double.compare(other.value, this.value) == COMPARE_EQUALS;
    }

    /**
     * Returns a hash code for this value.
     *
     * @return hash code based on the numeric value
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
        return "StopAlertValue={" + this.value + "}";
    }
}
