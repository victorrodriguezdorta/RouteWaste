package es.ull.project.domain.valueobject.infrastructureplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class InfrastructurePlanFailureReasonTests {

    @Test
    void givenValidReason_whenInstantiating_thenSuccess() {
        InfrastructurePlanFailureReason reason = new InfrastructurePlanFailureReason("Docker timeout");
        assertThat(reason.getValue()).isEqualTo("Docker timeout");
    }

    @Test
    void givenBlankReason_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InfrastructurePlanFailureReason(" "));
        assertThat(exception.getMessage())
                .isEqualTo("Infrastructure plan failure reason must not be null or blank");
    }

    @Test
    void fromNullable_returnsNullForBlankInput() {
        assertThat(InfrastructurePlanFailureReason.fromNullable(null)).isNull();
        assertThat(InfrastructurePlanFailureReason.fromNullable("  ")).isNull();
    }

    @Test
    void fromNullable_returnsValueObjectForText() {
        InfrastructurePlanFailureReason reason =
                InfrastructurePlanFailureReason.fromNullable("timeout");
        assertThat(reason).isNotNull();
        assertThat(reason.getValue()).isEqualTo("timeout");
    }
}
