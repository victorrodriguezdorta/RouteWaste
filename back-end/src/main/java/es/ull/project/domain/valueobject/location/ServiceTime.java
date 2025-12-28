package es.ull.project.domain.valueobject.location;


import java.util.Objects;

/**
 * ServiceTime
 * 
 * Represents the travel plus service time between a container and a facility.
 * This is a mandatory value object in the domain.
 */
public final class ServiceTime {

    private static final String ERROR_TIME_NEGATIVE = "Service time cannot be negative";

    /**
     * Service time value (e.g., in minutes or hours).
     */
    private final double value;

    /**
     * Creates a new ServiceTime.
     * 
     * @param value Service time value. Must be ≥ 0.
     */
    public ServiceTime(double value) {
        this.validateTime(value);
        this.value = value;
    }

    private void validateTime(double value) {
        if (value < 0) {
            throw new IllegalArgumentException(ERROR_TIME_NEGATIVE);
        }
    }

    /**
     * Returns the service time value.
     * 
     * @return Service time.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns a new ServiceTime with a new value.
     * 
     * @param newValue New service time.
     * @return New ServiceTime object.
     */
    public ServiceTime setValue(double newValue) {
        return new ServiceTime(newValue);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        ServiceTime other = (ServiceTime) otherObject;
        return Double.compare(other.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("ServiceTime={value=%s}", value);
    }
}