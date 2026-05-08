package es.ull.project.domain.valueobject.error;

import java.util.Objects;

/**
 * ErrorCode
 *
 * Represents the machine-readable error code label in an error response
 * (e.g. {@code "ValidationError"}, {@code "NotFound"}).
 * Immutable value object wrapping a non-blank string.
 * It is a required attribute.
 */
public final class ErrorCode {

    private static final String ERROR_CODE_NULL = "Error code must not be null or blank";

    /**
     * The error code label.
     * It is a required attribute.
     */
    private final String code;

    /**
     * Creates a new ErrorCode.
     *
     * @param code the error code label; must not be null or blank
     * @throws IllegalArgumentException if code is null or blank
     */
    public ErrorCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(ERROR_CODE_NULL);
        }
        this.code = code;
    }

    /**
     * Returns the error code label.
     *
     * @return the error code string
     */
    public String getValue() {
        return code;
    }

    /**
     * Checks equality based on the code value.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if the other object has the same code
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        ErrorCode other = (ErrorCode) otherObject;
        return code.equals(other.code);
    }

    /**
     * Returns a hash code based on the code value.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    /**
     * Returns a string representation.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "ErrorCode={" + code + "}";
    }
}
