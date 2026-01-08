package es.ull.project.domain.entity;

import java.util.Objects;
import java.util.UUID;

import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;

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
     * Creates a new Vehicle.
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


    private void validateVehicleType(VehicleType type) {
        if (type == null) {
            throw new IllegalArgumentException(TYPE_NOT_DEFINED);
        }
    }

    private void validateCapacity(Capacity capacity) {
        if (capacity == null) {
            throw new IllegalArgumentException(CAPACITY_NOT_DEFINED);
        }
    }

    private void validateCost(TransportationVariableCost cost) {
        if (cost == null) {
            throw new IllegalArgumentException(COST_NOT_DEFINED);
        }
    }

    public UUID getId() {
        return this.id;
    }

    public VehicleType getVehicleType() {
        return this.vehicleType;
    }

    public void updateVehicleType(VehicleType vehicleType) {
        validateVehicleType(vehicleType);
        this.vehicleType = vehicleType;
    }

    public Capacity getTransportCapacity() {
        return this.transportCapacity;
    }

    public void updateTransportCapacity(Capacity capacity) {
        validateCapacity(capacity);
        this.transportCapacity = capacity;
    }

    public TransportationVariableCost getCostPerKilometer() {
        return this.costPerKilometer;
    }

    public void updateCostPerKilometer(TransportationVariableCost cost) {
        validateCost(cost);
        this.costPerKilometer = cost;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return String.format(
                "Vehicle={id=%s, type=%s, capacity=%s, costPerKm=%s}",
                this.id,
                this.vehicleType,
                this.transportCapacity,
                this.costPerKilometer
        );
    }
}
