package com.ull.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.ull.domain.DeliveryPlanningSolution;
import com.ull.domain.entity.Alert;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.ContainerDailyState;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.entity.Vehicle;
import com.ull.domain.enumerate.WasteType;
import com.ull.domain.enums.ContainerStatus;
import com.ull.domain.valueobject.cost.MaximumBudget;
import com.ull.domain.valueobject.location.Location;

class DeliveryPlanningSolutionToJsonTest {

  @Test
  void shouldSerializeContainerStateMonitoringAndStopDetails() {
    DeliveryPlanningSolution solution = new DeliveryPlanningSolution();
    solution.updateStatus(DeliveryPlanningSolution.Status.SUBOPTIMAL);
    solution.updateMaxBudget(new MaximumBudget(5000.0, "EUR"));

    Location facilityLocation = new Location(28.4636, -16.2518, "Santa Cruz de Tenerife", "SC-001");
    Facility facility = new Facility("TRANSFER_STATION", facilityLocation, 1500.0, 600.0, 30, 250.0, "OPEN");
    FacilityCluster cluster = new FacilityCluster(facility);

    Container container = new Container(
        new Location(28.4698, -16.2574, "First Container", "C-001"),
        WasteType.ORGANIC,
        3200.0,
        180.0,
        "ZONE-1");
    cluster.addContainer(container);
    solution.addCluster(cluster);

    Vehicle vehicle = new Vehicle("LIGHT_TRUCK", 1200.0, 15.5, 0.75);
    DailyPlan dailyPlan = new DailyPlan(1, LocalDate.of(2026, 4, 27), facility, vehicle);
    dailyPlan.addStop(
        container,
        25.0,
        80.0,
        90.0,
        List.of(new Alert("VEHICLE_FULL", "Vehicle capacity reached.", 1200.0)));
    solution.addDailyPlan(dailyPlan);

    solution.addContainerDailyState(new ContainerDailyState(
        container.getId(),
        1,
        90.0,
        100.0,
        10.0,
        ContainerStatus.OVERFLOWED));

    JSONObject json = new DeliveryPlanningSolutionToJson().apply(solution);

    assertEquals("SUBOPTIMAL", json.getString("status"));
    assertTrue(json.has("containerStateMonitoring"));

    JSONArray monitoring = json.getJSONArray("containerStateMonitoring");
    assertEquals(1, monitoring.length());
    JSONObject stateJson = monitoring.getJSONObject(0);
    assertEquals(container.getId(), stateJson.getString("containerId"));
    assertEquals(1, stateJson.getInt("planDay"));
    assertEquals(90.0, stateJson.getDouble("dailyFillingLiters"));
    assertEquals(10.0, stateJson.getDouble("dailyDemandLitersPerDay"));
    assertEquals("OVERFLOWED", stateJson.getString("status"));

    JSONArray dailyPlans = json.getJSONArray("dailyPlans");
    JSONObject stopJson = dailyPlans.getJSONObject(0).getJSONArray("stops").getJSONObject(0);
    assertEquals(90.0, stopJson.getDouble("containerActualLiters"));
    assertEquals(1, stopJson.getJSONArray("alerts").length());
    assertEquals("VEHICLE_FULL", stopJson.getJSONArray("alerts").getJSONObject(0).getString("type"));
    assertEquals(80.0, stopJson.getDouble("collectedLiters"));
  }
}