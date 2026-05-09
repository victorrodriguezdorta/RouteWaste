package es.ull.project.domain.valueobject.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AlgorithmJsonPayloadTests {

    @Test
    void givenValidPayload_whenInstantiating_thenSuccess() {
        AlgorithmJsonPayload payload = new AlgorithmJsonPayload("{\"test\":\"data\"}");
        assertThat(payload.getJson()).isEqualTo("{\"test\":\"data\"}");
    }

    @Test
    void givenNullPayload_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new AlgorithmJsonPayload(null));
        assertThat(exception.getMessage()).isEqualTo("JSON payload must not be null or blank");
    }

    @Test
    void givenBlankPayload_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new AlgorithmJsonPayload("   "));
        assertThat(exception.getMessage()).isEqualTo("JSON payload must not be null or blank");
    }

    @Test
    void givenEqualPayloads_whenEquals_thenTrue() {
        AlgorithmJsonPayload p1 = new AlgorithmJsonPayload("{}");
        AlgorithmJsonPayload p2 = new AlgorithmJsonPayload("{}");
        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    void givenDifferentPayloads_whenEquals_thenFalse() {
        AlgorithmJsonPayload p1 = new AlgorithmJsonPayload("{\"test\":\"1\"}");
        AlgorithmJsonPayload p2 = new AlgorithmJsonPayload("{\"test\":\"2\"}");
        assertThat(p1).isNotEqualTo(p2);
    }
}
