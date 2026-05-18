package es.ull.project.domain.valueobject.alert;

import java.util.Objects;

/**
 * Value object representing the human-readable message of a stop alert.
 */
public final class StopAlertMessage {

    private static final String MESSAGE_NOT_DEFINED = "Stop alert message must not be null or blank";

    /**
     * Validated human-readable alert message text.
     * It is a required attribute.
     */
    private final String value;

    /**
     * Creates a validated stop alert message.
     *
     * @param value raw alert message
     */
    public StopAlertMessage(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(MESSAGE_NOT_DEFINED);
        }
        this.value = value.trim();
    }

    /**
     * Returns the alert message value.
     *
     * @return alert message text
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Compares this message with another object.
     *
     * @param otherObject object to compare
     * @return true when both messages have the same value
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        StopAlertMessage other = (StopAlertMessage) otherObject;
        return Objects.equals(this.value, other.value);
    }

    /**
     * Returns a hash code for this message.
     *
     * @return hash code based on the message value
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    /**
     * Returns a string representation of this message.
     *
     * @return formatted message
     */
    @Override
    public String toString() {
        return "StopAlertMessage={" + this.value + "}";
    }
}
