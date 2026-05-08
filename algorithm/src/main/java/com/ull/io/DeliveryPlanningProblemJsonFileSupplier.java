package com.ull.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityWithVehicles;
import com.ull.domain.entity.Vehicle;
import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.cost.MaximumBudget;
import com.ull.domain.valueobject.location.Location;

/**
 * Translates the incoming JSON into a fully typed {@link DeliveryPlanningProblem}.
 *
 * <p>Expected top-level JSON keys:
 * <ul>
 *   <li>{@code averagePickupTimeMinutes} – int</li>
 *   <li>{@code numberOfDays} – int</li>
 *   <li>{@code maxBudget} – optional object with {@code amount} and {@code currency}</li>
 *   <li>{@code facilitiesWithVehicles} – array of facility+vehicle groups</li>
 *   <li>{@code selectedContainers} – array of container objects</li>
 * </ul>
 */
public class DeliveryPlanningProblemJsonFileSupplier {

  /**
   * Parses the JSON object and returns a stream containing the single
   * {@link DeliveryPlanningProblem} built from it.
   *
   * @param json the raw JSON received by the algorithm
   * @return a stream with the parsed problem
   */
  public Stream<DeliveryPlanningProblem> get(JSONObject json) {
    int averagePickupTimeMinutes = json.getInt("averagePickupTimeMinutes");
    int numberOfDays = json.getInt("numberOfDays");
    MaximumBudget maxBudget = parseMaximumBudget(json.optJSONObject("maxBudget"));

    // Support explicit error when incoming JSON uses ID-only shorthand
    // (e.g. "facilitiesWithVehicles" entries with "facilityId" or
    // top-level "selectedContainerIds"). Those formats must be expanded by
    // the backend before invoking the algorithm since the algorithm needs
    // full objects (including coordinates) to compute distances.
    if (json.has("selectedContainerIds") || containsFacilityIdOnly(json)) {
      throw new IllegalArgumentException(
        "Input JSON uses ID-only shorthand (selectedContainerIds or facilityId). "
          + "The algorithm requires fully expanded objects: 'selectedContainers' and 'facilitiesWithVehicles' with full 'facility' and 'selectedVehicles' objects.");
    }

    List<FacilityWithVehicles> facilitiesWithVehicles =
      parseFacilitiesWithVehicles(json.getJSONArray("facilitiesWithVehicles"));

    List<Container> containers =
      parseContainers(json.getJSONArray("selectedContainers"));

    DeliveryPlanningProblem problem = new DeliveryPlanningProblem(
        averagePickupTimeMinutes,
        numberOfDays,
        facilitiesWithVehicles,
      containers,
      maxBudget);

    return Stream.of(problem);
  }

  /**
   * Returns true if the incoming JSON contains facilitiesWithVehicles entries
   * that use the ID-only shorthand ("facilityId") instead of a full
   * "facility" object.
   */
  private boolean containsFacilityIdOnly(JSONObject json) {
    if (!json.has("facilitiesWithVehicles")) {
      return false;
    }
    try {
      org.json.JSONArray array = json.getJSONArray("facilitiesWithVehicles");
      for (int i = 0; i < array.length(); i++) {
        org.json.JSONObject entry = array.getJSONObject(i);
        if (entry.has("facilityId") && !entry.has("facility")) {
          return true;
        }
      }
    } catch (Exception e) {
      // If any error occurs while peeking, assume it's not the id-only format
    }
    return false;
  }

  // -------------------------------------------------------------------------
  // Private parsing helpers
  // -------------------------------------------------------------------------

  private List<FacilityWithVehicles> parseFacilitiesWithVehicles(JSONArray array) {
    List<FacilityWithVehicles> result = new ArrayList<>();
    for (int i = 0; i < array.length(); i++) {
      JSONObject entry = array.getJSONObject(i);
      Facility facility = parseFacility(entry.getJSONObject("facility"));
      List<Vehicle> vehicles = parseVehicles(entry.getJSONArray("selectedVehicles"));
      result.add(new FacilityWithVehicles(facility, vehicles));
    }
    return result;
  }

  private Facility parseFacility(JSONObject json) {
    String id = json.getString("id");
    String facilityType = json.getString("facilityType");
    String status = json.getString("status");

    Location location = parseLocation(json.getJSONObject("location"));

    double storageCapacity = readDoubleFlexible(json, "storageCapacity", "value");
    double processingCapacity = readDoubleFlexible(json, "processingCapacity", "value");
    int unloadingTime = readIntFlexible(json, "unloadingTime", "timeValue");
    double openingFixedCost = readDoubleFlexible(json, "openingFixedCost", "amount");
    double currentFillingLevel = readDoubleFlexible(json, "currentFillingLevel", "wasteDemandValue");

    return new Facility(
        id,
        facilityType,
        location,
        storageCapacity,
        processingCapacity,
        unloadingTime,
        openingFixedCost,
        status,
        currentFillingLevel);
  }

  private List<Vehicle> parseVehicles(JSONArray array) {
    List<Vehicle> result = new ArrayList<>();
    for (int i = 0; i < array.length(); i++) {
      result.add(parseVehicle(array.getJSONObject(i)));
    }
    return result;
  }

  private Vehicle parseVehicle(JSONObject json) {
    String id = json.getString("id");
    String vehicleType = json.getString("vehicleType");
    double capacityKilograms = readDoubleFlexible(json, "capacityKilograms", "Kilograms");
    double capacityLiters = readDoubleFlexible(json, "capacityLiters", "liters");
    double costPerKilometer = readDoubleFlexible(json, "costPerKilometer", "amount");

    return new Vehicle(id, vehicleType, capacityKilograms, capacityLiters, costPerKilometer);
  }

  private List<Container> parseContainers(JSONArray array) {
    List<Container> result = new ArrayList<>();
    for (int i = 0; i < array.length(); i++) {
      result.add(parseContainer(array.getJSONObject(i)));
    }
    return result;
  }

  private MaximumBudget parseMaximumBudget(JSONObject json) {
    if (json == null) {
      return null;
    }

    double amount = json.getDouble("amount");
    String currency = json.optString("currency", "EUR");
    return new MaximumBudget(amount, currency);
  }

  private Container parseContainer(JSONObject json) {
    String id = json.getString("id");
    String serviceZone = json.optString("serviceZone", null);
    if (serviceZone != null && serviceZone.isBlank()) {
      serviceZone = null;
    }

    WasteType wasteType = WasteType.valueOf(json.getString("wasteType"));
    Location location = parseLocation(json.getJSONObject("location"));

    double capacityLiters = readDoubleFlexible(json, "capacityLiters", "liters");
    double dailyDemandLitersPerDay =
      readDoubleFlexible(json, "dailyDemandLitersPerDay", "litersPerDay");

    return new Container(id, location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone);
  }

  private Location parseLocation(JSONObject json) {
    double latitude = json.getDouble("latitude");
    double longitude = json.getDouble("longitude");
    String postalAddress = json.getString("postalAddress");
    String gisReference = json.getString("gisReference");
    return new Location(latitude, longitude, postalAddress, gisReference);
  }

  private double readDoubleFlexible(JSONObject json, String fieldName, String nestedField) {
    Object value = json.get(fieldName);
    if (value instanceof JSONObject objectValue) {
      return objectValue.getDouble(nestedField);
    }
    if (value instanceof Number numberValue) {
      return numberValue.doubleValue();
    }
    throw new IllegalArgumentException("Invalid numeric field: " + fieldName);
  }

  private int readIntFlexible(JSONObject json, String fieldName, String nestedField) {
    Object value = json.get(fieldName);
    if (value instanceof JSONObject objectValue) {
      return objectValue.getInt(nestedField);
    }
    if (value instanceof Number numberValue) {
      return numberValue.intValue();
    }
    throw new IllegalArgumentException("Invalid integer field: " + fieldName);
  }
}
