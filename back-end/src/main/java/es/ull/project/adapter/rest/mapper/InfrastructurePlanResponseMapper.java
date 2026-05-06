package es.ull.project.adapter.rest.mapper;

import java.util.ArrayList;
import java.util.List;

import es.ull.project.adapter.rest.response.dailyplan.DailyPlanResponseBody;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.ServiceAssignment;

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
        responseBody.executedAt = plan.getExecutedAt();
        // compute a basic status: if estimated cost exceeds budget mark as OVERBUDGET, otherwise SUBOPTIMAL as default
        try {
            if (plan.getEstimatedTotalCost() != null && plan.getMaxBudget() != null && plan.getEstimatedTotalCost().greaterThan(plan.getMaxBudget())) {
                responseBody.status = "OVERBUDGET";
            } else {
                responseBody.status = "SUBOPTIMAL";
            }
        } catch (Exception e) {
            responseBody.status = null;
        }
        
        if (plan.getTotalCollectedKilograms() != null) {
            responseBody.totalCollectedKilograms = plan.getTotalCollectedKilograms().getValue();
        }
        if (plan.getTotalCollectedLiters() != null) {
            responseBody.totalCollectedLiters = plan.getTotalCollectedLiters().getValue();
        }
        if (plan.getTotalDistanceMeters() != null) {
            responseBody.totalDistanceMeters = plan.getTotalDistanceMeters().getValue();
        }
        
        return responseBody;
    }
}
