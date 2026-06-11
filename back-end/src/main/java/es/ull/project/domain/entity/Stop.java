package es.ull.project.domain.entity;

import es.ull.project.domain.enumerate.StopType;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.route.RouteSequence;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Stop
 *
 * Represents a single point visited in a route (DailyPlan).
 * It acts as a local entity / Value Object within the DailyPlan aggregate.
 */
public class Stop {

    public static final String SEQUENCE_NOT_DEFINED = "Route sequence is not defined";
    public static final String STOP_TYPE_NOT_DEFINED = "Stop type is not defined";
    public static final String CONTAINER_NOT_DEFINED = "Container is not defined";
    public static final String COLLECTED_KILOGRAMS_NOT_DEFINED = "Collected kilograms is not defined";
    public static final String COLLECTED_LITERS_NOT_DEFINED = "Collected liters is not defined";
    public static final String DISTANCE_PREVIOUS_NOT_DEFINED = "Distance from previous is not defined";
    public static final String CUMULATIVE_DISTANCE_NOT_DEFINED = "Cumulative distance is not defined";
    public static final String STOP_TO_COPY_NULL = "Stop to copy must not be null";
    public static final String ID_NOT_DEFINED = "Stop id is not defined";

    /**
     * Unique identifier for the stop.
     * It is a computed attribute.
     */
    private final UUID id;

    /**
     * Route sequence position of this stop within the daily plan route.
     * It is a required attribute.
     */
    private final RouteSequence sequence;

    /**
     * Kind of stop represented by this entity.
     * It is a required attribute.
     */
    private final StopType type;

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
     * The actual liters in the container before collection at this stop.
     * Useful for displaying the container's state to the user.
     * It is an optional attribute.
     */
    private final CollectedVolumeLiters containerActualLiters;

    /**
     * Time of day at which the vehicle performs this stop.
     * It is an optional attribute.
     */
    private final LocalTime collectedAt;

    /**
     * List of alerts generated at this stop.
     * Can include alerts like vehicle full, container overflowed, etc.
     * It is an optional attribute.
     */
    private final List<StopAlert> alerts;

    /**
     * Creates a new Stop.
     *
     * @param sequence                   The sequence number in the route.
     * @param container                  The container visited.
     * @param collectedKilograms         The weight of waste collected.
     * @param collectedLiters            The volume of waste collected.
     * @param distanceFromPreviousMeters The distance traveled from the previous stop.
     * @param cumulativeDistanceMeters   The total distance traveled so far.
     * @param containerActualLiters      The actual liters before collection.
     * @param alerts                     List of alerts.
     */
    public Stop(RouteSequence sequence,
            Container container,
            CollectedWeightKilograms collectedKilograms,
            CollectedVolumeLiters collectedLiters,
            Distance distanceFromPreviousMeters,
            Distance cumulativeDistanceMeters,
            CollectedVolumeLiters containerActualLiters,
            List<StopAlert> alerts) {
        this(sequence, StopType.CONTAINER, container, collectedKilograms, collectedLiters, distanceFromPreviousMeters, cumulativeDistanceMeters, containerActualLiters, alerts);
    }

    /**
     * Creates a new Stop.
     *
     * @param sequence                   The sequence number in the route.
     * @param type                       The kind of stop.
     * @param container                  The container visited.
     * @param collectedKilograms         The weight of waste collected.
     * @param collectedLiters            The volume of waste collected.
     * @param distanceFromPreviousMeters The distance traveled from the previous stop.
     * @param cumulativeDistanceMeters   The total distance traveled so far.
     * @param containerActualLiters      The actual liters before collection.
     * @param alerts                     List of alerts.
     */
    public Stop(RouteSequence sequence,
            StopType type,
            Container container,
            CollectedWeightKilograms collectedKilograms,
            CollectedVolumeLiters collectedLiters,
            Distance distanceFromPreviousMeters,
            Distance cumulativeDistanceMeters,
            CollectedVolumeLiters containerActualLiters,
            List<StopAlert> alerts) {
        this(UUID.randomUUID(), sequence, type, container, collectedKilograms, collectedLiters,
                distanceFromPreviousMeters, cumulativeDistanceMeters, containerActualLiters, alerts);
    }

    /**
     * Restore constructor.
     * Restores a stop with its persisted identifier.
     *
     * @param id                          The persisted stop identifier.
     * @param sequence                    The sequence number in the route.
     * @param type                        The kind of stop.
     * @param container                   The container visited.
     * @param collectedKilograms          The weight of waste collected.
     * @param collectedLiters             The volume of waste collected.
     * @param distanceFromPreviousMeters  The distance traveled from the previous stop.
     * @param cumulativeDistanceMeters    The total distance traveled so far.
     * @param containerActualLiters       The actual liters before collection.
     * @param alerts                      List of alerts.
     */
    public Stop(UUID id,
            RouteSequence sequence,
            StopType type,
            Container container,
            CollectedWeightKilograms collectedKilograms,
            CollectedVolumeLiters collectedLiters,
            Distance distanceFromPreviousMeters,
            Distance cumulativeDistanceMeters,
            CollectedVolumeLiters containerActualLiters,
            List<StopAlert> alerts) {
        this(id, sequence, type, container, collectedKilograms, collectedLiters,
                distanceFromPreviousMeters, cumulativeDistanceMeters, containerActualLiters, alerts, null);
    }

    /**
     * Restore constructor including the time of the visit.
     *
     * @param id                          The persisted stop identifier.
     * @param sequence                    The sequence number in the route.
     * @param type                        The kind of stop.
     * @param container                   The container visited.
     * @param collectedKilograms          The weight of waste collected.
     * @param collectedLiters             The volume of waste collected.
     * @param distanceFromPreviousMeters  The distance traveled from the previous stop.
     * @param cumulativeDistanceMeters    The total distance traveled so far.
     * @param containerActualLiters       The actual liters before collection.
     * @param alerts                      List of alerts.
     * @param collectedAt                 Time of day at which the stop is performed.
     */
    public Stop(UUID id,
            RouteSequence sequence,
            StopType type,
            Container container,
            CollectedWeightKilograms collectedKilograms,
            CollectedVolumeLiters collectedLiters,
            Distance distanceFromPreviousMeters,
            Distance cumulativeDistanceMeters,
            CollectedVolumeLiters containerActualLiters,
            List<StopAlert> alerts,
            LocalTime collectedAt) {
        validateId(id);
        validate(sequence, type, container, collectedKilograms, collectedLiters, distanceFromPreviousMeters, cumulativeDistanceMeters);
        this.id = id;
        this.sequence = sequence;
        this.type = type;
        this.container = container;
        this.collectedKilograms = collectedKilograms;
        this.collectedLiters = collectedLiters;
        this.distanceFromPreviousMeters = distanceFromPreviousMeters;
        this.cumulativeDistanceMeters = cumulativeDistanceMeters;
        this.containerActualLiters = containerActualLiters != null ? containerActualLiters : CollectedVolumeLiters.fromLiters(0.0);
        this.collectedAt = collectedAt;
        this.alerts = alerts != null ? new ArrayList<>(alerts) : new ArrayList<>();
    }

    /**
     * Creates a new Stop including collection metadata and the time of the visit.
     *
     * @param sequence                   The sequence number in the route.
     * @param type                       The kind of stop.
     * @param container                  The container visited.
     * @param collectedKilograms         The weight of waste collected.
     * @param collectedLiters            The volume of waste collected.
     * @param distanceFromPreviousMeters The distance traveled from the previous stop.
     * @param cumulativeDistanceMeters   The total distance traveled so far.
     * @param containerActualLiters      The actual liters before collection.
     * @param alerts                     List of alerts.
     * @param collectedAt                Time of day at which the stop is performed.
     */
    public Stop(RouteSequence sequence,
            StopType type,
            Container container,
            CollectedWeightKilograms collectedKilograms,
            CollectedVolumeLiters collectedLiters,
            Distance distanceFromPreviousMeters,
            Distance cumulativeDistanceMeters,
            CollectedVolumeLiters containerActualLiters,
            List<StopAlert> alerts,
            LocalTime collectedAt) {
        this(UUID.randomUUID(), sequence, type, container, collectedKilograms, collectedLiters,
                distanceFromPreviousMeters, cumulativeDistanceMeters, containerActualLiters, alerts, collectedAt);
    }

    /**
     * Creates a new Stop with an explicit type and default collection metadata.
     *
     * @param sequence                   The sequence number in the route.
     * @param type                       The kind of stop.
     * @param container                  The container visited.
     * @param collectedKilograms         The weight of waste collected.
     * @param collectedLiters            The volume of waste collected.
     * @param distanceFromPreviousMeters The distance traveled from the previous stop.
     * @param cumulativeDistanceMeters   The total distance traveled so far.
     */
    public Stop(RouteSequence sequence,
                StopType type,
                Container container,
                CollectedWeightKilograms collectedKilograms,
                CollectedVolumeLiters collectedLiters,
                Distance distanceFromPreviousMeters,
                Distance cumulativeDistanceMeters) {
        this(sequence, type, container, collectedKilograms, collectedLiters, distanceFromPreviousMeters, cumulativeDistanceMeters, CollectedVolumeLiters.fromLiters(0.0), new ArrayList<>());
    }

    /**
     * Legacy constructor without containerActualLiters and alerts.
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
        this(sequence, StopType.CONTAINER, container, collectedKilograms, collectedLiters, distanceFromPreviousMeters, cumulativeDistanceMeters, CollectedVolumeLiters.fromLiters(0.0), new ArrayList<>());
    }

    /**
     * Copy constructor.
     *
     * @param otherObject the Stop instance to copy; must not be null
     */
    public Stop(Stop otherObject) {
        if (otherObject == null) {
            throw new IllegalArgumentException(STOP_TO_COPY_NULL);
        }
        this.id = otherObject.id;
        this.sequence = otherObject.sequence;
        this.type = otherObject.type;
        this.container = otherObject.container;
        this.collectedKilograms = otherObject.collectedKilograms;
        this.collectedLiters = otherObject.collectedLiters;
        this.distanceFromPreviousMeters = otherObject.distanceFromPreviousMeters;
        this.cumulativeDistanceMeters = otherObject.cumulativeDistanceMeters;
        this.containerActualLiters = otherObject.containerActualLiters;
        this.collectedAt = otherObject.collectedAt;
        this.alerts = new ArrayList<>(otherObject.alerts);
    }

    /**
     * Returns the unique identifier of this stop.
     *
     * @return the stop UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Validates that the stop identifier is defined.
     *
     * @param id stop identifier to validate
     */
    private void validateId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(ID_NOT_DEFINED);
        }
    }

    /**
     * Validates that all required fields for a Stop are non-null.
     *
     * @param sequence                   route sequence; must not be null
     * @param type                       stop type; must not be null
     * @param container                  visited container; must not be null
     * @param collectedKilograms         collected weight; must not be null
     * @param collectedLiters            collected volume; must not be null
     * @param distanceFromPreviousMeters distance from previous stop; must not be null
     * @param cumulativeDistanceMeters   cumulative route distance; must not be null
     */
    private void validate(RouteSequence sequence, StopType type, Container container, CollectedWeightKilograms collectedKilograms,
                          CollectedVolumeLiters collectedLiters, Distance distanceFromPreviousMeters,
                          Distance cumulativeDistanceMeters) {
        if (sequence == null) {
            throw new IllegalArgumentException(SEQUENCE_NOT_DEFINED);
        }
        if (type == null) {
            throw new IllegalArgumentException(STOP_TYPE_NOT_DEFINED);
        }
        if (type == StopType.CONTAINER && container == null) {
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
     * Returns the type of stop.
     *
     * @return the stop type
     */
    public StopType getType() {
        return type;
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
     * Returns the actual liters in the container before collection at this stop.
     *
     * @return container actual liters
     */
    public Optional<CollectedVolumeLiters> getContainerActualLiters() {
        return Optional.ofNullable(containerActualLiters);
    }

    /**
     * Returns the time of day at which the vehicle performs this stop.
     *
     * @return optional collection time
     */
    public Optional<LocalTime> getCollectedAt() {
        return Optional.ofNullable(collectedAt);
    }

    /**
     * Returns the alerts generated at this stop.
     *
     * @return unmodifiable list of alerts
     */
    public Optional<List<StopAlert>> getAlerts() {
        return Optional.of(Collections.unmodifiableList(alerts));
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
             Objects.equals(type, other.type) &&
               Objects.equals(container, other.container);
    }

    /**
     * Returns a hash code based on the sequence and container.
     *
     * @return hash code for this instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(sequence, type, container);
    }

    /**
     * Returns a human-readable string representation of this stop.
     *
     * @return string containing sequence, container id, collected weight/volume and distances
     */
    @Override
    public String toString() {
        String containerId = container != null ? container.getId().toString() : null;
        return String.format("Stop={sequence=%s, type=%s, containerId=%s, collectedKg=%s, collectedL=%s, distPrev=%s, distCum=%s, containerActualLiters=%s, alerts=%s}",
            sequence, type, containerId, collectedKilograms, collectedLiters, distanceFromPreviousMeters, cumulativeDistanceMeters, containerActualLiters, alerts);
    }
}
