package com.ull.io;

import com.ull.domain.DeliveryPlanningSolution;
import com.ull.domain.DeliveryPlanningStatus;
import com.ull.domain.entity.Alert;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.ContainerDailyState;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.entity.Vehicle;
import com.ull.domain.enumerate.ContainerStatus;
import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.cost.MaximumBudget;
import com.ull.domain.valueobject.location.Location;
import java.time.LocalDate;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class DeliveryPlanningSolutionToJsonTest {

  private static final String STATUS_FIELD = "status";
  private static final String SUBOPTIMAL_STATUS = "SUBOPTIMAL";
  private static final String CONTAINER_STATE_MONITORING_FIELD = "containerStateMonitoring";
  private static final String CONTAINER_ID_FIELD = "containerId";
  private static final String PLAN_DAY_FIELD = "planDay";
  private static final String DAILY_FILLING_LITERS_FIELD = "dailyFillingLiters";
  private static final String DAILY_FILLING_LITERS_BEFORE_COLLECTION_FIELD =
      "dailyFillingLitersBeforeCollection";
  private static final String DAILY_DEMAND_LITERS_PER_DAY_FIELD = "dailyDemandLitersPerDay";
  private static final String OVERFLOWED_STATUS = "OVERFLOWED";
  private static final String DAILY_PLANS_FIELD = "dailyPlans";
  private static final String STOPS_FIELD = "stops";
  private static final String CONTAINER_ACTUAL_LITERS_FIELD = "containerActualLiters";
  private static final String ALERTS_FIELD = "alerts";
  private static final String TYPE_FIELD = "type";
  private static final String VEHICLE_FULL_ALERT_TYPE = "VEHICLE_FULL";
  private static final String COLLECTED_LITERS_FIELD = "collectedLiters";

  /**
   * Tests serialization of container state monitoring and stop details to JSON.
   */
  @Test
  void shouldSerializeContainerStateMonitoringAndStopDetails() {
    DeliveryPlanningSolution solution = new DeliveryPlanningSolution();
    solution.updateStatus(DeliveryPlanningStatus.SUBOPTIMAL);
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
        List.of(new Alert(VEHICLE_FULL_ALERT_TYPE, "Vehicle capacity reached.", 1200.0)));
    solution.addDailyPlan(dailyPlan);
    solution.addContainerDailyState(new ContainerDailyState(
        container.getId(),
        1,
        90.0,
        100.0,
        10.0,
        ContainerStatus.OVERFLOWED));
    JSONObject json = new DeliveryPlanningSolutionToJson().apply(solution);
    assertEquals(SUBOPTIMAL_STATUS, json.getString(STATUS_FIELD));
    assertTrue(json.has(CONTAINER_STATE_MONITORING_FIELD));
    JSONArray monitoring = json.getJSONArray(CONTAINER_STATE_MONITORING_FIELD);
    assertEquals(1, monitoring.length());
    JSONObject stateJson = monitoring.getJSONObject(0);
    assertEquals(container.getId(), stateJson.getString(CONTAINER_ID_FIELD));
    assertEquals(1, stateJson.getInt(PLAN_DAY_FIELD));
    assertEquals(90.0, stateJson.getDouble(DAILY_FILLING_LITERS_FIELD));
    assertTrue(stateJson.has(DAILY_FILLING_LITERS_BEFORE_COLLECTION_FIELD));
    assertEquals(10.0, stateJson.getDouble(DAILY_DEMAND_LITERS_PER_DAY_FIELD));
    assertEquals(OVERFLOWED_STATUS, stateJson.getString(STATUS_FIELD));
    JSONArray dailyPlans = json.getJSONArray(DAILY_PLANS_FIELD);
    JSONObject stopJson = dailyPlans.getJSONObject(0).getJSONArray(STOPS_FIELD).getJSONObject(0);
    assertEquals(90.0, stopJson.getDouble(CONTAINER_ACTUAL_LITERS_FIELD));
    assertEquals(1, stopJson.getJSONArray(ALERTS_FIELD).length());
    assertEquals(VEHICLE_FULL_ALERT_TYPE, stopJson.getJSONArray(ALERTS_FIELD).getJSONObject(0).getString(TYPE_FIELD));
    assertEquals(80.0, stopJson.getDouble(COLLECTED_LITERS_FIELD));
  }
}
