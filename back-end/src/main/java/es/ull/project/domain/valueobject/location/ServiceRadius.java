package es.ull.project.domain.valueobject.location;

import java.util.Objects;

/**
 * ServiceRadius
 * 
 * Represents the maximum allowed service radius for a facility.
 * This is a mandatory value object in the domain.
 */
public final class ServiceRadius {

    private static final int ZERO = 0;
    private static final String ERROR_RADIUS_NEGATIVE = "Service radius cannot be negative";

        /**
         * Required.
         * Service radius value in meters.
         */
        private final double value;

    /**
     * Creates a new ServiceRadius.
     * 
     * @param value Service radius value. Must be ≥ 0.
     */
    public ServiceRadius(double value) {
        this.validateRadius(value);
        this.value = value;
    }

    /**
     * Validates that the radius value is not negative.
     *
     * @param value the radius value to validate
     * @throws IllegalArgumentException if the value is negative
     */
    private void validateRadius(double value) {
        if (value < ZERO) {
            throw new IllegalArgumentException(ERROR_RADIUS_NEGATIVE);
        }
    }

    /**
     * Returns the service radius value.
     * 
     * @return Service radius.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns a new ServiceRadius with a new value.
     * 
     * @param newValue New radius value.
     * @return New ServiceRadius object.
     */
    public ServiceRadius setValue(double newValue) {
        return new ServiceRadius(newValue);
    }

    /**
     * Compares this ServiceRadius to another object for equality.
     *
     * @param otherObject the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        ServiceRadius other = (ServiceRadius) otherObject;
        return Double.compare(other.value, value) == ZERO;
    }

    /**
     * Returns the hash code for this ServiceRadius.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns a string representation of this ServiceRadius.
     *
     * @return a formatted string containing the radius value
     */
    @Override
    public String toString() {
        return String.format("ServiceRadius={value=%s}", value);
    }
}