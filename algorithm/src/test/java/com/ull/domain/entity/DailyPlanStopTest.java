package com.ull.domain.entity;

import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.location.Location;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class DailyPlanStopTest {

  /**
   * Tests daily plan stop creation with valid data.
   */
  @Test
  void shouldCreateDailyPlanStop() {
    Container container = new Container(
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        "ZONE-1");
    DailyPlanStop stop = new DailyPlanStop(1, container, 1200.0, 1200.0, 80.0, 300.0);
    assertEquals(1, stop.getSequence());
    assertEquals(container, stop.getContainer());
    assertEquals(1200.0, stop.getDistanceFromPreviousMeters());
    assertEquals(1200.0, stop.getCumulativeDistanceMeters());
    assertEquals(80.0, stop.getCollectedKilograms());
    assertEquals(300.0, stop.getCollectedLiters());
  }

  /**
   * Tests that invalid daily plan stop data is rejected.
   */
  @Test
  void shouldRejectInvalidDailyPlanStopData() {
    Container container = new Container(
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        "ZONE-1");
    assertThrows(IllegalArgumentException.class, () -> new DailyPlanStop(0, container, 1200.0, 1200.0, 80.0, 300.0));
    assertThrows(IllegalArgumentException.class, () -> new DailyPlanStop(1, null, 1200.0, 1200.0, 80.0, 300.0));
    assertThrows(IllegalArgumentException.class, () -> new DailyPlanStop(1, container, -1.0, 1200.0, 80.0, 300.0));
    assertThrows(IllegalArgumentException.class, () -> new DailyPlanStop(1, container, 1200.0, -1.0, 80.0, 300.0));
    assertThrows(IllegalArgumentException.class, () -> new DailyPlanStop(1, container, 1200.0, 1200.0, -1.0, 300.0));
    assertThrows(IllegalArgumentException.class, () -> new DailyPlanStop(1, container, 1200.0, 1200.0, 80.0, -1.0));
  }
}
