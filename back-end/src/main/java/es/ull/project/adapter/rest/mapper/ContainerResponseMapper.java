package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import es.ull.project.domain.entity.Container;

/**
 * Mapper class to convert Container domain entities to ContainerResponseBody DTOs
 * This class handles the transformation between the domain layer and the REST adapter layer
 */
public class ContainerResponseMapper {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ContainerResponseMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Converts a Container domain entity to a ContainerResponseBody DTO
     * Maps all the container properties including nested objects (location and waste demand)
     *
     * @param container The Container domain entity to convert
     * @return ContainerResponseBody DTO ready to be serialized as JSON
     */
    public static ContainerResponseBody toResponseBody(Container container) {
        ContainerResponseBody responseBody = new ContainerResponseBody();
        responseBody.id = container.getId();
        responseBody.location = new ContainerResponseBody.LocationData();
        responseBody.location.latitude = container.getLocation().getLatitude();
        responseBody.location.longitude = container.getLocation().getLongitude();
        responseBody.location.postalAddress = container.getLocation().getPostalAddress();
        responseBody.location.gisReference = container.getLocation().getGISReference();
        responseBody.wasteType = container.getWasteType().name();
        responseBody.wasteDemand = new ContainerResponseBody.WasteDemandData();
        responseBody.wasteDemand.value = container.getWasteDemand().getValue();
        responseBody.wasteDemand.quantityUnit = container.getWasteDemand().getQuantityUnit().getValue();
        responseBody.wasteDemand.timeUnit = container.getWasteDemand().getTimeUnit().name();
        if (container.getServiceZone().isPresent()) {
            responseBody.serviceZone = container.getServiceZone().get().name();
        }
        return responseBody;
    }
}
