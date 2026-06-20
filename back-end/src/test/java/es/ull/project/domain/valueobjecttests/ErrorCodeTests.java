package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.error.ErrorCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ErrorCodeTests {

    private static final String VALID_ERROR_CODE = "ValidationError";
    private static final String DIFFERENT_ERROR_CODE = "NotFound";
    private static final String BLANK_ERROR_CODE = " ";
    private static final String BLANK_ERROR_CODE_MESSAGE = "Error code must not be null or blank";

    /**
     * Verifies that a valid error code is stored correctly.
     */
    @Test
    void constructorValidValue() {
        ErrorCode errorCode = new ErrorCode(VALID_ERROR_CODE);
        assertEquals(VALID_ERROR_CODE, errorCode.getValue());
    }

    /**
     * Verifies that blank error codes are rejected.
     */
    @Test
    void constructorBlankValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ErrorCode(BLANK_ERROR_CODE));
        assertEquals(BLANK_ERROR_CODE_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that error codes with equal values are equal and share the same hash code.
     */
    @Test
    void equalsSameValue() {
        ErrorCode firstErrorCode = new ErrorCode(VALID_ERROR_CODE);
        ErrorCode secondErrorCode = new ErrorCode(VALID_ERROR_CODE);
        assertEquals(firstErrorCode, secondErrorCode);
        assertEquals(firstErrorCode.hashCode(), secondErrorCode.hashCode());
    }

    /**
     * Verifies that error codes with different values are not equal.
     */
    @Test
    void equalsDifferentValue() {
        ErrorCode firstErrorCode = new ErrorCode(VALID_ERROR_CODE);
        ErrorCode secondErrorCode = new ErrorCode(DIFFERENT_ERROR_CODE);
        assertNotEquals(firstErrorCode, secondErrorCode);
    }
}
