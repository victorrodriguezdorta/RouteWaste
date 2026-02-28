package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.vehicle.VehicleResponseBody;
import es.ull.project.domain.entity.Vehicle;

/**
 * Mapper class to convert Vehicle domain entities to VehicleResponseBody DTOs
 * This class handles the transformation between the domain layer and the REST adapter layer
 */
public class VehicleResponseMapper {

    private static final String UTILITY_CLASS_ERROR_MESSAGE = "This is a utility class and cannot be instantiated";

    /**
     * Private constructor to prevent instantiation of this utility class
     */
    private VehicleResponseMapper() {
        throw new UnsupportedOperationException(UTILITY_CLASS_ERROR_MESSAGE);
    }

    /**
     * Converts a Vehicle domain entity to a VehicleResponseBody DTO
     * Maps all the vehicle properties including nested objects (capacity and cost)
     *
     * @param vehicle The Vehicle domain entity to convert
     * @return VehicleResponseBody DTO ready to be serialized as JSON
     */
    public static VehicleResponseBody toResponseBody(Vehicle vehicle) {
        VehicleResponseBody responseBody = new VehicleResponseBody();
        responseBody.id = vehicle.getId();
        responseBody.vehicleType = vehicle.getVehicleType();
        responseBody.transportCapacity = vehicle.getTransportCapacity();
        responseBody.costPerKilometer = vehicle.getCostPerKilometer();
        return responseBody;
    }
}
