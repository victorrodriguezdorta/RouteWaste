package es.ull.project.domain.valueobject.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ErrorCodeTests {

    @Test
    void givenValidErrorCode_whenInstantiating_thenSuccess() {
        ErrorCode errorCode = new ErrorCode("ValidationError");
        assertThat(errorCode.getValue()).isEqualTo("ValidationError");
    }

    @Test
    void givenBlankErrorCode_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ErrorCode(" "));
        assertThat(exception.getMessage()).isEqualTo("Error code must not be null or blank");
    }

    @Test
    void givenEqualErrorCodes_whenEquals_thenTrue() {
        ErrorCode firstErrorCode = new ErrorCode("ValidationError");
        ErrorCode secondErrorCode = new ErrorCode("ValidationError");
        assertThat(firstErrorCode).isEqualTo(secondErrorCode);
        assertThat(firstErrorCode.hashCode()).isEqualTo(secondErrorCode.hashCode());
    }

    @Test
    void givenDifferentErrorCodes_whenEquals_thenFalse() {
        ErrorCode firstErrorCode = new ErrorCode("ValidationError");
        ErrorCode secondErrorCode = new ErrorCode("NotFound");
        assertThat(firstErrorCode).isNotEqualTo(secondErrorCode);
    }
}
