package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.name.Name;

class VehicleTests {

	// Helpers
	private static VehicleCapacityKilograms randomCapacityKilograms() {
		return new VehicleCapacityKilograms(5000.0);
	}

	private static VehicleCapacityLiters randomCapacityLiters() {
		return new VehicleCapacityLiters(8000.0);
	}

	private static TransportationVariableCost randomCost() {
		return new TransportationVariableCost(0.75);
	}

	private static Vehicle randomVehicle() {
		return new Vehicle(randomName(), VehicleType.random(), randomCapacityKilograms(), randomCapacityLiters(), randomCost());
	}

	private static Name randomName() {
		return new Name("vehicle-" + ((int) (Math.random() * 10000)));
	}

	// ========== Constructors ==========

	@Test
	void constructor_1_right() {
		VehicleType type = VehicleType.random();
		VehicleCapacityKilograms capacityKg = randomCapacityKilograms();
		VehicleCapacityLiters capacityL = randomCapacityLiters();
		TransportationVariableCost cost = randomCost();

		Vehicle v = new Vehicle(randomName(), type, capacityKg, capacityL, cost);

		assertEquals(type, v.getVehicleType());
		assertEquals(capacityKg, v.getCapacityKilograms());
		assertEquals(capacityL, v.getCapacityLiters());
		assertEquals(cost, v.getCostPerKilometer());
		assertNotNull(v.getId());
	}

	@Test
	void constructor_1_type_undefined() {
		VehicleType type = null;
		VehicleCapacityKilograms capacityKg = randomCapacityKilograms();
		VehicleCapacityLiters capacityL = randomCapacityLiters();
		TransportationVariableCost cost = randomCost();

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Vehicle(randomName(), type, capacityKg, capacityL, cost)
		);
		assertEquals(Vehicle.TYPE_NOT_DEFINED, exception.getMessage());
	}

	@Test
	void constructor_1_capacity_Kilograms_undefined() {
		VehicleType type = VehicleType.random();
		VehicleCapacityKilograms capacityKg = null;
		VehicleCapacityLiters capacityL = randomCapacityLiters();
		TransportationVariableCost cost = randomCost();

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Vehicle(randomName(), type, capacityKg, capacityL, cost)
		);
		assertEquals(Vehicle.CAPACITY_Kilograms_NOT_DEFINED, exception.getMessage());
	}

	@Test
	void constructor_1_capacity_liters_undefined() {
		VehicleType type = VehicleType.random();
		VehicleCapacityKilograms capacityKg = randomCapacityKilograms();
		VehicleCapacityLiters capacityL = null;
		TransportationVariableCost cost = randomCost();

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Vehicle(randomName(), type, capacityKg, capacityL, cost)
		);
		assertEquals(Vehicle.CAPACITY_liters_NOT_DEFINED, exception.getMessage());
	}

	@Test
	void constructor_1_cost_undefined() {
		VehicleType type = VehicleType.random();
		VehicleCapacityKilograms capacityKg = randomCapacityKilograms();
		VehicleCapacityLiters capacityL = randomCapacityLiters();
		TransportationVariableCost cost = null;

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Vehicle(randomName(), type, capacityKg, capacityL, cost)
		);
		assertEquals(Vehicle.COST_NOT_DEFINED, exception.getMessage());
	}

	// ========== equals & hashCode ==========

	@Test
	void equalsMethod() {
		Vehicle v1 = randomVehicle();
		Vehicle v2 = randomVehicle();

		assertTrue(v1.equals(v1));
		assertFalse(v1.equals(null));
		assertFalse(v1.equals(Integer.valueOf(0)));
		assertNotEquals(v1, v2);
	}

	@Test
	void hashCodeMethod() {
		Vehicle v1 = randomVehicle();
		Vehicle v2 = randomVehicle();

		assertEquals(v1.hashCode(), v1.hashCode());
		assertNotEquals(v1.hashCode(), v2.hashCode());
	}

	// ========== State modifiers ==========

	@Test
	void updateVehicleType_valid() {
		Vehicle v = randomVehicle();
		VehicleType newType = VehicleType.random();
		v.updateVehicleType(newType);
		assertEquals(newType, v.getVehicleType());
	}

	@Test
	void updateVehicleType_undefined() {
		Vehicle v = randomVehicle();
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> v.updateVehicleType(null)
		);
		assertEquals(Vehicle.TYPE_NOT_DEFINED, exception.getMessage());
	}

	@Test
	void updateCapacityKilograms_valid() {
		Vehicle v = randomVehicle();
		VehicleCapacityKilograms newCapKg = new VehicleCapacityKilograms(6000.0);
		v.updateCapacityKilograms(newCapKg);
		assertEquals(newCapKg, v.getCapacityKilograms());
	}

	@Test
	void updateCapacityKilograms_undefined() {
		Vehicle v = randomVehicle();
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> v.updateCapacityKilograms(null)
		);
		assertEquals(Vehicle.CAPACITY_Kilograms_NOT_DEFINED, exception.getMessage());
	}

	@Test
	void updateCapacityLiters_valid() {
		Vehicle v = randomVehicle();
		VehicleCapacityLiters newCapL = new VehicleCapacityLiters(10000.0);
		v.updateCapacityLiters(newCapL);
		assertEquals(newCapL, v.getCapacityLiters());
	}

	@Test
	void updateCapacityLiters_undefined() {
		Vehicle v = randomVehicle();
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> v.updateCapacityLiters(null)
		);
		assertEquals(Vehicle.CAPACITY_liters_NOT_DEFINED, exception.getMessage());
	}

	@Test
	void updateCostPerKilometer_valid() {
		Vehicle v = randomVehicle();
		TransportationVariableCost newCost = new TransportationVariableCost(1.25);
		v.updateCostPerKilometer(newCost);
		assertEquals(newCost, v.getCostPerKilometer());
	}

	@Test
	void updateCostPerKilometer_undefined() {
		Vehicle v = randomVehicle();
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> v.updateCostPerKilometer(null)
		);
		assertEquals(Vehicle.COST_NOT_DEFINED, exception.getMessage());
	}

	// ========== toString ==========

	@Test
	void toStringMethod() {
		Vehicle v = randomVehicle();
		String expected = String.format("Vehicle={id=%s, name=%s, type=%s, capacityKilograms=%s, CapacityLiters=%s, costPerKm=%s}", v.getId(), v.getName(), v.getVehicleType(), v.getCapacityKilograms(), v.getCapacityLiters(), v.getCostPerKilometer());
		assertEquals(expected, v.toString());
	}
}
