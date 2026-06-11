package es.ull.project.domain.entity;

import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.time.PlanDay;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain entity representing a container daily state snapshot persisted in MongoDB.
 * <p>{@link #infrastructurePlan} may be null for documents stored before parent linkage was added.</p>
 */
public class ContainerDailyState {

    public static final String CONTAINER_NOT_DEFINED = "Container is not defined";
    public static final String PLAN_DAY_REQUIRED = "plan day is required";
    public static final String PLAN_DAY_TOO_LOW = "planDay must be >= 1";
    public static final String DAILY_FILLING_REQUIRED = "daily filling liters is required";
    public static final String CONTAINER_CAPACITY_REQUIRED = "container capacity liters is required";
    public static final String CONTAINER_CAPACITY_NOT_POSITIVE = "containerCapacityLiters must be > 0";
    public static final String DAILY_DEMAND_REQUIRED = "daily demand liters per day is required";

    private static final double ZERO_LITERS = 0.0;
    private static final int MINIMUM_PLAN_DAY = 1;

    /**
     * Daily state identifier.
     * It is a computed attribute.
     */
    private final UUID id;

    /**
     * Parent infrastructure plan.
     * It is an optional attribute.
     */
    private final InfrastructurePlan infrastructurePlan;

    /**
     * Referenced container.
     * It is a required attribute.
     */
    private final Container container;

    /**
     * Planning day represented by this snapshot.
     * It is a required attribute.
     */
    private final PlanDay planDay;

    /**
     * Container filling level for the day.
     * It is a required attribute.
     */
    private final CollectedVolumeLiters dailyFillingLiters;

    /**
     * Container filling level before any collection on the day.
     * It is an optional attribute.
     */
    private final CollectedVolumeLiters dailyFillingLitersBeforeCollection;

    /**
     * Container capacity at planning time.
     * It is a required attribute.
     */
    private final ContainerCapacityLiters containerCapacityLiters;

    /**
     * Expected daily demand for the container.
     * It is a required attribute.
     */
    private final DailyWasteDemandLitersPerDay dailyDemandLitersPerDay;

    /**
     * Computed status for the container on this day.
     * It is a required attribute.
     */
    private final ContainerStatus status;

    /**
     * Creates a new container daily state snapshot.
     *
     * @param infrastructurePlan      the parent infrastructure plan
     * @param container                 the referenced container
     * @param planDay                   the planning day represented by the snapshot
     * @param dailyFillingLiters        the container filling level in liters
     * @param containerCapacityLiters   the container capacity in liters
     * @param dailyDemandLitersPerDay   the estimated daily demand in liters per day
     * @param status                    the computed container status
     */
    public ContainerDailyState(
            InfrastructurePlan infrastructurePlan,
            Container container,
            PlanDay planDay,
            CollectedVolumeLiters dailyFillingLiters,
            ContainerCapacityLiters containerCapacityLiters,
            DailyWasteDemandLitersPerDay dailyDemandLitersPerDay,
            ContainerStatus status) {
        this(
                UUID.randomUUID(),
                infrastructurePlan,
                container,
                planDay,
                dailyFillingLiters,
                containerCapacityLiters,
                dailyDemandLitersPerDay,
                status,
                null);
    }

    /**
     * Creates a new container daily state snapshot including the before-collection level.
     *
     * @param infrastructurePlan                   the parent infrastructure plan
     * @param container                              the referenced container
     * @param planDay                                the planning day represented by the snapshot
     * @param dailyFillingLiters                     the container filling level in liters
     * @param containerCapacityLiters                the container capacity in liters
     * @param dailyDemandLitersPerDay                the estimated daily demand in liters per day
     * @param status                                 the computed container status
     * @param dailyFillingLitersBeforeCollection     the filling level before collection
     */
    public ContainerDailyState(
            InfrastructurePlan infrastructurePlan,
            Container container,
            PlanDay planDay,
            CollectedVolumeLiters dailyFillingLiters,
            ContainerCapacityLiters containerCapacityLiters,
            DailyWasteDemandLitersPerDay dailyDemandLitersPerDay,
            ContainerStatus status,
            CollectedVolumeLiters dailyFillingLitersBeforeCollection) {
        this(
                UUID.randomUUID(),
                infrastructurePlan,
                container,
                planDay,
                dailyFillingLiters,
                containerCapacityLiters,
                dailyDemandLitersPerDay,
                status,
                dailyFillingLitersBeforeCollection);
    }

    /**
     * Copy constructor.
     *
     * @param otherObject container daily state to copy
     */
    public ContainerDailyState(ContainerDailyState otherObject) {
        this.id = otherObject.id;
        this.infrastructurePlan = otherObject.infrastructurePlan;
        this.container = otherObject.container;
        this.planDay = otherObject.planDay;
        this.dailyFillingLiters = otherObject.dailyFillingLiters;
        this.dailyFillingLitersBeforeCollection = otherObject.dailyFillingLitersBeforeCollection;
        this.containerCapacityLiters = otherObject.containerCapacityLiters;
        this.dailyDemandLitersPerDay = otherObject.dailyDemandLitersPerDay;
        this.status = otherObject.status;
    }

    /**
     * Restore constructor.
     *
     * @param id                        the daily state identifier
     * @param infrastructurePlan        the parent infrastructure plan
     * @param container                 the referenced container
     * @param planDay                   the planning day represented by the snapshot
     * @param dailyFillingLiters        the container filling level in liters
     * @param containerCapacityLiters   the container capacity in liters
     * @param dailyDemandLitersPerDay   the estimated daily demand in liters per day
     * @param status                    the computed container status
     */
    public ContainerDailyState(
            UUID id,
            InfrastructurePlan infrastructurePlan,
            Container container,
            PlanDay planDay,
            CollectedVolumeLiters dailyFillingLiters,
            ContainerCapacityLiters containerCapacityLiters,
            DailyWasteDemandLitersPerDay dailyDemandLitersPerDay,
            ContainerStatus status) {
        this(
                id,
                infrastructurePlan,
                container,
                planDay,
                dailyFillingLiters,
                containerCapacityLiters,
                dailyDemandLitersPerDay,
                status,
                null);
    }

    /**
     * Restore constructor including the before-collection level.
     *
     * @param id                                     the daily state identifier
     * @param infrastructurePlan                     the parent infrastructure plan
     * @param container                              the referenced container
     * @param planDay                                the planning day represented by the snapshot
     * @param dailyFillingLiters                     the container filling level in liters
     * @param containerCapacityLiters                the container capacity in liters
     * @param dailyDemandLitersPerDay                the estimated daily demand in liters per day
     * @param status                                 the computed container status
     * @param dailyFillingLitersBeforeCollection     the filling level before collection
     */
    public ContainerDailyState(
            UUID id,
            InfrastructurePlan infrastructurePlan,
            Container container,
            PlanDay planDay,
            CollectedVolumeLiters dailyFillingLiters,
            ContainerCapacityLiters containerCapacityLiters,
            DailyWasteDemandLitersPerDay dailyDemandLitersPerDay,
            ContainerStatus status,
            CollectedVolumeLiters dailyFillingLitersBeforeCollection) {
        validate(container, planDay, dailyFillingLiters, containerCapacityLiters, dailyDemandLitersPerDay);
        this.id = id;
        this.infrastructurePlan = infrastructurePlan;
        this.container = container;
        this.planDay = planDay;
        this.dailyFillingLiters = dailyFillingLiters;
        this.dailyFillingLitersBeforeCollection = dailyFillingLitersBeforeCollection;
        this.containerCapacityLiters = containerCapacityLiters;
        this.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
        this.status = status != null ? status : ContainerStatus.CORRECT;
    }

    /**
     * Validates required snapshot values.
     *
     * @param container                 the referenced container
     * @param planDay                   the planning day represented by the snapshot
     * @param dailyFillingLiters        the container filling level in liters
     * @param containerCapacityLiters   the container capacity in liters
     * @param dailyDemandLitersPerDay   the estimated daily demand in liters per day
     */
    private void validate(
            Container container,
            PlanDay planDay,
            CollectedVolumeLiters dailyFillingLiters,
            ContainerCapacityLiters containerCapacityLiters,
            DailyWasteDemandLitersPerDay dailyDemandLitersPerDay) {
        if (container == null) {
            throw new IllegalArgumentException(CONTAINER_NOT_DEFINED);
        }
        if (planDay == null) {
            throw new IllegalArgumentException(PLAN_DAY_REQUIRED);
        }
        if (planDay.getDay() < MINIMUM_PLAN_DAY) {
            throw new IllegalArgumentException(PLAN_DAY_TOO_LOW);
        }
        if (dailyFillingLiters == null) {
            throw new IllegalArgumentException(DAILY_FILLING_REQUIRED);
        }
        if (containerCapacityLiters == null) {
            throw new IllegalArgumentException(CONTAINER_CAPACITY_REQUIRED);
        }
        if (containerCapacityLiters.getLiters() <= ZERO_LITERS) {
            throw new IllegalArgumentException(CONTAINER_CAPACITY_NOT_POSITIVE);
        }
        if (dailyDemandLitersPerDay == null) {
            throw new IllegalArgumentException(DAILY_DEMAND_REQUIRED);
        }
    }

    /**
     * Gets the daily state identifier.
     *
     * @return the daily state identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the parent infrastructure plan when present.
     *
     * @return parent plan, or empty for legacy snapshots without persisted linkage
     */
    public Optional<InfrastructurePlan> getInfrastructurePlan() {
        return Optional.ofNullable(infrastructurePlan);
    }

    /**
     * Gets the parent infrastructure plan identifier.
     *
     * @return parent plan id, or empty for legacy snapshots without persisted linkage
     */
    public Optional<UUID> getInfrastructurePlanId() {
        return getInfrastructurePlan().map(InfrastructurePlan::getId);
    }

    /**
     * Returns the referenced container.
     *
     * @return the container
     */
    public Container getContainer() {
        return container;
    }

    /**
     * Gets the container identifier.
     *
     * @return the container identifier
     */
    public UUID getContainerId() {
        return container.getId();
    }

    /**
     * Gets the planning day represented by this state.
     *
     * @return the planning day
     */
    public int getPlanDay() {
        return planDay.getDay();
    }

    /**
     * Gets the container filling level in liters.
     *
     * @return the filling level in liters
     */
    public double getDailyFillingLiters() {
        return dailyFillingLiters.getValue();
    }

    /**
     * Gets the container filling level before any collection on the day.
     *
     * @return optional filling level in liters before collection
     */
    public Optional<Double> getDailyFillingLitersBeforeCollection() {
        return dailyFillingLitersBeforeCollection != null
                ? Optional.of(dailyFillingLitersBeforeCollection.getValue())
                : Optional.empty();
    }

    /**
     * Gets the container capacity in liters.
     *
     * @return the container capacity in liters
     */
    public double getContainerCapacityLiters() {
        return containerCapacityLiters.getLiters();
    }

    /**
     * Gets the estimated daily demand in liters per day.
     *
     * @return the estimated daily demand in liters per day
     */
    public double getDailyDemandLitersPerDay() {
        return dailyDemandLitersPerDay.getLitersPerDay();
    }

    /**
     * Gets the computed status for the container on this day.
     *
     * @return the computed container status
     */
    public ContainerStatus getStatus() {
        return status;
    }

    /**
     * Compares daily state snapshots by identifier.
     *
     * @param otherObject the object to compare
     * @return true when both snapshots have the same identifier
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        ContainerDailyState that = (ContainerDailyState) otherObject;
        return Objects.equals(id, that.id);
    }

    /**
     * Calculates the hash code from the daily state identifier.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Formats this container daily state as text.
     *
     * @return the textual representation of this state
     */
    @Override
    public String toString() {
        return String.format(
                "ContainerDailyState{id=%s,infrastructurePlanId=%s,containerId=%s,planDay=%d,dailyFilling=%.2f,capacity=%.2f,dailyDemand=%.2f,status=%s}",
                id,
                getInfrastructurePlanId().map(UUID::toString).orElse(null),
                container.getId(),
                getPlanDay(),
                getDailyFillingLiters(),
                getContainerCapacityLiters(),
                getDailyDemandLitersPerDay(),
                status);
    }
}
