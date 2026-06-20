package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.algorithm.GreedyWeights;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class GreedyWeightsTests {

    private static final String INVALID_WEIGHT_MESSAGE = "Greedy weights must be within [0, 1]";
    private static final String INVALID_SUM_MESSAGE = "Greedy weights must add up to 1";

    /**
     * Verifies that valid greedy weights are stored correctly.
     */
    @Test
    void givenValidWeightsWhenInstantiatingThenSuccess() {
        GreedyWeights weights = new GreedyWeights(0.25, 0.75);
        assertThat(weights.getDistanceWeight()).isEqualTo(0.25);
        assertThat(weights.getFillWeight()).isEqualTo(0.75);
    }

    /**
     * Verifies that default greedy weights use the expected values.
     */
    @Test
    void givenDefaultWeightsWhenCreatingThenUsesExpectedValues() {
        GreedyWeights weights = GreedyWeights.defaultWeights();
        assertThat(weights.getDistanceWeight()).isEqualTo(0.40);
        assertThat(weights.getFillWeight()).isEqualTo(0.60);
    }

    /**
     * Verifies that weights outside the accepted range are rejected.
     */
    @Test
    void givenNegativeWeightWhenInstantiatingThenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new GreedyWeights(-0.1, 1.1));
        assertThat(exception.getMessage()).isEqualTo(INVALID_WEIGHT_MESSAGE);
    }

    /**
     * Verifies that weights whose sum is not one are rejected.
     */
    @Test
    void givenWeightsWithInvalidSumWhenInstantiatingThenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new GreedyWeights(0.2, 0.2));
        assertThat(exception.getMessage()).isEqualTo(INVALID_SUM_MESSAGE);
    }

    /**
     * Verifies that greedy weights with equal values are equal and share the same hash code.
     */
    @Test
    void givenEqualWeightsWhenEqualsThenTrue() {
        GreedyWeights firstWeights = new GreedyWeights(0.5, 0.5);
        GreedyWeights secondWeights = new GreedyWeights(0.5, 0.5);
        assertThat(firstWeights).isEqualTo(secondWeights);
        assertThat(firstWeights.hashCode()).isEqualTo(secondWeights.hashCode());
    }

    /**
     * Verifies that greedy weights with different values are not equal.
     */
    @Test
    void givenDifferentWeightsWhenEqualsThenFalse() {
        GreedyWeights firstWeights = new GreedyWeights(0.5, 0.5);
        GreedyWeights secondWeights = new GreedyWeights(0.3, 0.7);
        assertThat(firstWeights).isNotEqualTo(secondWeights);
    }
}
