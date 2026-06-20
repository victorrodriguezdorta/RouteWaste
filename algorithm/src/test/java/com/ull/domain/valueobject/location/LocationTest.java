package com.ull.domain.valueobject.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class LocationTest {

  private static final double MIN_EXPECTED_DISTANCE_METERS = 6000.0;
  private static final double MAX_EXPECTED_DISTANCE_METERS = 8500.0;
  private static final double ZERO_DISTANCE_DELTA = 0.000001;

  /**
   * Verifies that the haversine distance between two known locations falls in an expected range.
   */
  @Test
  void shouldCalculateDistanceBetweenTwoLocations() {
    Location santaCruz = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");
    Location laLaguna = new Location(28.4874, -16.3159, "San Cristobal de La Laguna", "LL-001");
    double distance = santaCruz.calculateDistanceTo(laLaguna);
    assertTrue(distance > MIN_EXPECTED_DISTANCE_METERS);
    assertTrue(distance < MAX_EXPECTED_DISTANCE_METERS);
  }

  /**
   * Verifies that invalid coordinates and blank metadata are rejected.
   */
  @Test
  void shouldRejectInvalidCoordinatesOrBlankMetadata() {
    assertThrows(IllegalArgumentException.class, () -> new Location(100.0, -16.2518, "A", "GIS-1"));
    assertThrows(IllegalArgumentException.class, () -> new Location(28.4636, -200.0, "A", "GIS-1"));
    assertThrows(IllegalArgumentException.class, () -> new Location(28.4636, -16.2518, " ", "GIS-1"));
    assertThrows(IllegalArgumentException.class, () -> new Location(28.4636, -16.2518, "A", " "));
  }

  /**
   * Verifies that the distance from a location to itself is zero.
   */
  @Test
  void shouldReturnZeroDistanceForSameLocation() {
    Location location = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");
    assertEquals(0.0, location.calculateDistanceTo(location), ZERO_DISTANCE_DELTA);
  }
}
