package es.ull.project.adapter.rest.request.vehicle;

import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;

/**
 * VehiclePostRequestBody
 * 
 * Data Transfer Object representing the request body for creating a new Vehicle.
 * This DTO is used in POST requests to the vehicle endpoint.
 * 
 * Public attributes are used to allow direct access without getters/setters,
 * reducing complexity and facilitating serialization/deserialization in the
 * REST API context. As a DTO designed exclusively for data transfer, it does
 * not require encapsulation like domain entities.
 * 
 * This class contains no business logic, only data representation.
 * 
 * Deserialization is handled by VehiclePostRequestBodyDeserializer registered in RestConfiguration.
 */
public class VehiclePostRequestBody {

    /**
     * Type of the vehicle (e.g., COLLECTION_TRUCK, TRANSFER_TRUCK).
     * Required field.
     */
    public VehicleType vehicleType;

    /**
     * Transport capacity of the vehicle in kilograms.
     * Required field.
     */
    public VehicleCapacityKilograms capacityKilograms;

    /**
     * Transport capacity of the vehicle in liters.
     * Required field.
     */
    public VehicleCapacityLiters CapacityLiters;

    /**
     * Cost per kilometer of operation.
     * Required field.
     */
    public TransportationVariableCost costPerKilometer;

    /**
     * Returns a string representation of this request body.
     * 
     * @return formatted string containing all attributes
     */
    @Override
    public String toString() {
        return String.format(
                "VehiclePostRequestBody={vehicleType=%s, capacityKilograms=%s, CapacityLiters=%s, costPerKilometer=%s}",
                this.vehicleType,
                this.capacityKilograms,
                this.CapacityLiters,
                this.costPerKilometer);
    }
}
