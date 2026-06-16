package es.ull.project.domain.valueobject.algorithm;

import java.util.Objects;

/**
 * GreedyWeights
 *
 * Represents the weights applied to the greedy container selection score used
 * by the algorithm: the {@code distanceWeight} penalises far away containers
 * while the {@code fillWeight} rewards containers with a higher fill percentage.
 * Both weights must be within {@code [0, 1]} and add up to {@code 1}.
 * It is an optional attribute.
 */
public final class GreedyWeights {

    private static final String ERROR_WEIGHT_NOT_VALID = "Greedy weights must be within [0, 1]";
    private static final String ERROR_SUM_NOT_VALID = "Greedy weights must add up to 1";
    private static final double MIN_WEIGHT = 0.0;
    private static final double MAX_WEIGHT = 1.0;
    private static final double EXPECTED_SUM = 1.0;
    private static final double SUM_TOLERANCE = 0.000001;
    private static final double DEFAULT_DISTANCE_WEIGHT = 0.40;
    private static final double DEFAULT_FILL_WEIGHT = 0.60;
    private static final int DOUBLE_COMPARE_EQUAL = 0;

    /**
     * Weight applied to the normalized distance term.
     * It is a required attribute.
     */
    private final double distanceWeight;

    /**
     * Weight applied to the normalized fill percentage term.
     * It is a required attribute.
     */
    private final double fillWeight;

    /**
     * Creates a new GreedyWeights.
     *
     * @param distanceWeight weight applied to the normalized distance term
     * @param fillWeight     weight applied to the normalized fill percentage term
     * @throws IllegalArgumentException if a weight is out of range or they do not add up to 1
     */
    public GreedyWeights(double distanceWeight, double fillWeight) {
        validateWeight(distanceWeight);
        validateWeight(fillWeight);
        validateSum(distanceWeight, fillWeight);
        this.distanceWeight = distanceWeight;
        this.fillWeight = fillWeight;
    }

    /**
     * Returns the default greedy weights used when the request does not provide them.
     *
     * @return greedy weights with the historical default distribution
     */
    public static GreedyWeights defaultWeights() {
        return new GreedyWeights(DEFAULT_DISTANCE_WEIGHT, DEFAULT_FILL_WEIGHT);
    }

    /**
     * Returns the weight applied to the distance term.
     *
     * @return distance weight
     */
    public double getDistanceWeight() {
        return distanceWeight;
    }

    /**
     * Returns the weight applied to the fill percentage term.
     *
     * @return fill weight
     */
    public double getFillWeight() {
        return fillWeight;
    }

    /**
     * Validates that a single weight is within the allowed range.
     *
     * @param weight weight to validate
     */
    private void validateWeight(double weight) {
        if (Double.isNaN(weight) || weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
            throw new IllegalArgumentException(ERROR_WEIGHT_NOT_VALID);
        }
    }

    /**
     * Validates that both weights add up to one within a small tolerance.
     *
     * @param distanceWeight distance weight to validate
     * @param fillWeight     fill weight to validate
     */
    private void validateSum(double distanceWeight, double fillWeight) {
        if (Math.abs((distanceWeight + fillWeight) - EXPECTED_SUM) > SUM_TOLERANCE) {
            throw new IllegalArgumentException(ERROR_SUM_NOT_VALID);
        }
    }

    /**
     * Checks equality based on both weights.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if the other object has the same weights
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        GreedyWeights other = (GreedyWeights) otherObject;
        return Double.compare(distanceWeight, other.distanceWeight) == DOUBLE_COMPARE_EQUAL
                && Double.compare(fillWeight, other.fillWeight) == DOUBLE_COMPARE_EQUAL;
    }

    /**
     * Returns a hash code based on both weights.
     *
     * @return hash code for this instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(distanceWeight, fillWeight);
    }

    /**
     * Returns a string representation of this value object.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "GreedyWeights={distanceWeight=" + distanceWeight + ", fillWeight=" + fillWeight + "}";
    }
}
