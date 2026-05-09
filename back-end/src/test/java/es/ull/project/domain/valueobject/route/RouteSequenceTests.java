package es.ull.project.domain.valueobject.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RouteSequenceTests {

    @Test
    void givenValidRouteSequence_whenInstantiating_thenSuccess() {
        RouteSequence sequence = RouteSequence.of(5);
        assertThat(sequence.getValue()).isEqualTo(5);
    }

    @Test
    void givenNonPositiveRouteSequence_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> RouteSequence.of(0));
        assertThat(exception.getMessage()).isEqualTo("Sequence must be a positive integer greater than zero");
    }

    @Test
    void givenEqualRouteSequence_whenEquals_thenTrue() {
        RouteSequence seq1 = RouteSequence.of(10);
        RouteSequence seq2 = RouteSequence.of(10);
        assertThat(seq1).isEqualTo(seq2);
        assertThat(seq1.hashCode()).isEqualTo(seq2.hashCode());
    }

    @Test
    void givenDifferentRouteSequence_whenEquals_thenFalse() {
        RouteSequence seq1 = RouteSequence.of(1);
        RouteSequence seq2 = RouteSequence.of(2);
        assertThat(seq1).isNotEqualTo(seq2);
    }
}
