package es.ull.project.adapter.rest.response.dailyplan;

import es.ull.project.adapter.rest.response.vehicle.VehicleResponseBody;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.time.ServiceDate;
import java.util.List;
import java.util.UUID;

/**
 * DailyPlanResponseBody
 *
 * Data Transfer Object representing a Daily Plan.
 */
public class DailyPlanResponseBody {

    /**
     * Unique identifier of the daily plan.
     */
    public UUID id;

    /**
     * Unique identifier of the parent infrastructure plan.
     */
    public UUID infrastructurePlanId;

    /**
     * Unique identifier of the facility associated with this daily plan.
     */
    public UUID facilityId;

    /**
     * Human-readable facility name (denormalized for clients).
     */
    public String facilityName;

    /**
     * The calendar date on which this daily plan is scheduled.
     */
    public ServiceDate serviceDate;

    /**
     * The day number within the planning horizon.
     */
    public Integer planDay;

    /**
     * Vehicle assigned to this daily plan (full snapshot for analysis in clients).
     */
    public VehicleResponseBody vehicle;

    /**
     * Total weight of waste collected across all stops (value object).
     */
    public CollectedWeightKilograms totalCollectedKilograms;

    /**
     * Total volume of waste collected across all stops (value object).
     */
    public CollectedVolumeLiters totalCollectedLiters;

    /**
     * Total distance traveled across all stops in meters (value object).
     */
    public Distance totalDistanceMeters;

    /**
     * Ordered list of stops in this daily plan.
     */
    public List<StopResponseBody> stops;
}
