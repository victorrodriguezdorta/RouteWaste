package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import es.ull.project.domain.entity.Container;

/**
 * Mapper class to convert Container domain entities to ContainerResponseBody DTOs
 * This class handles the transformation between the domain layer and the REST adapter layer
 */
public class ContainerResponseMapper {

    private static final String UTILITY_CLASS_ERROR_MESSAGE = "Utility class cannot be instantiated";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ContainerResponseMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR_MESSAGE);
    }

    /**
     * Converts a Container domain entity to a ContainerResponseBody DTO
     * Assigns domain value objects directly without extracting primitives
     *
     * @param container The Container domain entity to convert
     * @return ContainerResponseBody DTO ready to be serialized as JSON
     */
    public static ContainerResponseBody toResponseBody(Container container) {
        ContainerResponseBody responseBody = new ContainerResponseBody();
        responseBody.id = container.getId();
        responseBody.name = container.getName();
        responseBody.location = container.getLocation();
        responseBody.wasteType = container.getWasteType();
        responseBody.capacityLiters = container.getCapacityLiters();
        responseBody.dailyDemandLitersPerDay = container.getDailyDemandLitersPerDay();
        responseBody.serviceZone = container.getServiceZone().orElse(null);
        return responseBody;
    }
}
