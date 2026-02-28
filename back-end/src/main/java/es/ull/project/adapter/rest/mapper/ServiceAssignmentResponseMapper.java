package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.serviceassignment.ServiceAssignmentResponseBody;
import es.ull.project.domain.entity.ServiceAssignment;

/**
 * Mapper class to convert ServiceAssignment domain entities to ServiceAssignmentResponseBody DTOs
 * This class handles the transformation between the domain layer and the REST adapter layer,
 * including mapping complete Container and Facility entities using their respective mappers.
 */
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
        ServiceAssignmentResponseBody responseBody = new ServiceAssignmentResponseBody();
        responseBody.id = assignment.getId();
        responseBody.container = ContainerResponseMapper.toResponseBody(assignment.getContainer());
        responseBody.facility = FacilityResponseMapper.toResponseBody(assignment.getFacility());
        responseBody.wasteDemand = assignment.getWasteDemand();
        responseBody.distance = assignment.getDistance();
        responseBody.serviceTime = assignment.getServiceTime();
        responseBody.transportCost = assignment.getTransportCost();
        return responseBody;
    }
}
