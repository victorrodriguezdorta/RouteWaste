package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class AlgorithmJsonPayloadTests {

    private static final String BLANK_JSON_PAYLOAD = "   ";
    private static final String EMPTY_JSON_PAYLOAD = "{}";
    private static final String FIRST_DIFFERENT_JSON_PAYLOAD = "{\"test\":\"1\"}";
    private static final String JSON_PAYLOAD_NOT_DEFINED_MESSAGE = "Algorithm JSON payload must not be null or blank";
    private static final String SECOND_DIFFERENT_JSON_PAYLOAD = "{\"test\":\"2\"}";
    private static final String VALID_JSON_PAYLOAD = "{\"test\":\"data\"}";

    /**
     * Verifies that a valid algorithm JSON payload is stored correctly.
     */
    @Test
    void givenValidPayloadWhenInstantiatingThenSuccess() {
        AlgorithmJsonPayload payload = new AlgorithmJsonPayload(VALID_JSON_PAYLOAD);
        assertThat(payload.getJson()).isEqualTo(VALID_JSON_PAYLOAD);
    }

    /**
     * Verifies that null algorithm JSON payloads are rejected.
     */
    @Test
    void givenNullPayloadWhenInstantiatingThenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new AlgorithmJsonPayload(null));
        assertThat(exception.getMessage()).isEqualTo(JSON_PAYLOAD_NOT_DEFINED_MESSAGE);
    }

    /**
     * Verifies that blank algorithm JSON payloads are rejected.
     */
    @Test
    void givenBlankPayloadWhenInstantiatingThenThrowsException() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AlgorithmJsonPayload(BLANK_JSON_PAYLOAD)
        );
        assertThat(exception.getMessage()).isEqualTo(JSON_PAYLOAD_NOT_DEFINED_MESSAGE);
    }

    /**
     * Verifies that algorithm JSON payloads with equal values are equal and share the same hash code.
     */
    @Test
    void givenEqualPayloadsWhenEqualsThenTrue() {
        AlgorithmJsonPayload p1 = new AlgorithmJsonPayload(EMPTY_JSON_PAYLOAD);
        AlgorithmJsonPayload p2 = new AlgorithmJsonPayload(EMPTY_JSON_PAYLOAD);
        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    /**
     * Verifies that algorithm JSON payloads with different values are not equal.
     */
    @Test
    void givenDifferentPayloadsWhenEqualsThenFalse() {
        AlgorithmJsonPayload p1 = new AlgorithmJsonPayload(FIRST_DIFFERENT_JSON_PAYLOAD);
        AlgorithmJsonPayload p2 = new AlgorithmJsonPayload(SECOND_DIFFERENT_JSON_PAYLOAD);
        assertThat(p1).isNotEqualTo(p2);
    }
}
