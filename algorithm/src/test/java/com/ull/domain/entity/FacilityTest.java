package com.ull.domain.entity;

import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.location.Location;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class FacilityTest {

  private static final String TRANSFER_STATION_TYPE = "TRANSFER_STATION";
  private static final String PLANT_TYPE = "PLANT";
  private static final String OPEN_STATUS = "OPEN";
  private static final String PLANNED_STATUS = "PLANNED";
  private static final String SERVICE_ZONE = "ZONE-2";
  private static final double MIN_EXPECTED_DISTANCE_METERS = 6000.0;
  private static final double MAX_EXPECTED_DISTANCE_METERS = 8500.0;

  /**
   * Verifies that a facility can be created with simplified scalar attributes.
   */
  @Test
  void shouldCreateFacilityWithSimplifiedAttributes() {
    Location location = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");
    Facility facility = new Facility(TRANSFER_STATION_TYPE, location, 1500.0, 600.0, 30, 250.0, OPEN_STATUS);
    assertNotNull(facility.getId());
    assertEquals(TRANSFER_STATION_TYPE, facility.getFacilityType());
    assertEquals(location, facility.getLocation());
    assertEquals(1500.0, facility.getStorageCapacityKilograms());
    assertEquals(600.0, facility.getProcessingCapacityKilogramsPerDay());
    assertEquals(30.0, facility.getUnloadingTimeMinutes());
    assertEquals(250.0, facility.getOpeningFixedCost());
    assertEquals(OPEN_STATUS, facility.getStatus());
    assertEquals(0.0, facility.getCurrentFillingLevel());
    assertEquals(0.0, facility.getCurrentStoredKilograms());
  }

  /**
   * Verifies that distance between two facilities is calculated within an expected range.
   */
  @Test
  void shouldCalculateDistanceBetweenFacilities() {
    Facility origin = new Facility(
        TRANSFER_STATION_TYPE,
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        OPEN_STATUS);
    Facility target = new Facility(
        PLANT_TYPE,
        new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001"),
        2000.0,
        800.0,
        35,
        320.0,
        OPEN_STATUS);
    double distance = origin.calculateDistanceTo(target);
    assertTrue(distance > MIN_EXPECTED_DISTANCE_METERS);
    assertTrue(distance < MAX_EXPECTED_DISTANCE_METERS);
  }

  /**
   * Verifies that distance from a facility to a container is calculated within an expected range.
   */
  @Test
  void shouldCalculateDistanceToContainer() {
    Facility facility = new Facility(
        TRANSFER_STATION_TYPE,
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        OPEN_STATUS);
    Container container = new Container(
        new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001"),
        WasteType.GLASS,
        2800.0,
        120.0,
        SERVICE_ZONE);
    double distance = facility.calculateDistanceTo(container);
    assertTrue(distance > MIN_EXPECTED_DISTANCE_METERS);
    assertTrue(distance < MAX_EXPECTED_DISTANCE_METERS);
  }

  /**
   * Verifies that facility attributes can be updated after creation.
   */
  @Test
  void shouldUpdateFacilityAttributes() {
    Facility facility = new Facility(
        TRANSFER_STATION_TYPE,
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        OPEN_STATUS);
    Location updatedLocation = new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001");
    facility.updateFacilityType(PLANT_TYPE);
    facility.updateLocation(updatedLocation);
    facility.updateStorageCapacityKilograms(2500.0);
    facility.updateProcessingCapacityKilogramsPerDay(900.0);
    facility.updateUnloadingTimeMinutes(40);
    facility.updateOpeningFixedCost(500.0);
    facility.updateStatus(PLANNED_STATUS);
    facility.updateCurrentFillingLevel(120.0);
    facility.addStoredKilograms(400.0);
    assertEquals(PLANT_TYPE, facility.getFacilityType());
    assertEquals(updatedLocation, facility.getLocation());
    assertEquals(2500.0, facility.getStorageCapacityKilograms());
    assertEquals(900.0, facility.getProcessingCapacityKilogramsPerDay());
    assertEquals(40.0, facility.getUnloadingTimeMinutes());
    assertEquals(500.0, facility.getOpeningFixedCost());
    assertEquals(PLANNED_STATUS, facility.getStatus());
    assertEquals(120.0, facility.getCurrentFillingLevel());
    assertEquals(400.0, facility.getCurrentStoredKilograms());
  }

  /**
   * Verifies that stored kilograms can be accumulated and emptied.
   */
  @Test
  void shouldAccumulateAndEmptyStoredKilograms() {
    Facility facility = new Facility(
        TRANSFER_STATION_TYPE,
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        OPEN_STATUS);
    facility.addStoredKilograms(300.0);
    facility.addStoredKilograms(200.0);
    assertEquals(500.0, facility.getCurrentStoredKilograms());
    facility.removeStoredKilograms(150.0);
    assertEquals(350.0, facility.getCurrentStoredKilograms());
    facility.emptyStoredKilograms();
    assertEquals(0.0, facility.getCurrentStoredKilograms());
  }

  /**
   * Verifies that invalid facility data is rejected at construction time.
   */
  @Test
  void shouldRejectInvalidFacilityData() {
    Location location = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");
    assertThrows(IllegalArgumentException.class,
        () -> new Facility(" ", location, 1500.0, 600.0, 30, 250.0, OPEN_STATUS));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility(TRANSFER_STATION_TYPE, null, 1500.0, 600.0, 30, 250.0, OPEN_STATUS));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility(TRANSFER_STATION_TYPE, location, -1.0, 600.0, 30, 250.0, OPEN_STATUS));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility(TRANSFER_STATION_TYPE, location, 1500.0, -1.0, 30, 250.0, OPEN_STATUS));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility(TRANSFER_STATION_TYPE, location, 1500.0, 600.0, -1, 250.0, OPEN_STATUS));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility(TRANSFER_STATION_TYPE, location, 1500.0, 600.0, 30, -1.0, OPEN_STATUS));
    assertThrows(IllegalArgumentException.class,
        () -> new Facility(TRANSFER_STATION_TYPE, location, 1500.0, 600.0, 30, 250.0, " "));
  }

  /**
   * Verifies that stored kilograms cannot exceed capacity or become negative.
   */
  @Test
  void shouldRejectStoredKilogramsThatExceedCapacityOrGoNegative() {
    Facility facility = new Facility(
        TRANSFER_STATION_TYPE,
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        OPEN_STATUS);
    assertThrows(IllegalArgumentException.class, () -> facility.updateCurrentStoredKilograms(-1.0));
    assertThrows(IllegalArgumentException.class, () -> facility.updateCurrentStoredKilograms(1600.0));
    facility.addStoredKilograms(1000.0);
    assertThrows(IllegalArgumentException.class, () -> facility.addStoredKilograms(600.0));
    assertThrows(IllegalArgumentException.class, () -> facility.removeStoredKilograms(1100.0));
    assertThrows(IllegalArgumentException.class, () -> facility.updateStorageCapacityKilograms(900.0));
  }
}
