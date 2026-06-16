package es.ull.project.domain.valueobject.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GreedyWeightsTests {

    @Test
    void givenValidWeights_whenInstantiating_thenSuccess() {
        GreedyWeights weights = new GreedyWeights(0.25, 0.75);

        assertThat(weights.getDistanceWeight()).isEqualTo(0.25);
        assertThat(weights.getFillWeight()).isEqualTo(0.75);
    }

    @Test
    void givenDefaultWeights_whenCreating_thenUsesExpectedValues() {
        GreedyWeights weights = GreedyWeights.defaultWeights();

        assertThat(weights.getDistanceWeight()).isEqualTo(0.40);
        assertThat(weights.getFillWeight()).isEqualTo(0.60);
    }

    @Test
    void givenNegativeWeight_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new GreedyWeights(-0.1, 1.1));

        assertThat(exception.getMessage()).isEqualTo("Greedy weights must be within [0, 1]");
    }

    @Test
    void givenWeightsWithInvalidSum_whenInstantiating_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new GreedyWeights(0.2, 0.2));

        assertThat(exception.getMessage()).isEqualTo("Greedy weights must add up to 1");
    }

    @Test
    void givenEqualWeights_whenEquals_thenTrue() {
        GreedyWeights firstWeights = new GreedyWeights(0.5, 0.5);
        GreedyWeights secondWeights = new GreedyWeights(0.5, 0.5);

        assertThat(firstWeights).isEqualTo(secondWeights);
        assertThat(firstWeights.hashCode()).isEqualTo(secondWeights.hashCode());
    }

    @Test
    void givenDifferentWeights_whenEquals_thenFalse() {
        GreedyWeights firstWeights = new GreedyWeights(0.5, 0.5);
        GreedyWeights secondWeights = new GreedyWeights(0.3, 0.7);

        assertThat(firstWeights).isNotEqualTo(secondWeights);
    }
}
