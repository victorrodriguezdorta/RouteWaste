package com.ull.domain.entity;

import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.location.Location;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ContainerTest {

  private static final String ZONE_1 = "ZONE-1";
  private static final String ZONE_2 = "ZONE-2";
  private static final String ZONE_3 = "ZONE-3";
  private static final String TRANSFER_STATION_TYPE = "TRANSFER_STATION";
  private static final String OPEN_STATUS = "OPEN";
  private static final double MIN_EXPECTED_DISTANCE_METERS = 6000.0;
  private static final double MAX_EXPECTED_DISTANCE_METERS = 8500.0;
  private static final double ZERO_DISTANCE_DELTA = 0.000001;

  /**
   * Verifies that a container can be created with simplified scalar attributes.
   */
  @Test
  void shouldCreateContainerWithSimplifiedAttributes() {
    Location location = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");
    Container container = new Container(location, WasteType.ORGANIC, 3200.0, 180.0, ZONE_1);
    assertNotNull(container.getId());
    assertEquals(location, container.getLocation());
    assertEquals(WasteType.ORGANIC, container.getWasteType());
    assertEquals(3200.0, container.getCapacityLiters());
    assertEquals(180.0, container.getDailyDemandLitersPerDay());
    assertEquals(ZONE_1, container.getServiceZone());
    assertEquals(0.0, container.getCurrentLiters());
  }

  /**
   * Verifies distance calculation to another container and to a facility.
   */
  @Test
  void shouldCalculateDistanceToOtherContainerAndFacility() {
    Container origin = new Container(
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        ZONE_1);
    Container target = new Container(
        new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001"),
        WasteType.GLASS,
        2800.0,
        120.0,
        ZONE_2);
    Facility facility = new Facility(
        TRANSFER_STATION_TYPE,
        new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001"),
        1500.0,
        600.0,
        30,
        250.0,
        OPEN_STATUS);
    double distanceToContainer = origin.calculateDistanceTo(target);
    double distanceToFacility = origin.calculateDistanceTo(facility);
    assertTrue(distanceToContainer > MIN_EXPECTED_DISTANCE_METERS);
    assertTrue(distanceToContainer < MAX_EXPECTED_DISTANCE_METERS);
    assertEquals(0.0, target.calculateDistanceTo(facility), ZERO_DISTANCE_DELTA);
    assertEquals(distanceToContainer, distanceToFacility, ZERO_DISTANCE_DELTA);
  }

  /**
   * Verifies that container attributes can be updated after creation.
   */
  @Test
  void shouldUpdateContainerAttributes() {
    Container container = new Container(
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        ZONE_1);
    Location updatedLocation = new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001");
    container.updateLocation(updatedLocation);
    container.updateWasteType(WasteType.PACKAGING);
    container.updateCapacityLiters(3500.0);
    container.updateDailyDemandLitersPerDay(220.0);
    container.updateServiceZone(ZONE_3);
    container.addCurrentLiters(1200.0);
    assertEquals(updatedLocation, container.getLocation());
    assertEquals(WasteType.PACKAGING, container.getWasteType());
    assertEquals(3500.0, container.getCapacityLiters());
    assertEquals(220.0, container.getDailyDemandLitersPerDay());
    assertEquals(ZONE_3, container.getServiceZone());
    assertEquals(1200.0, container.getCurrentLiters());
  }

  /**
   * Verifies that current liters can be accumulated and emptied.
   */
  @Test
  void shouldAccumulateAndEmptyCurrentLiters() {
    Container container = new Container(
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        ZONE_1);
    container.addCurrentLiters(800.0);
    container.addCurrentLiters(200.0);
    assertEquals(1000.0, container.getCurrentLiters());
    container.removeCurrentLiters(250.0);
    assertEquals(750.0, container.getCurrentLiters());
    container.emptyCurrentLiters();
    assertEquals(0.0, container.getCurrentLiters());
  }

  /**
   * Verifies that invalid container data is rejected at construction time.
   */
  @Test
  void shouldRejectInvalidContainerData() {
    Location location = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");
    assertThrows(IllegalArgumentException.class,
        () -> new Container(null, WasteType.ORGANIC, 3200.0, 180.0, ZONE_1));
    assertThrows(IllegalArgumentException.class,
        () -> new Container(location, null, 3200.0, 180.0, ZONE_1));
    assertThrows(IllegalArgumentException.class,
        () -> new Container(location, WasteType.ORGANIC, -1.0, 180.0, ZONE_1));
    assertThrows(IllegalArgumentException.class,
        () -> new Container(location, WasteType.ORGANIC, 3200.0, -1.0, ZONE_1));
  }

  /**
   * Verifies that current liters cannot exceed capacity or become negative.
   */
  @Test
  void shouldRejectCurrentLitersThatExceedCapacityOrGoNegative() {
    Container container = new Container(
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        ZONE_1);
    assertThrows(IllegalArgumentException.class, () -> container.updateCurrentLiters(-1.0));
    assertThrows(IllegalArgumentException.class, () -> container.updateCurrentLiters(3300.0));
    container.addCurrentLiters(2000.0);
    assertThrows(IllegalArgumentException.class, () -> container.addCurrentLiters(1300.0));
    assertThrows(IllegalArgumentException.class, () -> container.removeCurrentLiters(2100.0));
    assertThrows(IllegalArgumentException.class, () -> container.updateCapacityLiters(1500.0));
  }
}
