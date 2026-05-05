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

    private final RouteSequence sequence;
    private final Container container;
    private final CollectedWeightKilograms collectedKilograms;
    private final CollectedVolumeLiters collectedLiters;
    private final Distance distanceFromPreviousMeters;
    private final Distance cumulativeDistanceMeters;

    /**
     * Creates a new Stop.
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

    public RouteSequence getSequence() {
        return sequence;
    }

    public Container getContainer() {
        return container;
    }

    public CollectedWeightKilograms getCollectedKilograms() {
        return collectedKilograms;
    }

    public CollectedVolumeLiters getCollectedLiters() {
        return collectedLiters;
    }

    public Distance getDistanceFromPreviousMeters() {
        return distanceFromPreviousMeters;
    }

    public Distance getCumulativeDistanceMeters() {
        return cumulativeDistanceMeters;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(sequence, container);
    }

    @Override
    public String toString() {
        return String.format("Stop={sequence=%s, containerId=%s, collectedKg=%s, collectedL=%s, distPrev=%s, distCum=%s}",
                sequence, container.getId(), collectedKilograms, collectedLiters, distanceFromPreviousMeters, cumulativeDistanceMeters);
    }
}
