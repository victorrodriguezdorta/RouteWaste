package es.ull.project.domain.entitytests;

import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.name.Name;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class VehicleTests {

    private static final String VEHICLE_NAME_PREFIX = "vehicle";
    private static final String VEHICLE_TO_STRING_TEMPLATE =
            "Vehicle={id=%s, name=%s, type=%s, capacityKilograms=%s, CapacityLiters=%s, costPerKm=%s}";

    /**
     * Creates valid random vehicle capacity in kilograms.
     *
     * @return valid vehicle capacity in kilograms.
     */
    private static VehicleCapacityKilograms randomCapacityKilograms() {
        return new VehicleCapacityKilograms(5000.0);
    }

    /**
     * Creates valid random vehicle capacity in liters.
     *
     * @return valid vehicle capacity in liters.
     */
    private static VehicleCapacityLiters randomCapacityLiters() {
        return new VehicleCapacityLiters(8000.0);
    }

    /**
     * Creates a valid random transportation variable cost.
     *
     * @return valid transportation variable cost.
     */
    private static TransportationVariableCost randomCost() {
        return new TransportationVariableCost(0.75);
    }

    /**
     * Creates a valid random vehicle.
     *
     * @return valid vehicle.
     */
    private static Vehicle randomVehicle() {
        return new Vehicle(
                randomName(),
                VehicleType.random(),
                randomCapacityKilograms(),
                randomCapacityLiters(),
                randomCost());
    }

    /**
     * Creates a random vehicle name.
     *
     * @return valid vehicle name.
     */
    private static Name randomName() {
        return new Name(VEHICLE_NAME_PREFIX + "-" + ((int) (Math.random() * 10000)));
    }

    /**
     * Tests that the constructor creates a valid vehicle.
     */
    @Test
    void constructorOneRight() {
        VehicleType type = VehicleType.random();
        VehicleCapacityKilograms capacityKg = randomCapacityKilograms();
        VehicleCapacityLiters capacityL = randomCapacityLiters();
        TransportationVariableCost cost = randomCost();
        Vehicle vehicle = new Vehicle(randomName(), type, capacityKg, capacityL, cost);
        assertEquals(type, vehicle.getVehicleType());
        assertEquals(capacityKg, vehicle.getCapacityKilograms());
        assertEquals(capacityL, vehicle.getCapacityLiters());
        assertEquals(cost, vehicle.getCostPerKilometer());
        assertNotNull(vehicle.getId());
    }

    /**
     * Tests that the constructor rejects an undefined vehicle type.
     */
    @Test
    void constructorTypeUndefined() {
        VehicleCapacityKilograms capacityKg = randomCapacityKilograms();
        VehicleCapacityLiters capacityL = randomCapacityLiters();
        TransportationVariableCost cost = randomCost();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vehicle(randomName(), null, capacityKg, capacityL, cost));
        assertEquals(Vehicle.TYPE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects undefined capacity in kilograms.
     */
    @Test
    void constructorCapacityKilogramsUndefined() {
        VehicleType type = VehicleType.random();
        VehicleCapacityLiters capacityL = randomCapacityLiters();
        TransportationVariableCost cost = randomCost();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vehicle(randomName(), type, null, capacityL, cost));
        assertEquals(Vehicle.CAPACITY_Kilograms_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects undefined capacity in liters.
     */
    @Test
    void constructorCapacityLitersUndefined() {
        VehicleType type = VehicleType.random();
        VehicleCapacityKilograms capacityKg = randomCapacityKilograms();
        TransportationVariableCost cost = randomCost();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vehicle(randomName(), type, capacityKg, null, cost));
        assertEquals(Vehicle.CAPACITY_liters_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that the constructor rejects an undefined cost per kilometer.
     */
    @Test
    void constructorCostUndefined() {
        VehicleType type = VehicleType.random();
        VehicleCapacityKilograms capacityKg = randomCapacityKilograms();
        VehicleCapacityLiters capacityL = randomCapacityLiters();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vehicle(randomName(), type, capacityKg, capacityL, null));
        assertEquals(Vehicle.COST_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests vehicle equality semantics.
     */
    @Test
    void equalsMethod() {
        Vehicle vehicle1 = randomVehicle();
        Vehicle vehicle2 = randomVehicle();
        assertTrue(vehicle1.equals(vehicle1));
        assertFalse(vehicle1.equals(null));
        assertFalse(vehicle1.equals(Integer.valueOf(0)));
        assertNotEquals(vehicle1, vehicle2);
    }

    /**
     * Tests vehicle hash code semantics.
     */
    @Test
    void hashCodeMethod() {
        Vehicle vehicle1 = randomVehicle();
        Vehicle vehicle2 = randomVehicle();
        assertEquals(vehicle1.hashCode(), vehicle1.hashCode());
        assertNotEquals(vehicle1.hashCode(), vehicle2.hashCode());
    }

    /**
     * Tests that vehicle type can be updated with a valid value.
     */
    @Test
    void updateVehicleTypeValid() {
        Vehicle vehicle = randomVehicle();
        VehicleType newType = VehicleType.random();
        vehicle.updateVehicleType(newType);
        assertEquals(newType, vehicle.getVehicleType());
    }

    /**
     * Tests that vehicle type update rejects an undefined value.
     */
    @Test
    void updateVehicleTypeUndefined() {
        Vehicle vehicle = randomVehicle();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicle.updateVehicleType(null));
        assertEquals(Vehicle.TYPE_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that capacity in kilograms can be updated with a valid value.
     */
    @Test
    void updateCapacityKilogramsValid() {
        Vehicle vehicle = randomVehicle();
        VehicleCapacityKilograms newCapacityKg = new VehicleCapacityKilograms(6000.0);
        vehicle.updateCapacityKilograms(newCapacityKg);
        assertEquals(newCapacityKg, vehicle.getCapacityKilograms());
    }

    /**
     * Tests that capacity in kilograms update rejects an undefined value.
     */
    @Test
    void updateCapacityKilogramsUndefined() {
        Vehicle vehicle = randomVehicle();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicle.updateCapacityKilograms(null));
        assertEquals(Vehicle.CAPACITY_Kilograms_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that capacity in liters can be updated with a valid value.
     */
    @Test
    void updateCapacityLitersValid() {
        Vehicle vehicle = randomVehicle();
        VehicleCapacityLiters newCapacityL = new VehicleCapacityLiters(10000.0);
        vehicle.updateCapacityLiters(newCapacityL);
        assertEquals(newCapacityL, vehicle.getCapacityLiters());
    }

    /**
     * Tests that capacity in liters update rejects an undefined value.
     */
    @Test
    void updateCapacityLitersUndefined() {
        Vehicle vehicle = randomVehicle();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicle.updateCapacityLiters(null));
        assertEquals(Vehicle.CAPACITY_liters_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests that cost per kilometer can be updated with a valid value.
     */
    @Test
    void updateCostPerKilometerValid() {
        Vehicle vehicle = randomVehicle();
        TransportationVariableCost newCost = new TransportationVariableCost(1.25);
        vehicle.updateCostPerKilometer(newCost);
        assertEquals(newCost, vehicle.getCostPerKilometer());
    }

    /**
     * Tests that cost per kilometer update rejects an undefined value.
     */
    @Test
    void updateCostPerKilometerUndefined() {
        Vehicle vehicle = randomVehicle();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicle.updateCostPerKilometer(null));
        assertEquals(Vehicle.COST_NOT_DEFINED, exception.getMessage());
    }

    /**
     * Tests the vehicle string representation.
     */
    @Test
    void toStringMethod() {
        Vehicle vehicle = randomVehicle();
        String expected = String.format(
                VEHICLE_TO_STRING_TEMPLATE,
                vehicle.getId(),
                vehicle.getName(),
                vehicle.getVehicleType(),
                vehicle.getCapacityKilograms(),
                vehicle.getCapacityLiters(),
                vehicle.getCostPerKilometer());
        assertEquals(expected, vehicle.toString());
    }
}
