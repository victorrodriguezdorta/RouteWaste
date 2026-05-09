package es.ull.project.domain.valueobject.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ErrorMessageTests {

    @Test
    void givenValidErrorMessage_whenInstantiating_thenSuccess() {
        ErrorMessage errorMessage = new ErrorMessage("Invalid payload");
        assertThat(errorMessage.getValue()).isEqualTo("Invalid payload");
    }

    @Test
    void givenBlankErrorMessage_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ErrorMessage(" "));
        assertThat(exception.getMessage()).isEqualTo("Error message must not be null or blank");
    }

    @Test
    void givenEqualErrorMessages_whenEquals_thenTrue() {
        ErrorMessage firstErrorMessage = new ErrorMessage("Invalid payload");
        ErrorMessage secondErrorMessage = new ErrorMessage("Invalid payload");
        assertThat(firstErrorMessage).isEqualTo(secondErrorMessage);
        assertThat(firstErrorMessage.hashCode()).isEqualTo(secondErrorMessage.hashCode());
    }

    @Test
    void givenDifferentErrorMessages_whenEquals_thenFalse() {
        ErrorMessage firstErrorMessage = new ErrorMessage("Invalid payload");
        ErrorMessage secondErrorMessage = new ErrorMessage("Missing field");
        assertThat(firstErrorMessage).isNotEqualTo(secondErrorMessage);
    }
}
