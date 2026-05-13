package es.ull.project.adapter.rest.response.vehicle;

import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.name.Name;
import java.util.UUID;

/**
 * Data Transfer Object representing a Vehicle response
 * This class is used to send Vehicle data in HTTP responses
 * It contains all the data of a vehicle using domain value objects
 */
public class VehicleResponseBody {

    /**
     * Unique identifier of the vehicle
     */
    public UUID id;
    public Name name;

    /**
     * Type of the vehicle (COLLECTION_TRUCK, TRANSFER_TRUCK, SUPPORT_VEHICLE)
     */
    public VehicleType vehicleType;

    /**
     * Transport capacity of the vehicle in kilograms
     */
    public VehicleCapacityKilograms capacityKilograms;

    /**
     * Transport capacity of the vehicle in liters
     */
    public VehicleCapacityLiters capacityLiters;

    /**
     * Variable cost per kilometer traveled
     */
    public TransportationVariableCost costPerKilometer;
}
