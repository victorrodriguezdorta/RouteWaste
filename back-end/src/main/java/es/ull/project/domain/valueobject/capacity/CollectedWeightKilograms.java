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

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        CollectedWeightKilograms other = (CollectedWeightKilograms) otherObject;
        return Double.compare(other.kilograms, kilograms) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kilograms);
    }

    @Override
    public String toString() {
        return String.format("CollectedWeightKilograms={%.2f kg}", kilograms);
    }
}
