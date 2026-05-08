package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanListResponseBody;
import es.ull.project.domain.entity.InfrastructurePlan;

/**
 * Mapper class to convert InfrastructurePlan domain entities to lightweight list response DTOs.
 */
public class InfrastructurePlanListResponseMapper {

    private static final String UTILITY_CLASS_EXCEPTION_MESSAGE = "Utility class cannot be instantiated";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private InfrastructurePlanListResponseMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_EXCEPTION_MESSAGE);
    }

    /**
     * Converts an InfrastructurePlan domain entity to the lightweight list response DTO.
     *
     * @param plan The InfrastructurePlan domain entity to convert.
     * @return InfrastructurePlanListResponseBody ready to be serialized as JSON.
     */
    public static InfrastructurePlanListResponseBody toResponseBody(InfrastructurePlan plan) {
        InfrastructurePlanListResponseBody responseBody = new InfrastructurePlanListResponseBody();
        responseBody.id = plan.getId();
        responseBody.executedAt = plan.getExecutedAt();
        responseBody.estimatedTotalCost = plan.getEstimatedTotalCost();
        responseBody.numberOfDays = plan.getNumberOfDays();
        responseBody.averagePickupTimeMinutes = plan.getAveragePickupTimeMinutes();
        return responseBody;
    }
}