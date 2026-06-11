package com.ull.domain.valueobject.algorithm;

import java.util.Objects;

/**
 * GreedyWeights value object used by the algorithm module.
 *
 * <p>Holds the weights applied to the greedy container selection score: the
 * {@code distanceWeight} penalises far away containers while the
 * {@code fillWeight} rewards containers with a higher fill percentage. Both
 * weights must be within {@code [0, 1]} and add up to {@code 1}.
 */
public final class GreedyWeights {

  public static final String WEIGHT_NOT_VALID = "Greedy weights must be within [0, 1]";
  public static final String WEIGHTS_SUM_NOT_VALID = "Greedy weights must add up to 1";

  private static final double MIN_WEIGHT = 0.0;
  private static final double MAX_WEIGHT = 1.0;
  private static final double EXPECTED_SUM = 1.0;
  private static final double SUM_TOLERANCE = 0.000001;
  private static final double DEFAULT_DISTANCE_WEIGHT = 0.40;
  private static final double DEFAULT_FILL_WEIGHT = 0.60;

  private final double distanceWeight;
  private final double fillWeight;

  /**
   * Creates a greedy weights value object.
   *
   * @param distanceWeight weight applied to the normalized distance term
   * @param fillWeight     weight applied to the normalized fill percentage term
   */
  public GreedyWeights(double distanceWeight, double fillWeight) {
    validateWeight(distanceWeight);
    validateWeight(fillWeight);
    validateSum(distanceWeight, fillWeight);
    this.distanceWeight = distanceWeight;
    this.fillWeight = fillWeight;
  }

  /**
   * Returns the default greedy weights used when the problem does not provide them.
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
    return this.distanceWeight;
  }

  /**
   * Returns the weight applied to the fill percentage term.
   *
   * @return fill weight
   */
  public double getFillWeight() {
    return this.fillWeight;
  }

  /**
   * Validates that a single weight is within the allowed range.
   *
   * @param weight weight to validate
   */
  private void validateWeight(double weight) {
    if (Double.isNaN(weight) || weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
      throw new IllegalArgumentException(WEIGHT_NOT_VALID);
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
      throw new IllegalArgumentException(WEIGHTS_SUM_NOT_VALID);
    }
  }

  /**
   * Compares this value object with another by both weights.
   *
   * @param otherObject object to compare
   * @return true when both weights match
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
    return Double.compare(this.distanceWeight, other.distanceWeight) == 0
        && Double.compare(this.fillWeight, other.fillWeight) == 0;
  }

  /**
   * Returns a hash code based on both weights.
   *
   * @return hash code for this value object
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.distanceWeight, this.fillWeight);
  }

  /**
   * Returns a readable representation of these weights.
   *
   * @return text containing both weights
   */
  @Override
  public String toString() {
    return String.format("GreedyWeights{distanceWeight=%s, fillWeight=%s}", this.distanceWeight, this.fillWeight);
  }
}
