package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.vehicle.VehicleResponseBody;
import es.ull.project.domain.entity.Vehicle;

/**
 * Mapper class to convert Vehicle domain entities to VehicleResponseBody DTOs
 * This class handles the transformation between the domain layer and the REST adapter layer
 */
public class VehicleResponseMapper {

    /**
     * Converts a Vehicle domain entity to a VehicleResponseBody DTO
     * Maps all the vehicle properties including nested objects (capacity and cost)
     *
     * @param vehicle The Vehicle domain entity to convert
     * @return VehicleResponseBody DTO ready to be serialized as JSON
     */
    public static VehicleResponseBody toResponseBody(Vehicle vehicle) {
        VehicleResponseBody responseBody = new VehicleResponseBody();
        
        // Map vehicle ID
        responseBody.id = vehicle.getId();
        
        // Map vehicle type (convert enum to string)
        responseBody.vehicleType = vehicle.getVehicleType().name();
        
        // Map transport capacity
        responseBody.transportCapacity = new VehicleResponseBody.TransportCapacityData();
        responseBody.transportCapacity.value = vehicle.getTransportCapacity().getValue();
        responseBody.transportCapacity.quantityUnit = vehicle.getTransportCapacity().getQuantityUnit().getValue();
        responseBody.transportCapacity.timeUnit = vehicle.getTransportCapacity().getTimeUnit().name();
        
        // Map cost per kilometer
        responseBody.costPerKilometer = new VehicleResponseBody.CostPerKilometerData();
        responseBody.costPerKilometer.amount = vehicle.getCostPerKilometer().getAmount();
        
        // Map currency (optional field)
        if (vehicle.getCostPerKilometer().getCurrency().isPresent()) {
            responseBody.costPerKilometer.currency = vehicle.getCostPerKilometer().getCurrency().get().getCode();
        }
        
        return responseBody;
    }
}
