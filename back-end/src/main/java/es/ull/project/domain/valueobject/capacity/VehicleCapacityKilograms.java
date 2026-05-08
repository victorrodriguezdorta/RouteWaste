package es.ull.project.domain.valueobject.capacity;

import java.util.Objects;

/**
 * VehicleCapacityKilograms
 *
 * Represents the maximum capacity of a vehicle in kilograms.
 * Immutable value object that encapsulates the capacity value in kilograms.
 * The unit is always kilograms (fixed).
 */
public final class VehicleCapacityKilograms {

    /**
     * Error messages for validation.
     */
    private static final String ERROR_CAPACITY_NEGATIVE = "Vehicle capacity cannot be negative";
    private static final String ERROR_OTHER_CAPACITY_NULL = "Other VehicleCapacityKilograms cannot be null";
    private static final int ZERO = 0;

    /**
     * Required.
     * Capacity value in kilograms.
     * It is a required attribute and always measured in kilograms.
     */
    private final double kilograms;

    /**
     * Creates a new VehicleCapacityKilograms.
     *
     * @param kilograms Capacity value in kilograms (must be ≥ 0)
     * @throws IllegalArgumentException if kilograms is negative
     */
    public VehicleCapacityKilograms(double kilograms) {
        if (kilograms < ZERO) {
            throw new IllegalArgumentException(ERROR_CAPACITY_NEGATIVE);
        }
        this.kilograms = kilograms;
    }

    /**
     * Returns the capacity value in kilograms.
     *
     * @return Capacity value in kilograms
     */
    @com.fasterxml.jackson.annotation.JsonValue
    public double getKilograms() {
        return this.kilograms;
    }

    /**
     * Returns a new VehicleCapacityKilograms with the updated value.
     *
     * @param newKilograms New capacity value in kilograms
     * @return New VehicleCapacityKilograms
     * @throws IllegalArgumentException if newKilograms is negative
     */
    public VehicleCapacityKilograms setKilograms(double newKilograms) {
        return new VehicleCapacityKilograms(newKilograms);
    }

    /**
     * Compares if this capacity is greater than another.
     *
     * @param other Other VehicleCapacityKilograms
     * @return true if greater, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThan(VehicleCapacityKilograms other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.kilograms > other.kilograms;
    }

    /**
     * Compares if this capacity is greater than or equal to another.
     *
     * @param other Other VehicleCapacityKilograms
     * @return true if greater or equal, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThanOrEqual(VehicleCapacityKilograms other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.kilograms >= other.kilograms;
    }

    /**
     * Compares if this capacity is less than another.
     *
     * @param other Other VehicleCapacityKilograms
     * @return true if less, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean lessThan(VehicleCapacityKilograms other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.kilograms < other.kilograms;
    }

    /**
     * Compares equality of two VehicleCapacityKilograms instances.
     *
     * @param otherObject Other object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        VehicleCapacityKilograms other = (VehicleCapacityKilograms) otherObject;
        return Double.compare(this.kilograms, other.kilograms) == ZERO;
    }

    /**
     * Returns the hash code of the VehicleCapacityKilograms.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(kilograms);
    }

    /**
     * Returns the string representation of the VehicleCapacityKilograms.
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format("VehicleCapacityKilograms={kilograms=%s}", this.kilograms);
    }
}
