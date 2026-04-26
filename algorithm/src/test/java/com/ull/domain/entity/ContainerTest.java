package com.ull.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.location.Location;
import org.junit.jupiter.api.Test;

class ContainerTest {

  @Test
  void shouldCreateContainerWithSimplifiedAttributes() {
    Location location = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");

    Container container = new Container(location, WasteType.ORGANIC, 3200.0, 180.0, "ZONE-1");

    assertNotNull(container.getId());
    assertEquals(location, container.getLocation());
    assertEquals(WasteType.ORGANIC, container.getWasteType());
    assertEquals(3200.0, container.getCapacityLiters());
    assertEquals(180.0, container.getDailyDemandLitersPerDay());
    assertEquals("ZONE-1", container.getServiceZone());
    assertEquals(0.0, container.getCurrentLiters());
  }

  @Test
  void shouldCalculateDistanceToOtherContainerAndFacility() {
    Container origin = new Container(
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        "ZONE-1");

    Container target = new Container(
        new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001"),
        WasteType.GLASS,
        2800.0,
        120.0,
        "ZONE-2");

    Facility facility = new Facility(
        "TRANSFER_STATION",
        new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001"),
        1500.0,
        600.0,
        30,
        250.0,
        "OPEN");

    double distanceToContainer = origin.calculateDistanceTo(target);
    double distanceToFacility = origin.calculateDistanceTo(facility);

    assertTrue(distanceToContainer > 6000.0);
    assertTrue(distanceToContainer < 8500.0);
    assertEquals(0.0, target.calculateDistanceTo(facility), 0.000001);
    assertEquals(distanceToContainer, distanceToFacility, 0.000001);
  }

  @Test
  void shouldUpdateContainerAttributes() {
    Container container = new Container(
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        "ZONE-1");

    Location updatedLocation = new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001");

    container.updateLocation(updatedLocation);
    container.updateWasteType(WasteType.PACKAGING);
    container.updateCapacityLiters(3500.0);
    container.updateDailyDemandLitersPerDay(220.0);
    container.updateServiceZone("ZONE-3");
    container.addCurrentLiters(1200.0);

    assertEquals(updatedLocation, container.getLocation());
    assertEquals(WasteType.PACKAGING, container.getWasteType());
    assertEquals(3500.0, container.getCapacityLiters());
    assertEquals(220.0, container.getDailyDemandLitersPerDay());
    assertEquals("ZONE-3", container.getServiceZone());
    assertEquals(1200.0, container.getCurrentLiters());
  }

  @Test
  void shouldAccumulateAndEmptyCurrentLiters() {
    Container container = new Container(
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        "ZONE-1");

    container.addCurrentLiters(800.0);
    container.addCurrentLiters(200.0);
    assertEquals(1000.0, container.getCurrentLiters());

    container.removeCurrentLiters(250.0);
    assertEquals(750.0, container.getCurrentLiters());

    container.emptyCurrentLiters();
    assertEquals(0.0, container.getCurrentLiters());
  }

  @Test
  void shouldRejectInvalidContainerData() {
    Location location = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");

    assertThrows(IllegalArgumentException.class,
        () -> new Container(null, WasteType.ORGANIC, 3200.0, 180.0, "ZONE-1"));
    assertThrows(IllegalArgumentException.class,
        () -> new Container(location, null, 3200.0, 180.0, "ZONE-1"));
    assertThrows(IllegalArgumentException.class,
        () -> new Container(location, WasteType.ORGANIC, -1.0, 180.0, "ZONE-1"));
    assertThrows(IllegalArgumentException.class,
        () -> new Container(location, WasteType.ORGANIC, 3200.0, -1.0, "ZONE-1"));
  }

  @Test
  void shouldRejectCurrentLitersThatExceedCapacityOrGoNegative() {
    Container container = new Container(
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        "ZONE-1");

    assertThrows(IllegalArgumentException.class, () -> container.updateCurrentLiters(-1.0));
    assertThrows(IllegalArgumentException.class, () -> container.updateCurrentLiters(3300.0));

    container.addCurrentLiters(2000.0);

    assertThrows(IllegalArgumentException.class, () -> container.addCurrentLiters(1300.0));
    assertThrows(IllegalArgumentException.class, () -> container.removeCurrentLiters(2100.0));
    assertThrows(IllegalArgumentException.class, () -> container.updateCapacityLiters(1500.0));
  }
}
