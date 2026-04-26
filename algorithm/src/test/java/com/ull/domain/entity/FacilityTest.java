package com.ull.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ull.domain.valueobject.location.Location;
import org.junit.jupiter.api.Test;

class FacilityTest {

  @Test
  void shouldCreateFacilityWithSimplifiedAttributes() {
    Location location = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");

    Facility facility = new Facility("TRANSFER_STATION", location, 1500.0, 600.0, 30, 250.0, "OPEN");

    assertNotNull(facility.getId());
    assertEquals("TRANSFER_STATION", facility.getFacilityType());
    assertEquals(location, facility.getLocation());
    assertEquals(1500.0, facility.getStorageCapacityKilograms());
    assertEquals(600.0, facility.getProcessingCapacityKilogramsPerDay());
    assertEquals(30.0, facility.getUnloadingTimeMinutes());
    assertEquals(250.0, facility.getOpeningFixedCost());
    assertEquals("OPEN", facility.getStatus());
    assertEquals(0.0, facility.getCurrentFillingLevel());
    assertEquals(0.0, facility.getCurrentStoredKilograms());
  }

  @Test
  void shouldCalculateDistanceBetweenFacilities() {
    Facility origin = new Facility(
        "TRANSFER_STATION",
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        "OPEN");

    Facility target = new Facility(
        "PLANT",
        new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001"),
        2000.0,
        800.0,
        35,
        320.0,
        "OPEN");

    double distance = origin.calculateDistanceTo(target);

    assertTrue(distance > 6000.0);
    assertTrue(distance < 8500.0);
  }

  @Test
  void shouldCalculateDistanceToContainer() {
    Facility facility = new Facility(
        "TRANSFER_STATION",
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        "OPEN");

    Container container = new Container(
        new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001"),
        com.ull.domain.enumerate.WasteType.GLASS,
        2800.0,
        120.0,
        "ZONE-2");

    double distance = facility.calculateDistanceTo(container);

    assertTrue(distance > 6000.0);
    assertTrue(distance < 8500.0);
  }

  @Test
  void shouldUpdateFacilityAttributes() {
    Facility facility = new Facility(
        "TRANSFER_STATION",
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        "OPEN");

    Location updatedLocation = new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001");

    facility.updateFacilityType("PLANT");
    facility.updateLocation(updatedLocation);
    facility.updateStorageCapacityKilograms(2500.0);
    facility.updateProcessingCapacityKilogramsPerDay(900.0);
    facility.updateUnloadingTimeMinutes(40);
    facility.updateOpeningFixedCost(500.0);
    facility.updateStatus("PLANNED");
    facility.updateCurrentFillingLevel(120.0);
    facility.addStoredKilograms(400.0);

    assertEquals("PLANT", facility.getFacilityType());
    assertEquals(updatedLocation, facility.getLocation());
    assertEquals(2500.0, facility.getStorageCapacityKilograms());
    assertEquals(900.0, facility.getProcessingCapacityKilogramsPerDay());
    assertEquals(40.0, facility.getUnloadingTimeMinutes());
    assertEquals(500.0, facility.getOpeningFixedCost());
    assertEquals("PLANNED", facility.getStatus());
    assertEquals(120.0, facility.getCurrentFillingLevel());
    assertEquals(400.0, facility.getCurrentStoredKilograms());
  }

  @Test
  void shouldAccumulateAndEmptyStoredKilograms() {
    Facility facility = new Facility(
        "TRANSFER_STATION",
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        "OPEN");

    facility.addStoredKilograms(300.0);
    facility.addStoredKilograms(200.0);
    assertEquals(500.0, facility.getCurrentStoredKilograms());

    facility.removeStoredKilograms(150.0);
    assertEquals(350.0, facility.getCurrentStoredKilograms());

    facility.emptyStoredKilograms();
    assertEquals(0.0, facility.getCurrentStoredKilograms());
  }

  @Test
  void shouldRejectInvalidFacilityData() {
    Location location = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");

    assertThrows(IllegalArgumentException.class,
        () -> new Facility(" ", location, 1500.0, 600.0, 30, 250.0, "OPEN"));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility("TRANSFER_STATION", null, 1500.0, 600.0, 30, 250.0, "OPEN"));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility("TRANSFER_STATION", location, -1.0, 600.0, 30, 250.0, "OPEN"));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility("TRANSFER_STATION", location, 1500.0, -1.0, 30, 250.0, "OPEN"));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility("TRANSFER_STATION", location, 1500.0, 600.0, -1, 250.0, "OPEN"));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility("TRANSFER_STATION", location, 1500.0, 600.0, 30, -1.0, "OPEN"));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility("TRANSFER_STATION", location, 1500.0, 600.0, 30, 250.0, " "));
  }

  @Test
  void shouldRejectStoredKilogramsThatExceedCapacityOrGoNegative() {
    Facility facility = new Facility(
        "TRANSFER_STATION",
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        "OPEN");

    assertThrows(IllegalArgumentException.class, () -> facility.updateCurrentStoredKilograms(-1.0));
    assertThrows(IllegalArgumentException.class, () -> facility.updateCurrentStoredKilograms(1600.0));

    facility.addStoredKilograms(1000.0);

    assertThrows(IllegalArgumentException.class, () -> facility.addStoredKilograms(600.0));
    assertThrows(IllegalArgumentException.class, () -> facility.removeStoredKilograms(1100.0));
    assertThrows(IllegalArgumentException.class, () -> facility.updateStorageCapacityKilograms(900.0));
  }
}
