package es.ull.project.domain.entity;

import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import java.util.Objects;
import java.util.UUID;

/**
 * Vehicle
 *
 * Represents a truck or transport unit participating in the service system.
 * It is an aggregate root.
 */
public class Vehicle {

    public static final String ID_NOT_DEFINED = "Vehicle id is not defined";
    public static final String TYPE_NOT_DEFINED = "Vehicle type is not defined";
    public static final String CAPACITY_Kilograms_NOT_DEFINED = "Vehicle capacity in kilograms is not defined";
    public static final String CAPACITY_liters_NOT_DEFINED = "Vehicle capacity in liters is not defined";
    public static final String COST_NOT_DEFINED = "Transportation variable cost is not defined";

    /**
     * Identifier of the vehicle.
     * It is required and immutable.
     * It is a computed attribute.
     */
    private final UUID id;

    /**
     * Enumeration of vehicle type (e.g., LIGHT_TRUCK, HEAVY_TRUCK...)
     * It is a required attribute.
     */
    private VehicleType vehicleType;

    /**
     * Transport capacity of the vehicle in kilograms.
     * It is a required attribute.
     */
    private VehicleCapacityKilograms capacityKilograms;

    /**
     * Transport capacity of the vehicle in liters.
     * It is a required attribute.
     */
    private VehicleCapacityLiters capacityLiters;

    /**
     * Cost per kilometer of operation.
     * It is a required attribute.
     */
    private TransportationVariableCost costPerKilometer;

    /**
     * Creates a new Vehicle with the specified parameters.
     *
     * @param vehicleType       the type of vehicle
     * @param capacityKilograms the capacity of the vehicle in kilograms
     * @param capacityLiters    the capacity of the vehicle in liters
     * @param costPerKilometer  the cost per kilometer of operation
     */
    public Vehicle(
            VehicleType vehicleType,
            VehicleCapacityKilograms capacityKilograms,
            VehicleCapacityLiters capacityLiters,
            TransportationVariableCost costPerKilometer) {
        validateVehicleType(vehicleType);
        validateCapacityKilograms(capacityKilograms);
        validateCapacityLiters(capacityLiters);
        validateCost(costPerKilometer);
        this.id = UUID.randomUUID();
        this.vehicleType = vehicleType;
        this.capacityKilograms = capacityKilograms;
        this.capacityLiters = capacityLiters;
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
        this.capacityKilograms = otherObject.capacityKilograms;
        this.capacityLiters = otherObject.capacityLiters;
        this.costPerKilometer = otherObject.costPerKilometer;
    }

    /**
     * Restore constructor.
     * Restores a Vehicle from persistence with all its attributes.
     *
     * @param id                the vehicle identifier
     * @param vehicleType       the type of vehicle
     * @param capacityKilograms the capacity in kilograms
     * @param capacityLiters    the capacity in liters
     * @param costPerKilometer  the cost per kilometer
     */
    public Vehicle(UUID id,
            VehicleType vehicleType,
            VehicleCapacityKilograms capacityKilograms,
            VehicleCapacityLiters capacityLiters,
            TransportationVariableCost costPerKilometer) {
        validateVehicleType(vehicleType);
        validateCapacityKilograms(capacityKilograms);
        validateCapacityLiters(capacityLiters);
        validateCost(costPerKilometer);
        this.id = id;
        this.vehicleType = vehicleType;
        this.capacityKilograms = capacityKilograms;
        this.capacityLiters = capacityLiters;
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
     * Validates that the capacity in Kilograms is not null.
     *
     * @param capacity the capacity to validate
     * @throws IllegalArgumentException if the capacity is null
     */
    private void validateCapacityKilograms(VehicleCapacityKilograms capacity) {
        if (capacity == null) {
            throw new IllegalArgumentException(CAPACITY_Kilograms_NOT_DEFINED);
        }
    }

    /**
     * Validates that the capacity in liters is not null.
     *
     * @param capacity the capacity to validate
     * @throws IllegalArgumentException if the capacity is null
     */
    private void validateCapacityLiters(VehicleCapacityLiters capacity) {
        if (capacity == null) {
            throw new IllegalArgumentException(CAPACITY_liters_NOT_DEFINED);
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
     * Returns the transport capacity in kilograms.
     *
     * @return the transport capacity in kilograms
     */
    public VehicleCapacityKilograms getCapacityKilograms() {
        return this.capacityKilograms;
    }

    /**
     * Updates the transport capacity in kilograms.
     *
     * @param capacity the new transport capacity in kilograms
     */
    public void updateCapacityKilograms(VehicleCapacityKilograms capacity) {
        validateCapacityKilograms(capacity);
        this.capacityKilograms = capacity;
    }

    /**
     * Returns the transport capacity in liters.
     *
     * @return the transport capacity in liters
     */
    public VehicleCapacityLiters getCapacityLiters() {
        return this.capacityLiters;
    }

    /**
     * Updates the transport capacity in liters.
     *
     * @param capacity the new transport capacity in liters
     */
    public void updateCapacityLiters(VehicleCapacityLiters capacity) {
        validateCapacityLiters(capacity);
        this.capacityLiters = capacity;
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
                "Vehicle={id=%s, type=%s, capacityKilograms=%s, capacityLiters=%s, costPerKm=%s}",
                this.id,
                this.vehicleType,
                this.capacityKilograms,
                this.capacityLiters,
                this.costPerKilometer);
    }
}
