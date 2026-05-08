package es.ull.project.domain.valueobject.capacity;

import java.util.Objects;

/**
 * CollectedWeightKilograms
 *
 * Represents the total weight collected in kilograms during a routing operation
 * (e.g., in a specific stop or across an entire daily plan).
 * This is an immutable value object.
 */
public final class CollectedWeightKilograms {

    private static final String ERROR_WEIGHT_NEGATIVE = "Collected weight cannot be negative";
    private static final double ZERO = 0.0;

    /**
     * Weight in kilograms.
     * It is a required attribute.
     */
    private final double kilograms;

    /**
     * Private constructor for the value object.
     *
     * @param kilograms The weight in kilograms.
     */
    private CollectedWeightKilograms(double kilograms) {
        validateWeight(kilograms);
        this.kilograms = kilograms;
    }

    /**
     * Validates that the provided weight is not negative.
     *
     * @param value Weight to validate.
     * @throws IllegalArgumentException if the weight is negative.
     */
    private void validateWeight(double value) {
        if (value < ZERO) {
            throw new IllegalArgumentException(ERROR_WEIGHT_NEGATIVE);
        }
    }

    /**
     * Factory method to create an instance from kilograms.
     *
     * @param kilograms The weight in kilograms.
     * @return A new instance of CollectedWeightKilograms.
     */
    public static CollectedWeightKilograms fromKilograms(double kilograms) {
        return new CollectedWeightKilograms(kilograms);
    }

    /**
     * Returns the weight in kilograms.
     *
     * @return The weight in kilograms.
     */
    public double getValue() {
        return kilograms;
    }

    /**
     * Checks equality based on the kilograms value.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if the other object is a CollectedWeightKilograms with the same value
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        CollectedWeightKilograms other = (CollectedWeightKilograms) otherObject;
        return Double.compare(other.kilograms, kilograms) == (int) ZERO;
    }

    /**
     * Returns a hash code based on the kilograms value.
     *
     * @return hash code for this instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(kilograms);
    }

    /**
     * Returns a human-readable string representation of this value object.
     *
     * @return string representation including the kilograms value
     */
    @Override
    public String toString() {
        return String.format("CollectedWeightKilograms={%.2f kg}", kilograms);
    }
}
