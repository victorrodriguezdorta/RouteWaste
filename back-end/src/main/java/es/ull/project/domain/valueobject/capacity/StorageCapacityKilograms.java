package es.ull.project.domain.valueobject.capacity;

import java.util.Objects;

/**
 * StorageCapacityKilograms
 *
 * Represents the maximum storage capacity of a facility in kilograms.
 * Immutable value object that encapsulates the storage capacity value in kilograms.
 * The unit is always kilograms (fixed).
 */
public final class StorageCapacityKilograms {

    /**
     * Error messages for validation.
     */
    private static final String ERROR_CAPACITY_NEGATIVE = "Storage capacity cannot be negative";
    private static final String ERROR_OTHER_CAPACITY_NULL = "Other StorageCapacityKilograms cannot be null";
    private static final int ZERO = 0;

    /**
     * Required.
     * Storage capacity value in kilograms.
     * It is a required attribute and always measured in kilograms.
     */
    private final double kilograms;

    /**
     * Creates a new StorageCapacityKilograms.
     *
     * @param kilograms Storage capacity value in kilograms (must be ≥ 0)
     * @throws IllegalArgumentException if kilograms is negative
     */
    public StorageCapacityKilograms(double kilograms) {
        if (kilograms < ZERO) {
            throw new IllegalArgumentException(ERROR_CAPACITY_NEGATIVE);
        }
        this.kilograms = kilograms;
    }

    /**
     * Returns the storage capacity value in kilograms.
     *
     * @return Storage capacity value in kilograms
     */
    public double getKilograms() {
        return this.kilograms;
    }

    /**
     * Returns a new StorageCapacityKilograms with the updated value.
     *
     * @param newKilograms New storage capacity value in kilograms
     * @return New StorageCapacityKilograms
     * @throws IllegalArgumentException if newKilograms is negative
     */
    public StorageCapacityKilograms setKilograms(double newKilograms) {
        return new StorageCapacityKilograms(newKilograms);
    }

    /**
     * Compares if this capacity is greater than another.
     *
     * @param other Other StorageCapacityKilograms
     * @return true if greater, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThan(StorageCapacityKilograms other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.kilograms > other.kilograms;
    }

    /**
     * Compares if this capacity is greater than or equal to another.
     *
     * @param other Other StorageCapacityKilograms
     * @return true if greater or equal, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean greaterThanOrEqual(StorageCapacityKilograms other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.kilograms >= other.kilograms;
    }

    /**
     * Compares if this capacity is less than another.
     *
     * @param other Other StorageCapacityKilograms
     * @return true if less, false otherwise
     * @throws IllegalArgumentException if the other object is null
     */
    public boolean lessThan(StorageCapacityKilograms other) {
        if (other == null) {
            throw new IllegalArgumentException(ERROR_OTHER_CAPACITY_NULL);
        }
        return this.kilograms < other.kilograms;
    }

    /**
     * Compares equality of two StorageCapacityKilograms instances.
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
        StorageCapacityKilograms other = (StorageCapacityKilograms) otherObject;
        return Double.compare(this.kilograms, other.kilograms) == ZERO;
    }

    /**
     * Returns the hash code of the StorageCapacityKilograms.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(kilograms);
    }

    /**
     * Returns the string representation of the StorageCapacityKilograms.
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format("StorageCapacityKilograms={kilograms=%s}", this.kilograms);
    }
}
