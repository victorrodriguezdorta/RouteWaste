package es.ull.project.domain.valueobject.location;


import java.util.Objects;

/**
 * ServiceRadius
 * 
 * Represents the maximum allowed service radius for a facility.
 * This is a mandatory value object in the domain.
 */
public final class ServiceRadius {

    private static final String ERROR_RADIUS_NEGATIVE = "Service radius cannot be negative";

    /**
     * Service radius value (e.g., in kilometers).
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

    private void validateRadius(double value) {
        if (value < 0) {
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

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        ServiceRadius other = (ServiceRadius) otherObject;
        return Double.compare(other.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("ServiceRadius={value=%s}", value);
    }
}