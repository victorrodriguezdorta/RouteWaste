package com.ull.io;

import com.ull.domain.DeliveryPlanningSolution;
import com.ull.domain.entity.Alert;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.ContainerDailyState;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.DailyPlanStop;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.entity.Vehicle;
import com.ull.domain.enumerate.StopType;
import com.ull.domain.valueobject.cost.MaximumBudget;
import java.util.List;
import java.util.function.Function;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Serializes a {@link DeliveryPlanningSolution} into a JSON object ready to be
 * returned by the algorithm and consumed by the backend.
 *
 * <p>Output JSON structure:
 * <pre>
 * {
 *   "status": "OPTIMAL",
 *   "executedAt": "2026-04-26T15:00:00Z",
 *   "totalDistanceMeters": 12400.0,
 *   "totalCollectedKilograms": 350.0,
 *   "totalCollectedLiters": 420.0,
 *   "clusters": [
 *     {
 *       "facility": { "id": "...", "facilityType": "...", "location": { ... } },
 *       "assignedContainers": [ { "id": "...", "wasteType": "...", "location": { ... } }, ... ]
 *     }
 *   ],
 *   "dailyPlans": [
 *     {
 *       "planDay": 1,
 *       "serviceDate": "2026-04-27",
 *       "facilityId": "...",
 *       "vehicle": { "id": "...", "vehicleType": "..." },
 *       "totalDistanceMeters": 4200.0,
 *       "totalCollectedKilograms": 120.0,
 *       "totalCollectedLiters": 145.0,
 *       "stops": [
 *         {
 *           "sequence": 1,
 *           "containerId": "...",
 *           "distanceFromPreviousMeters": 850.0,
 *           "cumulativeDistanceMeters": 850.0,
 *           "collectedKilograms": 40.0,
 *           "collectedLiters": 48.0,
 *           "containerActualLiters": 60.0,
 *           "alerts": []
 *         }
 *       ]
 *     }
 *   ]
 *   "containerStateMonitoring": [
 *     {
 *       "containerId": "...",
 *       "planDay": 1,
 *       "dailyFillingLiters": 12.0,
 *       "containerCapacityLiters": 100.0,
 *       "dailyDemandLitersPerDay": 12.0,
 *       "status": "CORRECT"
 *     }
 *   ]
 * }
 * </pre>
 */
public class DeliveryPlanningSolutionToJson
    implements Function<DeliveryPlanningSolution, JSONObject> {
  public static final String STATUS_FIELD = "status";
  public static final String EXECUTED_AT_FIELD = "executedAt";
  public static final String TOTAL_DISTANCE_METERS_FIELD = "totalDistanceMeters";
  public static final String TOTAL_COLLECTED_KILOGRAMS_FIELD = "totalCollectedKilograms";
  public static final String TOTAL_COLLECTED_LITERS_FIELD = "totalCollectedLiters";
  public static final String MAX_BUDGET_FIELD = "maxBudget";
  public static final String CLUSTERS_FIELD = "clusters";
  public static final String DAILY_PLANS_FIELD = "dailyPlans";
  public static final String CONTAINER_STATE_MONITORING_FIELD = "containerStateMonitoring";
  public static final String FACILITY_FIELD = "facility";
  public static final String ASSIGNED_CONTAINERS_FIELD = "assignedContainers";
  public static final String PLAN_DAY_FIELD = "planDay";
  public static final String SERVICE_DATE_FIELD = "serviceDate";
  public static final String FACILITY_ID_FIELD = "facilityId";
  public static final String VEHICLE_FIELD = "vehicle";
  public static final String STOPS_FIELD = "stops";
  public static final String SEQUENCE_FIELD = "sequence";
  public static final String TYPE_FIELD = "type";
  public static final String DISTANCE_FROM_PREVIOUS_METERS_FIELD = "distanceFromPreviousMeters";
  public static final String CUMULATIVE_DISTANCE_METERS_FIELD = "cumulativeDistanceMeters";
  public static final String COLLECTED_KILOGRAMS_FIELD = "collectedKilograms";
  public static final String COLLECTED_LITERS_FIELD = "collectedLiters";
  public static final String CONTAINER_ACTUAL_LITERS_FIELD = "containerActualLiters";
  public static final String CONTAINER_ID_FIELD = "containerId";
  public static final String ALERTS_FIELD = "alerts";
  public static final String MESSAGE_FIELD = "message";
  public static final String VALUE_FIELD = "value";
  public static final String DAILY_FILLING_LITERS_FIELD = "dailyFillingLiters";
  public static final String DAILY_FILLING_LITERS_BEFORE_COLLECTION_FIELD =
      "dailyFillingLitersBeforeCollection";
  public static final String CONTAINER_CAPACITY_LITERS_FIELD = "containerCapacityLiters";
  public static final String DAILY_DEMAND_LITERS_PER_DAY_FIELD = "dailyDemandLitersPerDay";
  public static final String ID_FIELD = "id";
  public static final String FACILITY_TYPE_FIELD = "facilityType";
  public static final String LOCATION_FIELD = "location";
  public static final String WASTE_TYPE_FIELD = "wasteType";
  public static final String SERVICE_ZONE_FIELD = "serviceZone";
  public static final String CAPACITY_LITERS_FIELD = "capacityLiters";
  public static final String VEHICLE_TYPE_FIELD = "vehicleType";
  public static final String CAPACITY_KILOGRAMS_FIELD = "capacityKilograms";
  public static final String COST_PER_KILOMETER_FIELD = "costPerKilometer";
  public static final String AMOUNT_FIELD = "amount";
  public static final String CURRENCY_FIELD = "currency";
  public static final String LATITUDE_FIELD = "latitude";
  public static final String LONGITUDE_FIELD = "longitude";
  public static final String POSTAL_ADDRESS_FIELD = "postalAddress";
  public static final String GIS_REFERENCE_FIELD = "gisReference";

  /**
   * Converts a delivery planning solution into its JSON representation.
   *
   * @param solution solution to serialize
   * @return JSON object with the complete solution payload
   */
  @Override
  public JSONObject apply(DeliveryPlanningSolution solution) {
    JSONObject json = new JSONObject();
    json.put(STATUS_FIELD, solution.getStatus().name());
    json.put(EXECUTED_AT_FIELD, solution.getExecutedAt().toString());
    json.put(TOTAL_DISTANCE_METERS_FIELD, solution.getTotalDistanceMeters());
    json.put(TOTAL_COLLECTED_KILOGRAMS_FIELD, solution.getTotalCollectedKilograms());
    json.put(TOTAL_COLLECTED_LITERS_FIELD, solution.getTotalCollectedLiters());
    if (solution.getMaxBudget() != null) {
      json.put(MAX_BUDGET_FIELD, serializeMaximumBudget(solution.getMaxBudget()));
    }
    json.put(CLUSTERS_FIELD, serializeClusters(solution));
    json.put(DAILY_PLANS_FIELD, serializeDailyPlans(solution));
    json.put(CONTAINER_STATE_MONITORING_FIELD, serializeContainerStateMonitoring(solution));
    return json;
  }

  /**
   * Serializes all facility clusters in the solution.
   *
   * @param solution solution containing the clusters
   * @return JSON array with serialized clusters
   */
  private JSONArray serializeClusters(DeliveryPlanningSolution solution) {
    JSONArray array = new JSONArray();
    for (FacilityCluster cluster : solution.getClusters()) {
      array.put(serializeCluster(cluster));
    }
    return array;
  }

  /**
   * Serializes a facility cluster with its facility and assigned containers.
   *
   * @param cluster cluster to serialize
   * @return JSON object with cluster information
   */
  private JSONObject serializeCluster(FacilityCluster cluster) {
    JSONObject json = new JSONObject();
    json.put(FACILITY_FIELD, serializeFacilitySummary(cluster.getFacility()));
    json.put(ASSIGNED_CONTAINERS_FIELD, serializeContainerSummaries(cluster.getAssignedContainers()));
    return json;
  }

  /**
   * Serializes all daily plans in the solution.
   *
   * @param solution solution containing daily plans
   * @return JSON array with serialized daily plans
   */
  private JSONArray serializeDailyPlans(DeliveryPlanningSolution solution) {
    JSONArray array = new JSONArray();
    for (DailyPlan plan : solution.getDailyPlans()) {
      array.put(serializeDailyPlan(plan));
    }
    return array;
  }

  /**
   * Serializes one daily route plan.
   *
   * @param plan daily plan to serialize
   * @return JSON object with daily plan information
   */
  private JSONObject serializeDailyPlan(DailyPlan plan) {
    JSONObject json = new JSONObject();
    json.put(PLAN_DAY_FIELD, plan.getPlanDay());
    json.put(SERVICE_DATE_FIELD, plan.getServiceDate().toString());
    json.put(FACILITY_ID_FIELD, plan.getOriginFacility().getId());
    json.put(VEHICLE_FIELD, serializeVehicleSummary(plan.getVehicle()));
    json.put(TOTAL_DISTANCE_METERS_FIELD, plan.getTotalDistanceMeters());
    json.put(TOTAL_COLLECTED_KILOGRAMS_FIELD, plan.getTotalCollectedKilograms());
    json.put(TOTAL_COLLECTED_LITERS_FIELD, plan.getTotalCollectedLiters());
    json.put(STOPS_FIELD, serializeStops(plan));
    return json;
  }

  /**
   * Serializes the stops of a daily plan.
   *
   * @param plan daily plan containing stops
   * @return JSON array with serialized stops
   */
  private JSONArray serializeStops(DailyPlan plan) {
    JSONArray array = new JSONArray();
    for (DailyPlanStop stop : plan.getStops()) {
      array.put(serializeStop(stop));
    }
    return array;
  }

  /**
   * Serializes a daily plan stop.
   *
   * @param stop stop to serialize
   * @return JSON object with stop information
   */
  private JSONObject serializeStop(DailyPlanStop stop) {
    JSONObject json = new JSONObject();
    json.put(SEQUENCE_FIELD, stop.getSequence());
    json.put(TYPE_FIELD, stop.getType() != null ? stop.getType().name() : JSONObject.NULL);
    json.put(DISTANCE_FROM_PREVIOUS_METERS_FIELD, stop.getDistanceFromPreviousMeters());
    json.put(CUMULATIVE_DISTANCE_METERS_FIELD, stop.getCumulativeDistanceMeters());
    json.put(COLLECTED_KILOGRAMS_FIELD, stop.getCollectedKilograms());
    json.put(COLLECTED_LITERS_FIELD, stop.getCollectedLiters());
    json.put(CONTAINER_ACTUAL_LITERS_FIELD, stop.getContainerActualLiters());
    if (stop.getType() == StopType.CONTAINER && stop.getContainer() != null) {
      json.put(CONTAINER_ID_FIELD, stop.getContainer().getId());
    } else {
      json.put(CONTAINER_ID_FIELD, JSONObject.NULL);
    }
    json.put(ALERTS_FIELD, serializeAlerts(stop.getAlerts()));
    return json;
  }

  /**
   * Serializes stop alerts.
   *
   * @param alerts alerts to serialize
   * @return JSON array with serialized alerts
   */
  private JSONArray serializeAlerts(List<Alert> alerts) {
    JSONArray array = new JSONArray();
    for (Alert alert : alerts) {
      JSONObject json = new JSONObject();
      json.put(TYPE_FIELD, alert.getType());
      json.put(MESSAGE_FIELD, alert.getMessage());
      if (alert.getValue() != null) {
        json.put(VALUE_FIELD, alert.getValue());
      }
      array.put(json);
    }
    return array;
  }

  /**
   * Serializes the state monitoring values for all containers.
   *
   * @param solution solution containing container state monitoring
   * @return JSON array with serialized container states
   */
  private JSONArray serializeContainerStateMonitoring(DeliveryPlanningSolution solution) {
    JSONArray array = new JSONArray();
    for (ContainerDailyState state : solution.getContainerStateMonitoring()) {
      JSONObject json = new JSONObject();
      json.put(CONTAINER_ID_FIELD, state.getContainerId());
      json.put(PLAN_DAY_FIELD, state.getPlanDay());
      json.put(DAILY_FILLING_LITERS_FIELD, state.getDailyFillingLiters());
      json.put(
          DAILY_FILLING_LITERS_BEFORE_COLLECTION_FIELD,
          state.getDailyFillingLitersBeforeCollection());
      json.put(CONTAINER_CAPACITY_LITERS_FIELD, state.getContainerCapacityLiters());
      json.put(DAILY_DEMAND_LITERS_PER_DAY_FIELD, state.getDailyDemandLitersPerDay());
      json.put(STATUS_FIELD, state.getStatus().name());
      array.put(json);
    }
    return array;
  }

  /**
   * Serializes a compact facility summary.
   *
   * @param facility facility to summarize
   * @return JSON object with facility summary
   */
  private JSONObject serializeFacilitySummary(Facility facility) {
    JSONObject json = new JSONObject();
    json.put(ID_FIELD, facility.getId());
    json.put(FACILITY_TYPE_FIELD, facility.getFacilityType());
    json.put(STATUS_FIELD, facility.getStatus());
    json.put(LOCATION_FIELD, serializeLocation(facility));
    return json;
  }

  /**
   * Serializes compact container summaries.
   *
   * @param containers containers to summarize
   * @return JSON array with container summaries
   */
  private JSONArray serializeContainerSummaries(List<Container> containers) {
    JSONArray array = new JSONArray();
    for (Container container : containers) {
      array.put(serializeContainerSummary(container));
    }
    return array;
  }

  /**
   * Serializes a compact container summary.
   *
   * @param container container to summarize
   * @return JSON object with container summary
   */
  private JSONObject serializeContainerSummary(Container container) {
    JSONObject json = new JSONObject();
    json.put(ID_FIELD, container.getId());
    json.put(WASTE_TYPE_FIELD, container.getWasteType().name());
    json.put(SERVICE_ZONE_FIELD, container.getServiceZone());
    json.put(CAPACITY_LITERS_FIELD, container.getCapacityLiters());
    json.put(DAILY_DEMAND_LITERS_PER_DAY_FIELD, container.getDailyDemandLitersPerDay());
    json.put(LOCATION_FIELD, serializeContainerLocation(container));
    return json;
  }

  /**
   * Serializes a compact vehicle summary.
   *
   * @param vehicle vehicle to summarize
   * @return JSON object with vehicle summary
   */
  private JSONObject serializeVehicleSummary(Vehicle vehicle) {
    JSONObject json = new JSONObject();
    json.put(ID_FIELD, vehicle.getId());
    json.put(VEHICLE_TYPE_FIELD, vehicle.getVehicleType());
    json.put(CAPACITY_KILOGRAMS_FIELD, vehicle.getCapacityKilograms());
    json.put(CAPACITY_LITERS_FIELD, vehicle.getCapacityLiters());
    json.put(COST_PER_KILOMETER_FIELD, vehicle.getCostPerKilometer());
    return json;
  }

  /**
   * Serializes the maximum budget value object.
   *
   * @param maxBudget maximum budget to serialize
   * @return JSON object with amount and currency
   */
  private JSONObject serializeMaximumBudget(MaximumBudget maxBudget) {
    JSONObject json = new JSONObject();
    json.put(AMOUNT_FIELD, maxBudget.getAmount());
    json.put(CURRENCY_FIELD, maxBudget.getCurrency());
    return json;
  }

  /**
   * Serializes a facility location.
   *
   * @param facility facility containing the location
   * @return JSON object with location data
   */
  private JSONObject serializeLocation(Facility facility) {
    JSONObject json = new JSONObject();
    json.put(LATITUDE_FIELD, facility.getLocation().getLatitude());
    json.put(LONGITUDE_FIELD, facility.getLocation().getLongitude());
    json.put(POSTAL_ADDRESS_FIELD, facility.getLocation().getPostalAddress());
    json.put(GIS_REFERENCE_FIELD, facility.getLocation().getGisReference());
    return json;
  }

  /**
   * Serializes a container location.
   *
   * @param container container containing the location
   * @return JSON object with location data
   */
  private JSONObject serializeContainerLocation(Container container) {
    JSONObject json = new JSONObject();
    json.put(LATITUDE_FIELD, container.getLocation().getLatitude());
    json.put(LONGITUDE_FIELD, container.getLocation().getLongitude());
    json.put(POSTAL_ADDRESS_FIELD, container.getLocation().getPostalAddress());
    json.put(GIS_REFERENCE_FIELD, container.getLocation().getGisReference());
    return json;
  }
}