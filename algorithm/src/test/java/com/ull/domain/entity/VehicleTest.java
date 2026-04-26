package com.ull.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ull.domain.enumerate.WasteType;
import org.junit.jupiter.api.Test;

class VehicleTest {

  @Test
  void shouldCreateVehicleWithSimplifiedAttributes() {
    Vehicle vehicle = new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, 0.75);

    assertNotNull(vehicle.getId());
    assertEquals("LIGHT_TRUCK", vehicle.getVehicleType());
    assertEquals(1200.0, vehicle.getCapacityKilograms());
    assertEquals(15.5, vehicle.getCapacityLiters());
    assertEquals(0.75, vehicle.getCostPerKilometer());
    assertEquals(0.0, vehicle.getCurrentLoadKilograms());
    assertEquals(0.0, vehicle.getCurrentLoadLiters());
    assertEquals(null, vehicle.getCurrentWasteType());
  }

  @Test
  void shouldUpdateVehicleAttributes() {
    Vehicle vehicle = new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, 0.75);

    vehicle.updateVehicleType("HEAVY_TRUCK");
    vehicle.updateCapacityKilograms(1800.0);
    vehicle.updateCapacityLiters(20.0);
    vehicle.updateCostPerKilometer(1.10);

    assertEquals("HEAVY_TRUCK", vehicle.getVehicleType());
    assertEquals(1800.0, vehicle.getCapacityKilograms());
    assertEquals(20.0, vehicle.getCapacityLiters());
    assertEquals(1.10, vehicle.getCostPerKilometer());
  }

  @Test
  void shouldAccumulateAndEmptyVehicleLoad() {
    Vehicle vehicle = new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, 0.75);

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

  @Test
  void shouldRejectNegativeCapacitiesOrCost() {
    assertThrows(IllegalArgumentException.class, () -> new Vehicle("LIGHT_TRUCK", -1.0, 15.5, 0.75));
    assertThrows(IllegalArgumentException.class, () -> new Vehicle("LIGHT_TRUCK", 1200.0, -1.0, 0.75));
    assertThrows(IllegalArgumentException.class, () -> new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, -0.1));
  }

  @Test
  void shouldRejectBlankTypeOrId() {
    assertThrows(IllegalArgumentException.class, () -> new Vehicle(" ", 1200.0, 15.5, 0.75));
    assertThrows(IllegalArgumentException.class, () -> new Vehicle(" ", "LIGHT_TRUCK", 1200.0, 15.5, 0.75));
  }

  @Test
  void shouldRejectVehicleLoadsThatExceedCapacityOrGoNegative() {
    Vehicle vehicle = new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, 0.75);

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
