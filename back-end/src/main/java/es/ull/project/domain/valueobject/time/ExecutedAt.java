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

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return timestamp;
    }
}
