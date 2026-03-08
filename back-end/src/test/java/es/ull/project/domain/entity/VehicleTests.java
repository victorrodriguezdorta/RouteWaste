package es.ull.project.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import es.ull.project.domain.enumerate.TimeUnit;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;

class VehicleTests {

	// Helpers
	private static Capacity randomCapacity() {
		return new Capacity(5000.0, new QuantityUnit("kg"), TimeUnit.DAY);
	}

	private static TransportationVariableCost randomCost() {
		return new TransportationVariableCost(0.75);
	}

	private static Vehicle randomVehicle() {
		return new Vehicle(VehicleType.random(), randomCapacity(), randomCost());
	}

	// ========== Constructors ==========

	@Test
	void constructor_1_right() {
		VehicleType type = VehicleType.random();
		Capacity capacity = randomCapacity();
		TransportationVariableCost cost = randomCost();

		Vehicle v = new Vehicle(type, capacity, cost);

		assertEquals(type, v.getVehicleType());
		assertEquals(capacity, v.getTransportCapacity());
		assertEquals(cost, v.getCostPerKilometer());
		assertNotNull(v.getId());
	}

	@Test
	void constructor_1_type_undefined() {
		VehicleType type = null;
		Capacity capacity = randomCapacity();
		TransportationVariableCost cost = randomCost();

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Vehicle(type, capacity, cost)
		);
		assertEquals(Vehicle.TYPE_NOT_DEFINED, exception.getMessage());
	}

	@Test
	void constructor_1_capacity_undefined() {
		VehicleType type = VehicleType.random();
		Capacity capacity = null;
		TransportationVariableCost cost = randomCost();

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Vehicle(type, capacity, cost)
		);
		assertEquals(Vehicle.CAPACITY_NOT_DEFINED, exception.getMessage());
	}

	@Test
	void constructor_1_cost_undefined() {
		VehicleType type = VehicleType.random();
		Capacity capacity = randomCapacity();
		TransportationVariableCost cost = null;

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Vehicle(type, capacity, cost)
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
	void updateTransportCapacity_valid() {
		Vehicle v = randomVehicle();
		Capacity newCap = new Capacity(6000.0, new QuantityUnit("kg"), TimeUnit.DAY);
		v.updateTransportCapacity(newCap);
		assertEquals(newCap, v.getTransportCapacity());
	}

	@Test
	void updateTransportCapacity_undefined() {
		Vehicle v = randomVehicle();
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> v.updateTransportCapacity(null)
		);
		assertEquals(Vehicle.CAPACITY_NOT_DEFINED, exception.getMessage());
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
		String expected = String.format("Vehicle={id=%s, type=%s, capacity=%s, costPerKm=%s}", v.getId(), v.getVehicleType(), v.getTransportCapacity(), v.getCostPerKilometer());
		assertEquals(expected, v.toString());
	}
}
