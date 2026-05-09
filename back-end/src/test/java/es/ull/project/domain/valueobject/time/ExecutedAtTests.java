package es.ull.project.domain.valueobject.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ExecutedAtTests {

    @Test
    void givenValidExecutedAt_whenInstantiating_thenSuccess() {
        String isoTime = "2026-05-07T10:00:00Z";
        ExecutedAt executedAt = new ExecutedAt(isoTime);
        assertThat(executedAt.getTimestamp()).isEqualTo(isoTime);
    }

    @Test
    void givenNullExecutedAt_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ExecutedAt(null));
        assertThat(exception.getMessage()).isEqualTo("Execution time must not be null");
    }

    @Test
    void givenBlankExecutedAt_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ExecutedAt("  "));
        assertThat(exception.getMessage()).isEqualTo("Execution time must be a valid ISO-8601 format");
    }

    @Test
    void givenInvalidFormatExecutedAt_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ExecutedAt("invalid-date-format"));
        assertThat(exception.getMessage()).isEqualTo("Execution time must be a valid ISO-8601 format");
    }

    @Test
    void givenEqualExecutedAt_whenEquals_thenTrue() {
        String isoTime = "2026-05-07T10:00:00Z";
        ExecutedAt ea1 = new ExecutedAt(isoTime);
        ExecutedAt ea2 = new ExecutedAt(isoTime);
        assertThat(ea1).isEqualTo(ea2);
        assertThat(ea1.hashCode()).isEqualTo(ea2.hashCode());
    }

    @Test
    void givenDifferentExecutedAt_whenEquals_thenFalse() {
        ExecutedAt ea1 = new ExecutedAt("2026-05-07T10:00:00Z");
        ExecutedAt ea2 = new ExecutedAt("2026-05-07T11:00:00Z");
        assertThat(ea1).isNotEqualTo(ea2);
    }
}
