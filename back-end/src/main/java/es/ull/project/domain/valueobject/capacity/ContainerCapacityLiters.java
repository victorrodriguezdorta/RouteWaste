package es.ull.project.domain.valueobject.capacity;

import java.util.Objects;

/**
 * ContainerCapacityLiters
 *
 * Represents the maximum capacity of a container in liters.
 * Immutable value object that encapsulates the capacity value in liters.
 * The unit is always liters (fixed).
 */
public final class ContainerCapacityLiters {

    /**
     * Error messages for validation.
     */
    private static final String ERROR_CAPACITY_NEGATIVE = "Container capacity cannot be negative";
    private static final String ERROR_OTHER_CAPACITY_NULL = "Other ContainerCapacityLiters cannot be null";
    private static final int ZERO = 0;

    /**
     * Required.
     * Capacity value in liters.
     * It is a required attribute and always measured in liters.
     */
    private final double liters;

    /**
     * Creates a new ContainerCapacityLiters.
     *
     * @param liters Capacity value in liters (must be ≥ 0)
     * @throws IllegalArgumentException if liters is negative
     */
    public ContainerCapacityLiters(double liters) {
        if (liters < ZERO) {
            throw new IllegalArgumentException(ERROR_CAPACITY_NEGATIVE);
        }
        this.liters = liters;
    }

    /**
     * Returns the capacity value in liters.
     *
     * @return Capacity value in liters
     */
    @com.fasterxml.jackson.annotation.JsonValue
    public double getLiters() {
        return this.liters;
    }

    /**
     * Returns a new ContainerCapacityLiters with the updated value.
     *
     * @param newLiters New capacity value in liters
     * @return New ContainerCapacityLiters
     * @throws IllegalArgumentException if newLiters is negative
     */
    public ContainerCapacityLiters setLiters(double newLiters) {
        return new ContainerCapacityLiters(newLiters);
    }

    /**
     * Compares if this capacity is greater than another.
     *
     * @param other Other ContainerCapacityLiters
     * @return true if greater, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThan(ContainerCapacityLiters other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.liters > other.liters;
    }

    /**
     * Compares if this capacity is greater than or equal to another.
     *
     * @param other Other ContainerCapacityLiters
     * @return true if greater or equal, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThanOrEqual(ContainerCapacityLiters other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.liters >= other.liters;
    }

    /**
     * Compares if this capacity is less than another.
     *
     * @param other Other ContainerCapacityLiters
     * @return true if less, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean lessThan(ContainerCapacityLiters other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.liters < other.liters;
    }

    /**
     * Compares equality of two ContainerCapacityLiters instances.
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
        ContainerCapacityLiters other = (ContainerCapacityLiters) otherObject;
        return Double.compare(this.liters, other.liters) == ZERO;
    }

    /**
     * Returns the hash code of the ContainerCapacityLiters.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(liters);
    }

    /**
     * Returns the string representation of the ContainerCapacityLiters.
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format("ContainerCapacityLiters={liters=%s}", this.liters);
    }
}
