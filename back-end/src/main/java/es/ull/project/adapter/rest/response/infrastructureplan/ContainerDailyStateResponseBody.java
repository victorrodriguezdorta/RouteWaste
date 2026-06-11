package es.ull.project.adapter.rest.response.infrastructureplan;

import es.ull.project.domain.enumerate.ContainerStatus;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.name.Name;
import es.ull.project.domain.valueobject.time.PlanDay;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO for a single container daily state entry in infrastructure plan responses.
 */
public class ContainerDailyStateResponseBody {

    public UUID id;
    public UUID containerId;
    /**
     * Human-readable container name (denormalized for clients).
     */
    public Name containerName;
    public PlanDay planDay;
    /**
     * Time of day represented by this snapshot (null for plain daily snapshots).
     */
    public LocalTime time;
    public CollectedVolumeLiters dailyFillingLiters;
    public CollectedVolumeLiters dailyFillingLitersBeforeCollection;
    public ContainerCapacityLiters containerCapacityLiters;
    public DailyWasteDemandLitersPerDay dailyDemandLitersPerDay;
    public ContainerStatus status;
}
