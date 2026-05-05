package es.ull.project.domain.valueobject.capacity;

import java.util.Objects;

/**
 * CollectedVolumeLiters
 *
 * Represents the total volume collected in liters during a routing operation
 * (e.g., in a specific stop or across an entire daily plan).
 * This is an immutable value object.
 */
public final class CollectedVolumeLiters {

    private static final String ERROR_VOLUME_NEGATIVE = "Collected volume cannot be negative";
    private static final double ZERO = 0.0;

    /**
     * Volume in liters.
     */
    private final double liters;

    /**
     * Private constructor for the value object.
     *
     * @param liters The volume in liters.
     */
    private CollectedVolumeLiters(double liters) {
        validateVolume(liters);
        this.liters = liters;
    }

    /**
     * Validates that the provided volume is not negative.
     *
     * @param value Volume to validate.
     * @throws IllegalArgumentException if the volume is negative.
     */
    private void validateVolume(double value) {
        if (value < ZERO) {
            throw new IllegalArgumentException(ERROR_VOLUME_NEGATIVE);
        }
    }

    /**
     * Factory method to create an instance from liters.
     *
     * @param liters The volume in liters.
     * @return A new instance of CollectedVolumeLiters.
     */
    public static CollectedVolumeLiters fromLiters(double liters) {
        return new CollectedVolumeLiters(liters);
    }

    /**
     * Returns the volume in liters.
     *
     * @return The volume in liters.
     */
    public double getValue() {
        return liters;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        CollectedVolumeLiters other = (CollectedVolumeLiters) otherObject;
        return Double.compare(other.liters, liters) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(liters);
    }

    @Override
    public String toString() {
        return String.format("CollectedVolumeLiters={%.2f L}", liters);
    }
}
