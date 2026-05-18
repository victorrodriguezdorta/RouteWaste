package es.ull.project.domain.valueobject.time;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * ExecutedAt
 *
 * Represents the execution timestamp of an algorithm, typically in ISO-8601 format.
 * It is a Value Object.
 */
public class ExecutedAt {

    public static final String EXECUTED_AT_NULL = "Execution time must not be null";
    public static final String EXECUTED_AT_INVALID = "Execution time must be a valid ISO-8601 format";

    /**
     * The execution timestamp in ISO-8601 string format.
     * It is a required attribute.
     */
    private final String timestamp;

    /**
     * Creates a new ExecutedAt.
     *
     * @param timestamp execution timestamp in ISO-8601 string format
     */
    public ExecutedAt(String timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException(EXECUTED_AT_NULL);
        }
        if (timestamp.trim().isEmpty()) {
            throw new IllegalArgumentException(EXECUTED_AT_INVALID);
        }
        try {
            ZonedDateTime.parse(timestamp);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(EXECUTED_AT_INVALID);
        }
        this.timestamp = timestamp;
    }

    /**
     * Returns the timestamp as a string.
     *
     * @return the execution timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Compares this execution timestamp with another object.
     *
     * @param otherObject object to compare with
     * @return true when both execution timestamps are equal
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        ExecutedAt that = (ExecutedAt) otherObject;
        return Objects.equals(timestamp, that.timestamp);
    }

    /**
     * Returns the hash code for this execution timestamp.
     *
     * @return hash code based on the timestamp
     */
    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    /**
     * Returns the timestamp as text.
     *
     * @return execution timestamp string
     */
    @Override
    public String toString() {
        return timestamp;
    }
}
