package es.ull.project.adapter.rest.mapper;

import java.util.stream.Collectors;

import es.ull.project.adapter.rest.response.dailyplan.DailyPlanResponseBody;
import es.ull.project.adapter.rest.response.dailyplan.StopResponseBody;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Stop;
import es.ull.project.domain.enumerate.StopType;
import es.ull.project.domain.valueobject.time.ServiceDate;

/**
 * Mapper for DailyPlan related response DTOs.
 */
public class DailyPlanResponseMapper {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DailyPlanResponseMapper() {
    }

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
        response.facilityId = entity.getFacility().getId();
        response.serviceDate = new ServiceDate(entity.getServiceDate());
        response.planDay = entity.getPlanDay() != null ? entity.getPlanDay().getDay() : null;
        response.vehicleId = entity.getVehicle().getId();
        response.totalCollectedKilograms = entity.getTotalCollectedKilograms();
        response.totalCollectedLiters = entity.getTotalCollectedLiters();
        response.totalDistanceMeters = entity.getTotalDistanceMeters();
        if (entity.getStops() != null) {
            response.stops = entity.getStops().stream()
                    .map(DailyPlanResponseMapper::toStopResponseBody)
                    .collect(Collectors.toList());
        }
        return response;
    }

    /**
     * Maps a {@link Stop} domain entity to its response DTO.
     *
     * @param stop the Stop entity to map
     * @return the corresponding {@link StopResponseBody}, or {@code null} if the input is {@code null}
     */
    private static StopResponseBody toStopResponseBody(Stop stop) {
        if (stop == null) {
            return null;
        }
        StopResponseBody response = new StopResponseBody();
        response.sequence = stop.getSequence();
        response.type = stop.getType();
        response.containerId = stop.getContainer() != null ? stop.getContainer().getId() : null;
        response.collectedKilograms = stop.getCollectedKilograms();
        response.collectedLiters = stop.getCollectedLiters();
        response.distanceFromPreviousMeters = stop.getDistanceFromPreviousMeters();
        response.cumulativeDistanceMeters = stop.getCumulativeDistanceMeters();
        response.containerActualLiters = stop.getContainerActualLiters();
        response.alerts = stop.getAlerts();
        return response;
    }
}
