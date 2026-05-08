package es.ull.project.domain.valueobject.error;

import java.util.Objects;

/**
 * ErrorMessage
 *
 * Represents a human-readable error message in an error response.
 * Immutable value object wrapping a non-blank string.
 * It is a required attribute.
 */
public final class ErrorMessage {

    private static final String ERROR_MESSAGE_NULL = "Error message must not be null or blank";

    /**
     * The human-readable error message.
     * It is a required attribute.
     */
    private final String message;

    /**
     * Creates a new ErrorMessage.
     *
     * @param message the human-readable message; must not be null or blank
     * @throws IllegalArgumentException if message is null or blank
     */
    public ErrorMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NULL);
        }
        this.message = message;
    }

    /**
     * Returns the human-readable error message.
     *
     * @return the message string
     */
    public String getValue() {
        return message;
    }

    /**
     * Checks equality based on the message value.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if the other object has the same message
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        ErrorMessage other = (ErrorMessage) otherObject;
        return message.equals(other.message);
    }

    /**
     * Returns a hash code based on the message value.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    /**
     * Returns a string representation.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "ErrorMessage={" + message + "}";
    }
}
