package es.ull.project.domain.valueobject.algorithm;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * CollectionStartTime
 *
 * Represents the time of day when the waste collection journey starts. It is
 * used to schedule the hours at which each vehicle visits its stops.
 * Immutable value object that encapsulates a {@link LocalTime} and exposes it
 * using the canonical {@code HH:mm} representation.
 * It is an optional attribute.
 */
public final class CollectionStartTime {

    private static final String TIME_PATTERN = "HH:mm";
    private static final String ERROR_TIME_NOT_DEFINED = "Collection start time is not defined";
    private static final String ERROR_TIME_NOT_VALID = "Collection start time must follow the " + TIME_PATTERN + " format";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    /**
     * The collection start time.
     * It is a required attribute.
     */
    private final LocalTime value;

    /**
     * Creates a new CollectionStartTime from a {@link LocalTime}.
     *
     * @param value the start time; must not be null
     * @throws IllegalArgumentException if the value is null
     */
    public CollectionStartTime(LocalTime value) {
        if (value == null) {
            throw new IllegalArgumentException(ERROR_TIME_NOT_DEFINED);
        }
        this.value = value;
    }

    /**
     * Creates a new CollectionStartTime from a textual {@code HH:mm} value.
     *
     * @param value the start time in {@code HH:mm} format
     * @return the parsed value object
     * @throws IllegalArgumentException if the value is null, blank or malformed
     */
    public static CollectionStartTime fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(ERROR_TIME_NOT_DEFINED);
        }
        try {
            return new CollectionStartTime(LocalTime.parse(value.trim()));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(ERROR_TIME_NOT_VALID);
        }
    }

    /**
     * Returns the collection start time.
     *
     * @return the start time as a {@link LocalTime}
     */
    public LocalTime getValue() {
        return value;
    }

    /**
     * Returns the collection start time formatted as {@code HH:mm}.
     *
     * @return the canonical textual representation
     */
    public String getFormatted() {
        return value.format(FORMATTER);
    }

    /**
     * Checks equality based on the time value.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if the other object has the same value
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        CollectionStartTime other = (CollectionStartTime) otherObject;
        return Objects.equals(value, other.value);
    }

    /**
     * Returns a hash code based on the time value.
     *
     * @return hash code for this instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns a string representation of this value object.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "CollectionStartTime={" + getFormatted() + "}";
    }
}
