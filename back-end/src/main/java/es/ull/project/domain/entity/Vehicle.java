package es.ull.project.domain.entity;

import java.util.Objects;
import java.util.UUID;

import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.Capacity;

/**
 * Vehicle
 *
 * Represents a truck or transport unit participating in the service system.
 * It is an aggregate root.
 */
public class Vehicle {

    public static final String ID_NOT_DEFINED = "Vehicle id is not defined";
    public static final String TYPE_NOT_DEFINED = "Vehicle type is not defined";
    public static final String CAPACITY_NOT_DEFINED = "Vehicle capacity is not defined";
    public static final String COST_NOT_DEFINED = "Transportation variable cost is not defined";

    /**
     * Identifier of the vehicle.
     * It is required and immutable.
     */
    private final UUID id;

    /**
     * Enumeration of vehicle type (e.g., LIGHT_TRUCK, HEAVY_TRUCK...)
     * It is required.
     */
    private VehicleType vehicleType;

    /**
     * Transport capacity of the vehicle.
     * It is required.
     */
    private Capacity transportCapacity;

    /**
     * Cost per kilometer of operation.
     * It is required.
     */
    private TransportationVariableCost costPerKilometer;

    /**
     * Creates a new Vehicle with the specified parameters.
     *
     * @param vehicleType       the type of vehicle
     * @param transportCapacity the transport capacity of the vehicle
     * @param costPerKilometer  the cost per kilometer of operation
     */
    public Vehicle(
            VehicleType vehicleType,
            Capacity transportCapacity,
            TransportationVariableCost costPerKilometer) {
        validateVehicleType(vehicleType);
        validateCapacity(transportCapacity);
        validateCost(costPerKilometer);
        this.id = UUID.randomUUID();
        this.vehicleType = vehicleType;
        this.transportCapacity = transportCapacity;
        this.costPerKilometer = costPerKilometer;
    }

    /**
     * Copy constructor.
     * Creates a new Vehicle as a copy of another Vehicle.
     *
     * @param otherObject the Vehicle to copy
     */
    public Vehicle(Vehicle otherObject) {
        this.id = otherObject.id;
        this.vehicleType = otherObject.vehicleType;
        this.transportCapacity = otherObject.transportCapacity;
        this.costPerKilometer = otherObject.costPerKilometer;
    }

    /**
     * Restore constructor.
     * Restores a Vehicle from persistence with all its attributes.
     *
     * @param id                the vehicle identifier
     * @param vehicleType       the type of vehicle
     * @param transportCapacity the transport capacity
     * @param costPerKilometer  the cost per kilometer
     */
    public Vehicle(UUID id,
            VehicleType vehicleType,
            Capacity transportCapacity,
            TransportationVariableCost costPerKilometer) {
        validateVehicleType(vehicleType);
        validateCapacity(transportCapacity);
        validateCost(costPerKilometer);
        this.id = id;
        this.vehicleType = vehicleType;
        this.transportCapacity = transportCapacity;
        this.costPerKilometer = costPerKilometer;
    }

    /**
     * Validates that the vehicle type is not null.
     *
     * @param type the vehicle type to validate
     * @throws IllegalArgumentException if the vehicle type is null
     */
    private void validateVehicleType(VehicleType type) {
        if (type == null) {
            throw new IllegalArgumentException(TYPE_NOT_DEFINED);
        }
    }

    /**
     * Validates that the capacity is not null.
     *
     * @param capacity the capacity to validate
     * @throws IllegalArgumentException if the capacity is null
     */
    private void validateCapacity(Capacity capacity) {
        if (capacity == null) {
            throw new IllegalArgumentException(CAPACITY_NOT_DEFINED);
        }
    }

    /**
     * Validates that the cost is not null.
     *
     * @param cost the cost to validate
     * @throws IllegalArgumentException if the cost is null
     */
    private void validateCost(TransportationVariableCost cost) {
        if (cost == null) {
            throw new IllegalArgumentException(COST_NOT_DEFINED);
        }
    }

    /**
     * Returns the vehicle identifier.
     *
     * @return the unique identifier of the vehicle
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Returns the vehicle type.
     *
     * @return the type of the vehicle
     */
    public VehicleType getVehicleType() {
        return this.vehicleType;
    }

    /**
     * Updates the vehicle type.
     *
     * @param vehicleType the new vehicle type
     */
    public void updateVehicleType(VehicleType vehicleType) {
        validateVehicleType(vehicleType);
        this.vehicleType = vehicleType;
    }

    /**
     * Returns the transport capacity.
     *
     * @return the transport capacity of the vehicle
     */
    public Capacity getTransportCapacity() {
        return this.transportCapacity;
    }

    /**
     * Updates the transport capacity.
     *
     * @param capacity the new transport capacity
     */
    public void updateTransportCapacity(Capacity capacity) {
        validateCapacity(capacity);
        this.transportCapacity = capacity;
    }

    /**
     * Returns the cost per kilometer.
     *
     * @return the transportation variable cost per kilometer
     */
    public TransportationVariableCost getCostPerKilometer() {
        return this.costPerKilometer;
    }

    /**
     * Updates the cost per kilometer.
     *
     * @param cost the new cost per kilometer
     */
    public void updateCostPerKilometer(TransportationVariableCost cost) {
        validateCost(cost);
        this.costPerKilometer = cost;
    }

    /**
     * Compares this vehicle to another object for equality.
     *
     * @param otherObject the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Vehicle otherVehicle = (Vehicle) otherObject;
        return Objects.equals(this.id, otherVehicle.id);
    }

    /**
     * Returns a hash code value for this vehicle.
     *
     * @return a hash code value based on the vehicle identifier
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * Returns a string representation of this vehicle.
     *
     * @return a formatted string containing all vehicle attributes
     */
    @Override
    public String toString() {
        return String.format(
                "Vehicle={id=%s, type=%s, capacity=%s, costPerKm=%s}",
                this.id,
                this.vehicleType,
                this.transportCapacity,
                this.costPerKilometer);
    }
}
