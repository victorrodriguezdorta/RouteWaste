package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.serviceassignment.ServiceAssignmentResponseBody;
import es.ull.project.domain.entity.ServiceAssignment;
import java.util.stream.Collectors;

public class ServiceAssignmentResponseMapper {

    private static final String UTILITY_CLASS_ERROR_MESSAGE = "Utility class cannot be instantiated";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ServiceAssignmentResponseMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR_MESSAGE);
    }

    /**
     * Converts a ServiceAssignment domain entity to a ServiceAssignmentResponseBody DTO.
     * 
     * Following DDD principles, this mapper passes domain value objects directly
     * to the DTO instead of converting them to primitives. The serializer will
     * extract the necessary fields when converting to JSON.
     * 
     * Maps all the service assignment properties including value objects (waste demand,
     * distance, service time, transport cost) and complete container and facility entities.
     *
     * @param assignment The ServiceAssignment domain entity to convert
     * @return ServiceAssignmentResponseBody DTO ready to be serialized as JSON
     */
    public static ServiceAssignmentResponseBody toResponseBody(ServiceAssignment assignment) {
        if (assignment == null) {
            return null;
        }
        ServiceAssignmentResponseBody responseBody = new ServiceAssignmentResponseBody();
        responseBody.id = assignment.getId();
        responseBody.infrastructurePlanId = assignment.getInfrastructurePlan().getId();
        responseBody.facilityId = assignment.getFacility().getId();
        if (assignment.getAssignedContainers() != null) {
            responseBody.assignedContainers = assignment.getAssignedContainers().stream()
                    .map(ContainerResponseMapper::toResponseBody)
                    .collect(Collectors.toList());
        }
        return responseBody;
    }
}
