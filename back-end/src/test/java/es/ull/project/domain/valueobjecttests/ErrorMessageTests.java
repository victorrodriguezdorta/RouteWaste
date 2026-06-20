package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.error.ErrorMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ErrorMessageTests {

    private static final String VALID_ERROR_MESSAGE = "Invalid payload";
    private static final String DIFFERENT_ERROR_MESSAGE = "Missing field";
    private static final String BLANK_ERROR_MESSAGE = " ";
    private static final String ERROR_MESSAGE_NOT_DEFINED_MESSAGE =
            "Error message must not be null or blank";

    /**
     * Verifies that a valid error message is stored correctly.
     */
    @Test
    void constructorValidErrorMessage() {
        ErrorMessage errorMessage = new ErrorMessage(VALID_ERROR_MESSAGE);
        assertThat(errorMessage.getValue()).isEqualTo(VALID_ERROR_MESSAGE);
    }

    /**
     * Verifies that blank error messages are rejected.
     */
    @Test
    void constructorBlankErrorMessage() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ErrorMessage(BLANK_ERROR_MESSAGE));
        assertThat(exception.getMessage()).isEqualTo(ERROR_MESSAGE_NOT_DEFINED_MESSAGE);
    }

    /**
     * Verifies that error messages with equal values are equal and share the same hash code.
     */
    @Test
    void equalsEqualErrorMessages() {
        ErrorMessage firstErrorMessage = new ErrorMessage(VALID_ERROR_MESSAGE);
        ErrorMessage secondErrorMessage = new ErrorMessage(VALID_ERROR_MESSAGE);
        assertThat(firstErrorMessage).isEqualTo(secondErrorMessage);
        assertThat(firstErrorMessage.hashCode()).isEqualTo(secondErrorMessage.hashCode());
    }

    /**
     * Verifies that error messages with different values are not equal.
     */
    @Test
    void equalsDifferentErrorMessages() {
        ErrorMessage firstErrorMessage = new ErrorMessage(VALID_ERROR_MESSAGE);
        ErrorMessage secondErrorMessage = new ErrorMessage(DIFFERENT_ERROR_MESSAGE);
        assertThat(firstErrorMessage).isNotEqualTo(secondErrorMessage);
    }
}
