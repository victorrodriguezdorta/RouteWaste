package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.dailyplan.DailyPlanResponseBody;
import es.ull.project.adapter.rest.response.infrastructureplan.ContainerDailyStateResponseBody;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.ContainerDailyState;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.enumerate.InfrastructurePlanStatus;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.name.Name;
import es.ull.project.domain.valueobject.time.PlanDay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper class to convert InfrastructurePlan domain entities to InfrastructurePlanResponseBody DTOs
 * This class handles the transformation between the domain layer and the REST adapter layer
 * Includes complete facility and container information using their respective mappers
 */
public class InfrastructurePlanResponseMapper {

    private static final String UTILITY_CLASS_EXCEPTION_MESSAGE = "Utility class cannot be instantiated";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private InfrastructurePlanResponseMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_EXCEPTION_MESSAGE);
    }

    /**
     * Converts an InfrastructurePlan domain entity to an InfrastructurePlanResponseBody DTO
     * Uses FacilityResponseMapper and ContainerResponseMapper to include complete entity information
     *
     * @param plan       The InfrastructurePlan domain entity to convert
     * @param dailyPlans The associated daily plans mapped to DTOs
     * @return InfrastructurePlanResponseBody DTO ready to be serialized as JSON
     */
    public static InfrastructurePlanResponseBody toResponseBody(InfrastructurePlan plan, List<DailyPlanResponseBody> dailyPlans) {
        InfrastructurePlanResponseBody responseBody = new InfrastructurePlanResponseBody();
        responseBody.id = plan.getId();
        responseBody.period = plan.getPeriod();
        responseBody.maxBudget = plan.getMaxBudget();
        responseBody.estimatedTotalCost = plan.getEstimatedTotalCost();
        responseBody.servicePolicies = plan.getServicePolicies();
        responseBody.selectedFacilities = new ArrayList<>();
        for (Facility facility : plan.getSelectedFacilities()) {
            responseBody.selectedFacilities.add(FacilityResponseMapper.toResponseBody(facility));
        }
        responseBody.serviceAssignments = new ArrayList<>();
        for (ServiceAssignment assignment : plan.getServiceAssignments()) {
            responseBody.serviceAssignments.add(ServiceAssignmentResponseMapper.toResponseBody(assignment));
        }
        responseBody.dailyPlans = dailyPlans;
        responseBody.containerStateMonitoring = new ArrayList<>();
        Map<String, Name> containerIdToName = new HashMap<>();
        for (ServiceAssignment assignment : plan.getServiceAssignments()) {
            for (Container container : assignment.getAssignedContainers()) {
                containerIdToName.put(container.getId().toString(), container.getName());
            }
        }
        for (ContainerDailyState state : plan.getContainerDailyStates()) {
            ContainerDailyStateResponseBody stateResponse = new ContainerDailyStateResponseBody();
            stateResponse.id = state.getId();
            stateResponse.containerId = state.getContainerId();
            stateResponse.containerName = containerIdToName.get(state.getContainerId().toString());
            stateResponse.planDay = new PlanDay(state.getPlanDay());
            stateResponse.dailyFillingLiters = CollectedVolumeLiters.fromLiters(state.getDailyFillingLiters());
            stateResponse.containerCapacityLiters = new ContainerCapacityLiters(state.getContainerCapacityLiters());
            stateResponse.dailyDemandLitersPerDay = new DailyWasteDemandLitersPerDay(state.getDailyDemandLitersPerDay());
            stateResponse.status = state.getStatus();
            responseBody.containerStateMonitoring.add(stateResponse);
        }
        responseBody.numberOfDays = plan.getNumberOfDays().orElse(null);
        responseBody.averagePickupTimeMinutes = plan.getAveragePickupTimeMinutes().orElse(null);
        responseBody.executedAt = plan.getExecutedAt().orElse(null);
        responseBody.validityState = plan.getValidityState();
        responseBody.executionState = plan.getExecutionState();
        responseBody.failureReason = plan.getFailureReason().orElse(null);
        responseBody.executionRequestJson = plan.getExecutionRequestJson()
                .map(AlgorithmJsonPayload::new)
                .orElse(null);
        try {
            if (plan.getEstimatedTotalCost() != null && plan.getMaxBudget() != null && plan.getEstimatedTotalCost().greaterThan(plan.getMaxBudget())) {
                responseBody.status = InfrastructurePlanStatus.OVERBUDGET;
            } else {
                responseBody.status = InfrastructurePlanStatus.SUBOPTIMAL;
            }
        } catch (Exception e) {
            responseBody.status = null;
        }
        if (plan.getTotalCollectedKilograms() != null) {
            responseBody.totalCollectedKilograms = plan.getTotalCollectedKilograms();
        }
        if (plan.getTotalCollectedLiters() != null) {
            responseBody.totalCollectedLiters = plan.getTotalCollectedLiters();
        }
        if (plan.getTotalDistanceMeters() != null) {
            responseBody.totalDistanceMeters = plan.getTotalDistanceMeters();
        }
        return responseBody;
    }
}
