package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.serviceassignment.ServiceAssignmentResponseBody;
import es.ull.project.domain.entity.ServiceAssignment;

/**
 * Mapper class to convert ServiceAssignment domain entities to ServiceAssignmentResponseBody DTOs
 * This class handles the transformation between the domain layer and the REST adapter layer,
 * including mapping complete Container and Facility entities using their respective mappers.
 */
public class ServiceAssignmentResponseMapper {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ServiceAssignmentResponseMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Converts a ServiceAssignment domain entity to a ServiceAssignmentResponseBody DTO
     * Maps all the service assignment properties including nested objects (waste demand, distance, service time, transport cost)
     * and complete container and facility entities.
     *
     * @param assignment The ServiceAssignment domain entity to convert
     * @return ServiceAssignmentResponseBody DTO ready to be serialized as JSON
     */
    public static ServiceAssignmentResponseBody toResponseBody(ServiceAssignment assignment) {
        ServiceAssignmentResponseBody responseBody = new ServiceAssignmentResponseBody();
        responseBody.id = assignment.getId();
        responseBody.container = ContainerResponseMapper.toResponseBody(assignment.getContainer());
        responseBody.facility = FacilityResponseMapper.toResponseBody(assignment.getFacility());
        responseBody.wasteDemand = new ServiceAssignmentResponseBody.WasteDemandData();
        responseBody.wasteDemand.value = assignment.getWasteDemand().getValue();
        responseBody.wasteDemand.quantityUnit = assignment.getWasteDemand().getQuantityUnit().getValue();
        responseBody.wasteDemand.timeUnit = assignment.getWasteDemand().getTimeUnit().name();
        responseBody.distance = new ServiceAssignmentResponseBody.DistanceData();
        responseBody.distance.meters = assignment.getDistance().toMeters();
        responseBody.distance.kilometers = assignment.getDistance().toKilometers();
        responseBody.serviceTime = new ServiceAssignmentResponseBody.ServiceTimeData();
        responseBody.serviceTime.minutes = assignment.getServiceTime().getValue();
        responseBody.transportCost = new ServiceAssignmentResponseBody.TransportCostData();
        responseBody.transportCost.amount = assignment.getTransportCost().getAmount();
        if (assignment.getTransportCost().getCurrency().isPresent()) {
            responseBody.transportCost.currency = assignment.getTransportCost().getCurrency().get().getCode();
        }
        return responseBody;
    }
}
