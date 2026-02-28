package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.domain.entity.Facility;

/**
 * Mapper class to convert Facility domain entities to FacilityResponseBody DTOs
 * This class handles the transformation between the domain layer and the REST adapter layer
 */
public class FacilityResponseMapper {

    private static final String UTILITY_CLASS_EXCEPTION_MESSAGE = "Utility class cannot be instantiated";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private FacilityResponseMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_EXCEPTION_MESSAGE);
    }

    /**
     * Converts a Facility domain entity to a FacilityResponseBody DTO
     * Assigns domain value objects and enums directly without extracting primitives
     *
     * @param facility The Facility domain entity to convert
     * @return FacilityResponseBody DTO ready to be serialized as JSON
     */
    public static FacilityResponseBody toResponseBody(Facility facility) {
        FacilityResponseBody responseBody = new FacilityResponseBody();
        responseBody.id = facility.getId();
        responseBody.facilityType = facility.getFacilityType();
        responseBody.location = facility.getLocation();
        responseBody.capacity = facility.getCapacity();
        responseBody.openingFixedCost = facility.getOpeningFixedCost();
        responseBody.status = facility.getStatus();
        responseBody.assignedWasteDemand = facility.getAssignedWasteDemand();
        return responseBody;
    }
}
