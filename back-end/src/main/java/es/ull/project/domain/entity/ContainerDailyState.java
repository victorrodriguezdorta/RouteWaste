package es.ull.project.domain.entity;

import java.util.Objects;
import java.util.UUID;

import es.ull.project.domain.enumerate.ContainerStatus;

/**
 * Domain entity representing a container daily state snapshot persisted in MongoDB.
 * <p>{@link #infrastructurePlanId} may be null for documents stored before parent linkage was added.</p>
 */
public class ContainerDailyState {

    private final UUID id;
    /**
     * Parent infrastructure plan; null only for legacy persisted rows without this field.
     */
    private final UUID infrastructurePlanId;
    private final String containerId;
    private final int planDay;
    private final double dailyFillingLiters;
    private final double containerCapacityLiters;
    private final double dailyDemandLitersPerDay;
    private final ContainerStatus status;

    public ContainerDailyState(UUID id,
            UUID infrastructurePlanId,
            String containerId,
            int planDay,
            double dailyFillingLiters,
            double containerCapacityLiters,
            double dailyDemandLitersPerDay,
            ContainerStatus status) {
        if (id == null) throw new IllegalArgumentException("id is required");
        this.infrastructurePlanId = infrastructurePlanId;
        if (containerId == null || containerId.isBlank()) throw new IllegalArgumentException("containerId is required");
        if (planDay < 1) throw new IllegalArgumentException("planDay must be >= 1");
        if (dailyFillingLiters < 0.0) throw new IllegalArgumentException("dailyFillingLiters must be >= 0");
        if (containerCapacityLiters <= 0.0) throw new IllegalArgumentException("containerCapacityLiters must be > 0");
        if (dailyDemandLitersPerDay < 0.0) throw new IllegalArgumentException("dailyDemandLitersPerDay must be >= 0");
        this.id = id;
        this.containerId = containerId;
        this.planDay = planDay;
        this.dailyFillingLiters = dailyFillingLiters;
        this.containerCapacityLiters = containerCapacityLiters;
        this.dailyDemandLitersPerDay = dailyDemandLitersPerDay;
        this.status = status != null ? status : ContainerStatus.CORRECT;
    }

    public UUID getId() { return id; }

    /**
     * @return parent plan id, or null for legacy snapshots without persisted linkage
     */
    public UUID getInfrastructurePlanId() {
        return infrastructurePlanId;
    }

    public String getContainerId() { return containerId; }

    public int getPlanDay() { return planDay; }

    public double getDailyFillingLiters() { return dailyFillingLiters; }

    public double getContainerCapacityLiters() { return containerCapacityLiters; }

    public double getDailyDemandLitersPerDay() { return dailyDemandLitersPerDay; }

    public ContainerStatus getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainerDailyState that = (ContainerDailyState) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ContainerDailyState{id=%s,infrastructurePlanId=%s,containerId=%s,planDay=%d,dailyFilling=%.2f,capacity=%.2f,dailyDemand=%.2f,status=%s}",
            id, infrastructurePlanId, containerId, planDay, dailyFillingLiters, containerCapacityLiters, dailyDemandLitersPerDay, status);
    }
}
