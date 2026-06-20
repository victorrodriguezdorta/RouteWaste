package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.route.RouteSequence;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class RouteSequenceTests {

    private static final String INVALID_ROUTE_SEQUENCE_MESSAGE =
            "Sequence must be a positive integer greater than zero";

    /**
     * Verifies that a valid route sequence is stored correctly.
     */
    @Test
    void givenValidRouteSequenceWhenInstantiatingThenSuccess() {
        RouteSequence sequence = RouteSequence.of(5);
        assertThat(sequence.getValue()).isEqualTo(5);
    }

    /**
     * Verifies that non-positive route sequences are rejected.
     */
    @Test
    void givenNonPositiveRouteSequenceWhenInstantiatingThenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> RouteSequence.of(0));
        assertThat(exception.getMessage()).isEqualTo(INVALID_ROUTE_SEQUENCE_MESSAGE);
    }

    /**
     * Verifies that route sequences with equal values are equal and share the same hash code.
     */
    @Test
    void givenEqualRouteSequenceWhenEqualsThenTrue() {
        RouteSequence seq1 = RouteSequence.of(10);
        RouteSequence seq2 = RouteSequence.of(10);
        assertThat(seq1).isEqualTo(seq2);
        assertThat(seq1.hashCode()).isEqualTo(seq2.hashCode());
    }

    /**
     * Verifies that route sequences with different values are not equal.
     */
    @Test
    void givenDifferentRouteSequenceWhenEqualsThenFalse() {
        RouteSequence seq1 = RouteSequence.of(1);
        RouteSequence seq2 = RouteSequence.of(2);
        assertThat(seq1).isNotEqualTo(seq2);
    }
}
