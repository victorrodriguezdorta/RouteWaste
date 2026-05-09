package es.ull.project.domain.valueobject.route;

import java.util.Objects;

/**
 * RouteSequence
 *
 * Represents the 1-based sequence order of a stop within a route.
 * This is an immutable value object.
 */
public final class RouteSequence {

    private static final String ERROR_SEQUENCE_NON_POSITIVE = "Sequence must be a positive integer greater than zero";
    private static final int MIN_SEQUENCE = 1;

    /**
     * The sequence number.
     * It is a required attribute.
     */
    private final int value;

    /**
     * Private constructor for the value object.
     *
     * @param value The sequence number.
     */
    private RouteSequence(int value) {
        validateSequence(value);
        this.value = value;
    }

    /**
     * Validates that the provided sequence is strictly positive.
     *
     * @param value Sequence to validate.
     * @throws IllegalArgumentException if the sequence is less than 1.
     */
    private void validateSequence(int value) {
        if (value < MIN_SEQUENCE) {
            throw new IllegalArgumentException(ERROR_SEQUENCE_NON_POSITIVE);
        }
    }

    /**
     * Factory method to create an instance from an integer value.
     *
     * @param value The sequence integer.
     * @return A new instance of RouteSequence.
     */
    public static RouteSequence of(int value) {
        return new RouteSequence(value);
    }

    /**
     * Returns the sequence integer value.
     *
     * @return The sequence number.
     */
    public int getValue() {
        return value;
    }

    /**
     * Compares this RouteSequence to another object for equality.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if the other object is a RouteSequence with the same sequence value
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        RouteSequence other = (RouteSequence) otherObject;
        return value == other.value;
    }

    /**
     * Returns a hash code for this RouteSequence based on its sequence value.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns a human-readable string representation of this RouteSequence.
     *
     * @return a string in the format {@code RouteSequence={n}}
     */
    @Override
    public String toString() {
        return String.format("RouteSequence={%d}", value);
    }
}
