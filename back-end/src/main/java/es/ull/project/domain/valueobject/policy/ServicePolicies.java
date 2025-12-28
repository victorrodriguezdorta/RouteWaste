package es.ull.project.domain.valueobject.policy;


import java.util.Objects;
import java.util.Optional;

/**
 * ServicePolicies
 *
 * Represents a group of service-related policies and constraints.
 * This value object aggregates coherent policy parameters.
 */
public final class ServicePolicies {

    private static final String ERROR_NEGATIVE_VALUE =
            "Policy values cannot be negative";

    /**
     * Maximum service distance allowed.
     * Optional attribute. Measured in kilometers.
     */
    private final Double maxServiceDistance;

    /**
     * Maximum service time allowed.
     * Optional attribute. Measured in minutes.
     */
    private final Integer maxServiceTime;

    /**
     * Maximum number of infrastructures allowed.
     * Optional attribute.
     */
    private final Integer maxInfrastructureCount;

    /**
     * Maximum emission limit.
     * Optional attribute. Measured in kilograms of CO2.
     */
    private final Double maxEmissions;

    /**
     * Creates a new ServicePolicies value object.
     *
     * @param maxServiceDistance     Maximum service distance (km).
     * @param maxServiceTime         Maximum service time (minutes).
     * @param maxInfrastructureCount Maximum number of infrastructures.
     * @param maxEmissions           Maximum emissions allowed (kg CO2).
     */
    public ServicePolicies(
            Double maxServiceDistance,
            Integer maxServiceTime,
            Integer maxInfrastructureCount,
            Double maxEmissions
    ) {
        validateNonNegative(maxServiceDistance);
        validateNonNegative(maxServiceTime);
        validateNonNegative(maxInfrastructureCount);
        validateNonNegative(maxEmissions);

        this.maxServiceDistance = maxServiceDistance;
        this.maxServiceTime = maxServiceTime;
        this.maxInfrastructureCount = maxInfrastructureCount;
        this.maxEmissions = maxEmissions;
    }

    /**
     * Validates that a numeric policy value is non-negative.
     *
     * @param value Value to validate.
     */
    private void validateNonNegative(Number value) {
        if (value != null && value.doubleValue() < 0) {
            throw new IllegalArgumentException(ERROR_NEGATIVE_VALUE);
        }
    }

    /**
     * Returns the maximum service distance, if defined.
     *
     * @return Optional maximum service distance.
     */
    public Optional<Double> getMaxServiceDistance() {
        return Optional.ofNullable(this.maxServiceDistance);
    }

    /**
     * Returns the maximum service time, if defined.
     *
     * @return Optional maximum service time.
     */
    public Optional<Integer> getMaxServiceTime() {
        return Optional.ofNullable(this.maxServiceTime);
    }

    /**
     * Returns the maximum number of infrastructures, if defined.
     *
     * @return Optional maximum infrastructure count.
     */
    public Optional<Integer> getMaxInfrastructureCount() {
        return Optional.ofNullable(this.maxInfrastructureCount);
    }

    /**
     * Returns the maximum emissions allowed, if defined.
     *
     * @return Optional emissions limit.
     */
    public Optional<Double> getMaxEmissions() {
        return Optional.ofNullable(this.maxEmissions);
    }

    /**
     * Checks whether any policy is defined.
     *
     * @return True if at least one policy is present.
     */
    public boolean hasAnyPolicy() {
        return maxServiceDistance != null
                || maxServiceTime != null
                || maxInfrastructureCount != null
                || maxEmissions != null;
    }

    /**
     * Validates whether a service assignment complies with defined policies.
     *
     * @param serviceDistance Distance of the service assignment.
     * @param serviceTime Service time of the assignment.
     * @return Optional with error message if validation fails, empty if compliant.
     */
    public Optional<String> validateServiceAssignment(double serviceDistance, int serviceTime) {
        if (maxServiceDistance != null && serviceDistance > maxServiceDistance) {
            return Optional.of(String.format(
                    "Service distance %.2f km exceeds maximum allowed %.2f km",
                    serviceDistance, maxServiceDistance
            ));
        }
        
        if (maxServiceTime != null && serviceTime > maxServiceTime) {
            return Optional.of(String.format(
                    "Service time %d minutes exceeds maximum allowed %d minutes",
                    serviceTime, maxServiceTime
            ));
        }
        
        return Optional.empty();
    }

    //iscompliantvalue
    public boolean isCompliant(
            double serviceDistance,
            int serviceTime,
            int infrastructureCount,
            double emissions
    ) {
        if (maxServiceDistance != null && serviceDistance > maxServiceDistance) {
            return false;
        }
        if (maxServiceTime != null && serviceTime > maxServiceTime) {
            return false;
        }
        if (maxInfrastructureCount != null && infrastructureCount > maxInfrastructureCount) {
            return false;
        }
        if (maxEmissions != null && emissions > maxEmissions) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        ServicePolicies other = (ServicePolicies) otherObject;
        return Objects.equals(this.maxServiceDistance, other.maxServiceDistance)
                && Objects.equals(this.maxServiceTime, other.maxServiceTime)
                && Objects.equals(this.maxInfrastructureCount, other.maxInfrastructureCount)
                && Objects.equals(this.maxEmissions, other.maxEmissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                maxServiceDistance,
                maxServiceTime,
                maxInfrastructureCount,
                maxEmissions
        );
    }

    @Override
    public String toString() {
        return String.format(
                "ServicePolicies={maxServiceDistance=%s, maxServiceTime=%s, maxInfrastructureCount=%s, maxEmissions=%s}",
                maxServiceDistance,
                maxServiceTime,
                maxInfrastructureCount,
                maxEmissions
        );
    }
}