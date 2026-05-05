package es.ull.project.adapter.rest.response.dailyplan;

import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import java.util.UUID;

/**
 * StopResponseBody
 * 
 * Data Transfer Object representing a Stop in a daily plan.
 */
public class StopResponseBody {
    public int sequence;
    public ContainerResponseBody container;
    public double collectedKilograms;
    public double collectedLiters;
    public double distanceFromPreviousMeters;
    public double cumulativeDistanceMeters;
}
