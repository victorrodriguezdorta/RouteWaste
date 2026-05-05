package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.dailyplan.DailyPlanResponseBody;
import es.ull.project.adapter.rest.response.dailyplan.StopResponseBody;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Stop;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for DailyPlan related response DTOs.
 */
public class DailyPlanResponseMapper {

    /**
     * Maps a DailyPlan domain entity to its response DTO.
     *
     * @param entity the DailyPlan entity
     * @return the corresponding response DTO
     */
    public static DailyPlanResponseBody toResponseBody(DailyPlan entity) {
        if (entity == null) {
            return null;
        }

        DailyPlanResponseBody response = new DailyPlanResponseBody();
        response.id = entity.getId();
        response.infrastructurePlanId = entity.getInfrastructurePlan().getId();
        response.facility = FacilityResponseMapper.toResponseBody(entity.getFacility());
        response.serviceDate = entity.getServiceDate().toString();
        response.vehicle = VehicleResponseMapper.toResponseBody(entity.getVehicle());
        response.totalCollectedKilograms = entity.getTotalCollectedKilograms().getValue();
        response.totalCollectedLiters = entity.getTotalCollectedLiters().getValue();
        response.totalDistanceMeters = entity.getTotalDistanceMeters().getValue();

        if (entity.getStops() != null) {
            response.stops = entity.getStops().stream()
                    .map(DailyPlanResponseMapper::toStopResponseBody)
                    .collect(Collectors.toList());
        }

        return response;
    }

    private static StopResponseBody toStopResponseBody(Stop stop) {
        if (stop == null) {
            return null;
        }

        StopResponseBody response = new StopResponseBody();
        response.sequence = stop.getSequence().getValue();
        response.container = ContainerResponseMapper.toResponseBody(stop.getContainer());
        response.collectedKilograms = stop.getCollectedKilograms().getValue();
        response.collectedLiters = stop.getCollectedLiters().getValue();
        response.distanceFromPreviousMeters = stop.getDistanceFromPreviousMeters().getValue();
        response.cumulativeDistanceMeters = stop.getCumulativeDistanceMeters().getValue();

        return response;
    }
}
