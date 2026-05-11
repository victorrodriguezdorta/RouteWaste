package es.ull.project.adapter.rest.response.dailyplan;

import java.util.List;
import java.util.UUID;

import es.ull.project.domain.entity.StopAlert;
import es.ull.project.domain.enumerate.StopType;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.route.RouteSequence;

/**
 * StopResponseBody
 *
 * Data Transfer Object representing a Stop in a daily plan.
 */
public class StopResponseBody {

    /**
     * The sequence number of this stop within the route.
     */
    public RouteSequence sequence;

    /**
     * Unique identifier of the container visited at this stop.
     */
    public UUID containerId;

    /**
     * Kind of stop (container visit or facility unload).
     */
    public StopType type;

    /**
     * Weight of waste collected at this stop (value object).
     */
    public CollectedWeightKilograms collectedKilograms;

    /**
     * Volume of waste collected at this stop (value object).
     */
    public CollectedVolumeLiters collectedLiters;

    /**
     * Distance traveled from the previous stop in meters (value object).
     */
    public Distance distanceFromPreviousMeters;

    /**
     * Cumulative distance traveled along the route up to this stop (value object).
     */
    public Distance cumulativeDistanceMeters;

    /**
     * The actual liters in the container before collection at this stop.
     * Useful for displaying the container's state to the user.
     */
    public Double containerActualLiters;

    /**
     * List of alerts generated at this stop.
     * Can include alerts like vehicle full, container overflowed, etc.
     */
    public List<StopAlert> alerts;
}
