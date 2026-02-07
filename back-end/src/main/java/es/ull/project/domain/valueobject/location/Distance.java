package es.ull.project.domain.valueobject.location;

import java.util.Objects;

/**
 * Distance
 * 
 * Represents the distance between a container and a facility.
 * This is a mandatory value object in the domain.
 * 
 * Internally stores distance in meters (SI base unit) but provides
 * conversion methods for different units.
 */
public final class Distance {

    private static final String ERROR_DISTANCE_NEGATIVE = "Distance cannot be negative";
    private static final int ZERO = 0;

    /**
     * Distance value in meters (base unit).
     * Always stored with meter precision.
     * It is a required attribute.
     */
    private final double meters;

    /**
     * Creates a new Distance from meters.
     * 
     * @param meters Distance in meters. Must be ≥ 0.
     */
    private Distance(double meters) {
        this.validateDistance(meters);
        this.meters = meters;
    }

    /**
     * Validates that the provided distance is not negative.
     *
     * @param value Distance value to validate (meters)
     * @throws IllegalArgumentException if the value is negative
     */
    private void validateDistance(double value) {
        if (value < ZERO) {
            throw new IllegalArgumentException(ERROR_DISTANCE_NEGATIVE);
        }
    }

    /**
     * Creates a Distance from meters.
     * 
     * @param meters Distance in meters.
     * @return New Distance instance.
     */
    public static Distance fromMeters(double meters) {
        return new Distance(meters);
    }

    /**
     * Creates a Distance from kilometers.
     * 
     * @param kilometers Distance in kilometers.
     * @return New Distance instance.
     */
    public static Distance fromKilometers(double kilometers) {
        return new Distance(kilometers * 1000.0);
    }

    /**
     * Creates a Distance from miles.
     * 
     * @param miles Distance in miles.
     * @return New Distance instance.
     */
    public static Distance fromMiles(double miles) {
        return new Distance(miles * 1609.34);
    }

    /**
     * Returns the distance in meters.
     * 
     * @return Distance in meters.
     */
    public double toMeters() {
        return this.meters;
    }

    /**
     * Returns the distance in kilometers.
     * 
     * @return Distance in kilometers.
     */
    public double toKilometers() {
        return this.meters / 1000.0;
    }

    /**
     * Returns the distance in miles.
     * 
     * @return Distance in miles.
     */
    public double toMiles() {
        return this.meters / 1609.34;
    }

    /**
     * Returns the distance value in meters (base unit).
     * 
     * @return Distance in meters.
     */
    public double getValue() {
        return this.meters;
    }

    /**
     * Returns a new Distance with a new value in meters.
     * 
     * @param newMeters New distance in meters.
     * @return New Distance object.
     */
    public Distance setValue(double newMeters) {
        return Distance.fromMeters(newMeters);
    }

    /**
     * Compares this Distance with another object for equality.
     *
     * @param otherObject the object to compare with
     * @return true if both objects represent the same distance (in meters), false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Distance other = (Distance) otherObject;
        return Double.compare(other.meters, meters) == ZERO;
    }

    /**
     * Returns the hash code for this Distance.
     *
     * @return hash code based on the meters value
     */
    @Override
    public int hashCode() {
        return Objects.hash(meters);
    }

    /**
     * Returns a string representation of the Distance.
     *
     * @return formatted string with meters and kilometers
     */
    @Override
    public String toString() {
        return String.format("Distance={meters=%.2f m, kilometers=%.2f km}", meters, toKilometers());
    }
}