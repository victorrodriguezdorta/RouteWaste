package es.ull.project.adapter.rest.request.vehicle;

import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.Capacity;

/**
 * VehiclePutRequestBody
 * 
 * Data Transfer Object representing the request body for updating an existing Vehicle.
 * This DTO is used in PUT requests to the vehicle endpoint.
 * 
 * Public attributes are used to allow direct access without getters/setters,
 * reducing complexity and facilitating serialization/deserialization in the
 * REST API context. As a DTO designed exclusively for data transfer, it does
 * not require encapsulation like domain entities.
 * 
 * This class contains no business logic, only data representation.
 */
public class VehiclePutRequestBody {

    /**
     * Type of the vehicle (e.g., COLLECTION_TRUCK, TRANSFER_TRUCK).
     * Required field.
     */
    public VehicleType vehicleType;

    /**
     * Transport capacity of the vehicle.
     * Required field.
     */
    public Capacity transportCapacity;

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
                "VehiclePutRequestBody={vehicleType=%s, transportCapacity=%s, costPerKilometer=%s}",
                this.vehicleType,
                this.transportCapacity,
                this.costPerKilometer);
    }
}
