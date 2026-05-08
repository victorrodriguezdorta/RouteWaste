package es.ull.project.domain.entity;

import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.route.RouteSequence;

import java.util.Objects;
import java.util.UUID;

/**
 * Stop
 *
 * Represents a single point visited in a route (DailyPlan).
 * It acts as a local entity / Value Object within the DailyPlan aggregate.
 */
public class Stop {

    public static final String SEQUENCE_NOT_DEFINED = "Route sequence is not defined";
    public static final String CONTAINER_NOT_DEFINED = "Container is not defined";
    public static final String COLLECTED_KILOGRAMS_NOT_DEFINED = "Collected kilograms is not defined";
    public static final String COLLECTED_LITERS_NOT_DEFINED = "Collected liters is not defined";
    public static final String DISTANCE_PREVIOUS_NOT_DEFINED = "Distance from previous is not defined";
    public static final String CUMULATIVE_DISTANCE_NOT_DEFINED = "Cumulative distance is not defined";

    /**
     * Route sequence position of this stop within the daily plan route.
     * It is a required attribute.
     */
    private final RouteSequence sequence;

    /**
     * Container visited at this stop.
     * It is a required attribute.
     */
    private final Container container;

    /**
     * Weight of waste collected at this stop in kilograms.
     * It is a required attribute.
     */
    private final CollectedWeightKilograms collectedKilograms;

    /**
     * Volume of waste collected at this stop in liters.
     * It is a required attribute.
     */
    private final CollectedVolumeLiters collectedLiters;

    /**
     * Distance traveled from the previous stop in meters.
     * It is a required attribute.
     */
    private final Distance distanceFromPreviousMeters;

    /**
     * Cumulative distance traveled along the route up to this stop in meters.
     * It is a required attribute.
     */
    private final Distance cumulativeDistanceMeters;

    /**
     * Restore constructor.
     *
     * @param sequence                   The sequence number in the route.
     * @param container                  The container visited.
     * @param collectedKilograms         The weight of waste collected.
     * @param collectedLiters            The volume of waste collected.
     * @param distanceFromPreviousMeters The distance traveled from the previous stop.
     * @param cumulativeDistanceMeters   The total distance traveled so far.
     */
    public Stop(RouteSequence sequence,
                Container container,
                CollectedWeightKilograms collectedKilograms,
                CollectedVolumeLiters collectedLiters,
                Distance distanceFromPreviousMeters,
                Distance cumulativeDistanceMeters) {
        validate(sequence, container, collectedKilograms, collectedLiters, distanceFromPreviousMeters, cumulativeDistanceMeters);
        this.sequence = sequence;
        this.container = container;
        this.collectedKilograms = collectedKilograms;
        this.collectedLiters = collectedLiters;
        this.distanceFromPreviousMeters = distanceFromPreviousMeters;
        this.cumulativeDistanceMeters = cumulativeDistanceMeters;
    }

    /**
     * Copy constructor.
     *
     * @param other the Stop instance to copy; must not be null
     */
    public Stop(Stop other) {
        if (other == null) {
            throw new IllegalArgumentException("Stop to copy must not be null");
        }
        this.sequence = other.sequence;
        this.container = other.container;
        this.collectedKilograms = other.collectedKilograms;
        this.collectedLiters = other.collectedLiters;
        this.distanceFromPreviousMeters = other.distanceFromPreviousMeters;
        this.cumulativeDistanceMeters = other.cumulativeDistanceMeters;
    }

    /**
     * Validates that all required fields for a Stop are non-null.
     *
     * @param sequence                   route sequence; must not be null
     * @param container                  visited container; must not be null
     * @param collectedKilograms         collected weight; must not be null
     * @param collectedLiters            collected volume; must not be null
     * @param distanceFromPreviousMeters distance from previous stop; must not be null
     * @param cumulativeDistanceMeters   cumulative route distance; must not be null
     */
    private void validate(RouteSequence sequence, Container container, CollectedWeightKilograms collectedKilograms,
                          CollectedVolumeLiters collectedLiters, Distance distanceFromPreviousMeters,
                          Distance cumulativeDistanceMeters) {
        if (sequence == null) {
            throw new IllegalArgumentException(SEQUENCE_NOT_DEFINED);
        }
        if (container == null) {
            throw new IllegalArgumentException(CONTAINER_NOT_DEFINED);
        }
        if (collectedKilograms == null) {
            throw new IllegalArgumentException(COLLECTED_KILOGRAMS_NOT_DEFINED);
        }
        if (collectedLiters == null) {
            throw new IllegalArgumentException(COLLECTED_LITERS_NOT_DEFINED);
        }
        if (distanceFromPreviousMeters == null) {
            throw new IllegalArgumentException(DISTANCE_PREVIOUS_NOT_DEFINED);
        }
        if (cumulativeDistanceMeters == null) {
            throw new IllegalArgumentException(CUMULATIVE_DISTANCE_NOT_DEFINED);
        }
    }

    /**
     * Returns the route sequence number of this stop.
     *
     * @return the route sequence
     */
    public RouteSequence getSequence() {
        return sequence;
    }

    /**
     * Returns the container visited at this stop.
     *
     * @return the container
     */
    public Container getContainer() {
        return container;
    }

    /**
     * Returns the weight of waste collected at this stop.
     *
     * @return collected weight in kilograms
     */
    public CollectedWeightKilograms getCollectedKilograms() {
        return collectedKilograms;
    }

    /**
     * Returns the volume of waste collected at this stop.
     *
     * @return collected volume in liters
     */
    public CollectedVolumeLiters getCollectedLiters() {
        return collectedLiters;
    }

    /**
     * Returns the distance traveled from the previous stop.
     *
     * @return distance from previous stop in meters
     */
    public Distance getDistanceFromPreviousMeters() {
        return distanceFromPreviousMeters;
    }

    /**
     * Returns the cumulative distance traveled up to and including this stop.
     *
     * @return cumulative distance in meters
     */
    public Distance getCumulativeDistanceMeters() {
        return cumulativeDistanceMeters;
    }

    /**
     * Checks equality based on sequence and container.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if both stops have the same sequence and container
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Stop other = (Stop) otherObject;
        return Objects.equals(sequence, other.sequence) &&
               Objects.equals(container, other.container);
    }

    /**
     * Returns a hash code based on the sequence and container.
     *
     * @return hash code for this instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(sequence, container);
    }

    /**
     * Returns a human-readable string representation of this stop.
     *
     * @return string containing sequence, container id, collected weight/volume and distances
     */
    @Override
    public String toString() {
        return String.format("Stop={sequence=%s, containerId=%s, collectedKg=%s, collectedL=%s, distPrev=%s, distCum=%s}",
                sequence, container.getId(), collectedKilograms, collectedLiters, distanceFromPreviousMeters, cumulativeDistanceMeters);
    }
}
