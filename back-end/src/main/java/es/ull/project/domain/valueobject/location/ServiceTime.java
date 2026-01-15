package es.ull.project.domain.valueobject.location;


import java.util.Objects;

/**
 * ServiceTime
 * 
 * Represents the travel plus service time between a container and a facility.
 * This is a mandatory value object in the domain.
 * 
 * The internal value is always stored in minutes.
 */
public final class ServiceTime {

    private static final String ERROR_TIME_NEGATIVE = "Service time cannot be negative";

    /**
     * Service time value in minutes.
     */
    private final double value;

    /**
     * Creates a new ServiceTime in minutes.
     * 
     * @param value Service time value in minutes. Must be ≥ 0.
     */
    public ServiceTime(double value) {
        this.validateTime(value);
        this.value = value;
    }

    /**
     * Creates a new ServiceTime from hours.
     * 
     * @param hours Service time in hours. Must be ≥ 0.
     * @return New ServiceTime object.
     */
    public static ServiceTime fromHours(double hours) {
        double minutes = hours * 60.0;
        return new ServiceTime(minutes);
    }

    /**
     * Creates a new ServiceTime from seconds.
     * 
     * @param seconds Service time in seconds. Must be ≥ 0.
     * @return New ServiceTime object.
     */
    public static ServiceTime fromSeconds(double seconds) {
        double minutes = seconds / 60.0;
        return new ServiceTime(minutes);
    }

    private void validateTime(double value) {
        if (value < 0) {
            throw new IllegalArgumentException(ERROR_TIME_NEGATIVE);
        }
    }

    /**
     * Returns the service time value in minutes.
     * 
     * @return Service time in minutes.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns the service time value in hours.
     * 
     * @return Service time in hours.
     */
    public double getValueInHours() {
        return this.value / 60.0;
    }

    /**
     * Returns the service time value in seconds.
     * 
     * @return Service time in seconds.
     */
    public double getValueInSeconds() {
        return this.value * 60.0;
    }

    /**
     * Returns a new ServiceTime with a new value in minutes.
     * 
     * @param newValue New service time in minutes.
     * @return New ServiceTime object.
     */
    public ServiceTime setValue(double newValue) {
        return new ServiceTime(newValue);
    }

    /**
     * Returns a new ServiceTime with a new value in hours.
     * 
     * @param hours New service time in hours.
     * @return New ServiceTime object.
     */
    public ServiceTime setValueInHours(double hours) {
        return ServiceTime.fromHours(hours);
    }

    /**
     * Returns a new ServiceTime with a new value in seconds.
     * 
     * @param seconds New service time in seconds.
     * @return New ServiceTime object.
     */
    public ServiceTime setValueInSeconds(double seconds) {
        return ServiceTime.fromSeconds(seconds);
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
        return String.format("ServiceTime={value=%s min}", value);
    }
}