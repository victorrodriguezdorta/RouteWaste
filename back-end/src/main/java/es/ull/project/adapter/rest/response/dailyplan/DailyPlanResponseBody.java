package es.ull.project.adapter.rest.response.dailyplan;

import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.adapter.rest.response.vehicle.VehicleResponseBody;
import java.util.List;
import java.util.UUID;

/**
 * DailyPlanResponseBody
 * 
 * Data Transfer Object representing a Daily Plan.
 */
public class DailyPlanResponseBody {
    public UUID id;
    public UUID infrastructurePlanId;
    public FacilityResponseBody facility;
    public String serviceDate;
    public VehicleResponseBody vehicle;
    public double totalCollectedKilograms;
    public double totalCollectedLiters;
    public double totalDistanceMeters;
    public List<StopResponseBody> stops;
}
