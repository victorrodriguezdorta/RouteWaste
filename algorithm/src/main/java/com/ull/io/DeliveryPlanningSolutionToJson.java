package com.ull.io;

import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ull.domain.DeliveryPlanningSolution;
import com.ull.domain.entity.Alert;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.ContainerDailyState;
import com.ull.domain.entity.DailyPlan;
import com.ull.domain.entity.DailyPlanStop;
import com.ull.domain.enumerate.StopType;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityCluster;
import com.ull.domain.entity.Vehicle;
import com.ull.domain.valueobject.cost.MaximumBudget;

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

  @Override
  public JSONObject apply(DeliveryPlanningSolution solution) {
    JSONObject json = new JSONObject();

    json.put("status", solution.getStatus().name());
    json.put("executedAt", solution.getExecutedAt().toString());
    json.put("totalDistanceMeters", solution.getTotalDistanceMeters());
    json.put("totalCollectedKilograms", solution.getTotalCollectedKilograms());
    json.put("totalCollectedLiters", solution.getTotalCollectedLiters());
    if (solution.getMaxBudget() != null) {
      json.put("maxBudget", serializeMaximumBudget(solution.getMaxBudget()));
    }
    json.put("clusters", serializeClusters(solution));
    json.put("dailyPlans", serializeDailyPlans(solution));
    json.put("containerStateMonitoring", serializeContainerStateMonitoring(solution));

    return json;
  }

  // -------------------------------------------------------------------------
  // Clusters
  // -------------------------------------------------------------------------

  private JSONArray serializeClusters(DeliveryPlanningSolution solution) {
    JSONArray array = new JSONArray();
    for (FacilityCluster cluster : solution.getClusters()) {
      array.put(serializeCluster(cluster));
    }
    return array;
  }

  private JSONObject serializeCluster(FacilityCluster cluster) {
    JSONObject json = new JSONObject();
    json.put("facility", serializeFacilitySummary(cluster.getFacility()));
    json.put("assignedContainers", serializeContainerSummaries(cluster.getAssignedContainers()));
    return json;
  }

  // -------------------------------------------------------------------------
  // Daily plans
  // -------------------------------------------------------------------------

  private JSONArray serializeDailyPlans(DeliveryPlanningSolution solution) {
    JSONArray array = new JSONArray();
    for (DailyPlan plan : solution.getDailyPlans()) {
      array.put(serializeDailyPlan(plan));
    }
    return array;
  }

  private JSONObject serializeDailyPlan(DailyPlan plan) {
    JSONObject json = new JSONObject();
    json.put("planDay", plan.getPlanDay());
    json.put("serviceDate", plan.getServiceDate().toString());
    json.put("facilityId", plan.getOriginFacility().getId());
    json.put("vehicle", serializeVehicleSummary(plan.getVehicle()));
    json.put("totalDistanceMeters", plan.getTotalDistanceMeters());
    json.put("totalCollectedKilograms", plan.getTotalCollectedKilograms());
    json.put("totalCollectedLiters", plan.getTotalCollectedLiters());
    json.put("stops", serializeStops(plan));
    return json;
  }

  private JSONArray serializeStops(DailyPlan plan) {
    JSONArray array = new JSONArray();
    for (DailyPlanStop stop : plan.getStops()) {
      array.put(serializeStop(stop));
    }
    return array;
  }

  private JSONObject serializeStop(DailyPlanStop stop) {
    JSONObject json = new JSONObject();
    json.put("sequence", stop.getSequence());
    json.put("type", stop.getType() != null ? stop.getType().name() : JSONObject.NULL);
    json.put("distanceFromPreviousMeters", stop.getDistanceFromPreviousMeters());
    json.put("cumulativeDistanceMeters", stop.getCumulativeDistanceMeters());
    json.put("collectedKilograms", stop.getCollectedKilograms());
    json.put("collectedLiters", stop.getCollectedLiters());
    json.put("containerActualLiters", stop.getContainerActualLiters());
    if (stop.getType() == StopType.CONTAINER && stop.getContainer() != null) {
      json.put("containerId", stop.getContainer().getId());
    } else {
      json.put("containerId", JSONObject.NULL);
    }
    json.put("alerts", serializeAlerts(stop.getAlerts()));
    return json;
  }

  private JSONArray serializeAlerts(java.util.List<Alert> alerts) {
    JSONArray array = new JSONArray();
    for (Alert alert : alerts) {
      JSONObject json = new JSONObject();
      json.put("type", alert.getType());
      json.put("message", alert.getMessage());
      if (alert.getValue() != null) {
        json.put("value", alert.getValue());
      }
      array.put(json);
    }
    return array;
  }

  private JSONArray serializeContainerStateMonitoring(DeliveryPlanningSolution solution) {
    JSONArray array = new JSONArray();
    for (ContainerDailyState state : solution.getContainerStateMonitoring()) {
      JSONObject json = new JSONObject();
      json.put("containerId", state.getContainerId());
      json.put("planDay", state.getPlanDay());
      json.put("dailyFillingLiters", state.getDailyFillingLiters());
      json.put("containerCapacityLiters", state.getContainerCapacityLiters());
      json.put("dailyDemandLitersPerDay", state.getDailyDemandLitersPerDay());
      json.put("status", state.getStatus().name());
      array.put(json);
    }
    return array;
  }

  // -------------------------------------------------------------------------
  // Shared summary helpers
  // -------------------------------------------------------------------------

  private JSONObject serializeFacilitySummary(Facility facility) {
    JSONObject json = new JSONObject();
    json.put("id", facility.getId());
    json.put("facilityType", facility.getFacilityType());
    json.put("status", facility.getStatus());
    json.put("location", serializeLocation(facility));
    return json;
  }

  private JSONArray serializeContainerSummaries(java.util.List<Container> containers) {
    JSONArray array = new JSONArray();
    for (Container container : containers) {
      array.put(serializeContainerSummary(container));
    }
    return array;
  }

  private JSONObject serializeContainerSummary(Container container) {
    JSONObject json = new JSONObject();
    json.put("id", container.getId());
    json.put("wasteType", container.getWasteType().name());
    json.put("serviceZone", container.getServiceZone());
    json.put("capacityLiters", container.getCapacityLiters());
    json.put("dailyDemandLitersPerDay", container.getDailyDemandLitersPerDay());
    json.put("location", serializeContainerLocation(container));
    return json;
  }

  private JSONObject serializeVehicleSummary(Vehicle vehicle) {
    JSONObject json = new JSONObject();
    json.put("id", vehicle.getId());
    json.put("vehicleType", vehicle.getVehicleType());
    json.put("capacityKilograms", vehicle.getCapacityKilograms());
    json.put("capacityLiters", vehicle.getCapacityLiters());
    json.put("costPerKilometer", vehicle.getCostPerKilometer());
    return json;
  }

  private JSONObject serializeMaximumBudget(MaximumBudget maxBudget) {
    JSONObject json = new JSONObject();
    json.put("amount", maxBudget.getAmount());
    json.put("currency", maxBudget.getCurrency());
    return json;
  }

  private JSONObject serializeLocation(Facility facility) {
    JSONObject json = new JSONObject();
    json.put("latitude", facility.getLocation().getLatitude());
    json.put("longitude", facility.getLocation().getLongitude());
    json.put("postalAddress", facility.getLocation().getPostalAddress());
    json.put("gisReference", facility.getLocation().getGisReference());
    return json;
  }

  private JSONObject serializeContainerLocation(Container container) {
    JSONObject json = new JSONObject();
    json.put("latitude", container.getLocation().getLatitude());
    json.put("longitude", container.getLocation().getLongitude());
    json.put("postalAddress", container.getLocation().getPostalAddress());
    json.put("gisReference", container.getLocation().getGisReference());
    return json;
  }
}