package com.ull.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.location.Location;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class DailyPlanTest {

  @Test
  void shouldCreateEmptyDailyPlan() {
    DailyPlan dailyPlan = new DailyPlan(
        1,
        LocalDate.of(2026, 4, 26),
        new Facility(
            "TRANSFER_STATION",
            new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
            1500.0,
            600.0,
            30,
            250.0,
            "OPEN"),
        new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, 0.75));

    assertEquals(1, dailyPlan.getPlanDay());
    assertEquals(LocalDate.of(2026, 4, 26), dailyPlan.getServiceDate());
    assertEquals(0.0, dailyPlan.getTotalDistanceMeters());
    assertEquals(0.0, dailyPlan.getTotalCollectedKilograms());
    assertEquals(0.0, dailyPlan.getTotalCollectedLiters());
    assertTrue(dailyPlan.getStops().isEmpty());
    assertNull(dailyPlan.getLastContainer());
  }

  @Test
  void shouldAddStopsAndAccumulateMetrics() {
    Facility facility = new Facility(
        "TRANSFER_STATION",
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        "OPEN");

    DailyPlan dailyPlan = new DailyPlan(
        2,
        LocalDate.of(2026, 4, 26),
        facility,
        new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, 0.75));

    Container firstContainer = new Container(
        new Location(28.4698, -16.2574, "First Container", "C-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        "ZONE-1");

    Container secondContainer = new Container(
        new Location(28.4874, -16.3159, "Second Container", "C-002"),
        WasteType.ORGANIC,
        2800.0,
        120.0,
        "ZONE-1");

    dailyPlan.addStop(firstContainer, 90.0, 350.0);
    dailyPlan.addStop(secondContainer, 60.0, 200.0);

    List<DailyPlanStop> stops = dailyPlan.getStops();

    assertEquals(2, stops.size());
    assertEquals(secondContainer, dailyPlan.getLastContainer());
    assertEquals(1, stops.get(0).getSequence());
    assertEquals(2, stops.get(1).getSequence());
    assertEquals(facility.calculateDistanceTo(firstContainer), stops.get(0).getDistanceFromPreviousMeters(), 0.000001);
    assertEquals(firstContainer.calculateDistanceTo(secondContainer), stops.get(1).getDistanceFromPreviousMeters(), 0.000001);
    assertEquals(stops.get(0).getDistanceFromPreviousMeters() + stops.get(1).getDistanceFromPreviousMeters(),
        dailyPlan.getTotalDistanceMeters(),
        0.000001);
    assertEquals(dailyPlan.getTotalDistanceMeters(), stops.get(1).getCumulativeDistanceMeters(), 0.000001);
    assertEquals(150.0, dailyPlan.getTotalCollectedKilograms());
    assertEquals(550.0, dailyPlan.getTotalCollectedLiters());
  }

  @Test
  void shouldClearStopsAndResetMetrics() {
    DailyPlan dailyPlan = new DailyPlan(
        3,
        LocalDate.of(2026, 4, 26),
        new Facility(
            "TRANSFER_STATION",
            new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
            1500.0,
            600.0,
            30,
            250.0,
            "OPEN"),
        new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, 0.75));

    dailyPlan.addStop(
        new Container(
            new Location(28.4698, -16.2574, "First Container", "C-001"),
            WasteType.ORGANIC,
            3200.0,
            180.0,
            "ZONE-1"),
        90.0,
        350.0);

    dailyPlan.clearStops();

    assertEquals(0.0, dailyPlan.getTotalDistanceMeters());
    assertEquals(0.0, dailyPlan.getTotalCollectedKilograms());
    assertEquals(0.0, dailyPlan.getTotalCollectedLiters());
    assertTrue(dailyPlan.getStops().isEmpty());
    assertNull(dailyPlan.getLastContainer());
  }

  @Test
  void shouldRejectInvalidDailyPlanData() {
    Facility facility = new Facility(
        "TRANSFER_STATION",
        new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
        1500.0,
        600.0,
        30,
        250.0,
        "OPEN");

    Vehicle vehicle = new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, 0.75);

    assertThrows(IllegalArgumentException.class, () -> new DailyPlan(0, LocalDate.of(2026, 4, 26), facility, vehicle));
    assertThrows(IllegalArgumentException.class, () -> new DailyPlan(1, null, facility, vehicle));
    assertThrows(IllegalArgumentException.class, () -> new DailyPlan(1, LocalDate.of(2026, 4, 26), null, vehicle));
    assertThrows(IllegalArgumentException.class, () -> new DailyPlan(1, LocalDate.of(2026, 4, 26), facility, null));
  }

  @Test
  void shouldRejectInvalidStops() {
    DailyPlan dailyPlan = new DailyPlan(
        1,
        LocalDate.of(2026, 4, 26),
        new Facility(
            "TRANSFER_STATION",
            new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001"),
            1500.0,
            600.0,
            30,
            250.0,
            "OPEN"),
        new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, 0.75));

    Container container = new Container(
        new Location(28.4698, -16.2574, "First Container", "C-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        "ZONE-1");

    assertThrows(IllegalArgumentException.class, () -> dailyPlan.addStop(null, 10.0, 20.0));
    assertThrows(IllegalArgumentException.class, () -> dailyPlan.addStop(container, -1.0, 20.0));
    assertThrows(IllegalArgumentException.class, () -> dailyPlan.addStop(container, 10.0, -1.0));
  }
}
