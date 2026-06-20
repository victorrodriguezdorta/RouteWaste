package com.ull.domain.entity;

import com.ull.domain.enumerate.WasteType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class VehicleTest {

  private static final String LIGHT_TRUCK_TYPE = "LIGHT_TRUCK";
  private static final String HEAVY_TRUCK_TYPE = "HEAVY_TRUCK";
  private static final double DEFAULT_CAPACITY_KILOGRAMS = 1200.0;
  private static final double DEFAULT_CAPACITY_LITERS = 15.5;
  private static final double DEFAULT_COST_PER_KILOMETER = 0.75;

  /**
   * Verifies that a vehicle can be created with simplified scalar attributes.
   */
  @Test
  void shouldCreateVehicleWithSimplifiedAttributes() {
    Vehicle vehicle = new Vehicle(LIGHT_TRUCK_TYPE, DEFAULT_CAPACITY_KILOGRAMS, DEFAULT_CAPACITY_LITERS, DEFAULT_COST_PER_KILOMETER);
    assertNotNull(vehicle.getId());
    assertEquals(LIGHT_TRUCK_TYPE, vehicle.getVehicleType());
    assertEquals(DEFAULT_CAPACITY_KILOGRAMS, vehicle.getCapacityKilograms());
    assertEquals(DEFAULT_CAPACITY_LITERS, vehicle.getCapacityLiters());
    assertEquals(DEFAULT_COST_PER_KILOMETER, vehicle.getCostPerKilometer());
    assertEquals(0.0, vehicle.getCurrentLoadKilograms());
    assertEquals(0.0, vehicle.getCurrentLoadLiters());
    assertEquals(null, vehicle.getCurrentWasteType());
  }

  /**
   * Verifies that vehicle attributes can be updated after creation.
   */
  @Test
  void shouldUpdateVehicleAttributes() {
    Vehicle vehicle = new Vehicle(LIGHT_TRUCK_TYPE, DEFAULT_CAPACITY_KILOGRAMS, DEFAULT_CAPACITY_LITERS, DEFAULT_COST_PER_KILOMETER);
    vehicle.updateVehicleType(HEAVY_TRUCK_TYPE);
    vehicle.updateCapacityKilograms(1800.0);
    vehicle.updateCapacityLiters(20.0);
    vehicle.updateCostPerKilometer(1.10);
    assertEquals(HEAVY_TRUCK_TYPE, vehicle.getVehicleType());
    assertEquals(1800.0, vehicle.getCapacityKilograms());
    assertEquals(20.0, vehicle.getCapacityLiters());
    assertEquals(1.10, vehicle.getCostPerKilometer());
  }

  /**
   * Verifies that load kilograms and liters can be accumulated and emptied.
   */
  @Test
  void shouldAccumulateAndEmptyVehicleLoad() {
    Vehicle vehicle = new Vehicle(LIGHT_TRUCK_TYPE, DEFAULT_CAPACITY_KILOGRAMS, DEFAULT_CAPACITY_LITERS, DEFAULT_COST_PER_KILOMETER);
    vehicle.updateCurrentWasteType(WasteType.ORGANIC);
    vehicle.addLoadKilograms(300.0);
    vehicle.addLoadKilograms(150.0);
    vehicle.addLoadLiters(4.0);
    vehicle.addLoadLiters(1.5);
    assertEquals(WasteType.ORGANIC, vehicle.getCurrentWasteType());
    assertEquals(450.0, vehicle.getCurrentLoadKilograms());
    assertEquals(5.5, vehicle.getCurrentLoadLiters());
    vehicle.removeLoadKilograms(50.0);
    vehicle.removeLoadLiters(0.5);
    assertEquals(400.0, vehicle.getCurrentLoadKilograms());
    assertEquals(5.0, vehicle.getCurrentLoadLiters());
    vehicle.emptyLoad();
    assertEquals(0.0, vehicle.getCurrentLoadKilograms());
    assertEquals(0.0, vehicle.getCurrentLoadLiters());
    assertEquals(null, vehicle.getCurrentWasteType());
  }

  /**
   * Verifies that negative capacities or cost per kilometer are rejected.
   */
  @Test
  void shouldRejectNegativeCapacitiesOrCost() {
    assertThrows(IllegalArgumentException.class, () -> new Vehicle(LIGHT_TRUCK_TYPE, -1.0, DEFAULT_CAPACITY_LITERS, DEFAULT_COST_PER_KILOMETER));
    assertThrows(IllegalArgumentException.class, () -> new Vehicle(LIGHT_TRUCK_TYPE, DEFAULT_CAPACITY_KILOGRAMS, -1.0, DEFAULT_COST_PER_KILOMETER));
    assertThrows(IllegalArgumentException.class, () -> new Vehicle(LIGHT_TRUCK_TYPE, DEFAULT_CAPACITY_KILOGRAMS, DEFAULT_CAPACITY_LITERS, -0.1));
  }

  /**
   * Verifies that blank vehicle type or identifier are rejected.
   */
  @Test
  void shouldRejectBlankTypeOrId() {
    assertThrows(IllegalArgumentException.class, () -> new Vehicle(" ", DEFAULT_CAPACITY_KILOGRAMS, DEFAULT_CAPACITY_LITERS, DEFAULT_COST_PER_KILOMETER));
    assertThrows(IllegalArgumentException.class, () -> new Vehicle(" ", LIGHT_TRUCK_TYPE, DEFAULT_CAPACITY_KILOGRAMS, DEFAULT_CAPACITY_LITERS, DEFAULT_COST_PER_KILOMETER));
  }

  /**
   * Verifies that vehicle loads cannot exceed capacity or become negative.
   */
  @Test
  void shouldRejectVehicleLoadsThatExceedCapacityOrGoNegative() {
    Vehicle vehicle = new Vehicle(LIGHT_TRUCK_TYPE, DEFAULT_CAPACITY_KILOGRAMS, DEFAULT_CAPACITY_LITERS, DEFAULT_COST_PER_KILOMETER);
    assertThrows(IllegalArgumentException.class, () -> vehicle.updateCurrentLoadKilograms(-1.0));
    assertThrows(IllegalArgumentException.class, () -> vehicle.updateCurrentLoadLiters(-1.0));
    assertThrows(IllegalArgumentException.class, () -> vehicle.updateCurrentLoadKilograms(1300.0));
    assertThrows(IllegalArgumentException.class, () -> vehicle.updateCurrentLoadLiters(20.0));
    vehicle.addLoadKilograms(1000.0);
    vehicle.addLoadLiters(10.0);
    assertThrows(IllegalArgumentException.class, () -> vehicle.addLoadKilograms(300.0));
    assertThrows(IllegalArgumentException.class, () -> vehicle.addLoadLiters(6.0));
    assertThrows(IllegalArgumentException.class, () -> vehicle.removeLoadKilograms(1100.0));
    assertThrows(IllegalArgumentException.class, () -> vehicle.removeLoadLiters(11.0));
    assertThrows(IllegalArgumentException.class, () -> vehicle.updateCapacityKilograms(900.0));
    assertThrows(IllegalArgumentException.class, () -> vehicle.updateCapacityLiters(9.0));
  }
}
