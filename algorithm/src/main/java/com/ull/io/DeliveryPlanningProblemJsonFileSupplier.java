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

    double storageCapacity = json.getJSONObject("storageCapacity").getDouble("value");
    double processingCapacity = json.getJSONObject("processingCapacity").getDouble("value");
    int unloadingTime = json.getJSONObject("unloadingTime").getInt("timeValue");
    double openingFixedCost = json.getJSONObject("openingFixedCost").getDouble("amount");
    double currentFillingLevel = json.getJSONObject("currentFillingLevel").getDouble("wasteDemandValue");

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
    double capacityKilograms = json.getJSONObject("capacityKilograms").getDouble("Kilograms");
    double capacityLiters = json.getJSONObject("CapacityLiters").getDouble("liters");
    double costPerKilometer = json.getJSONObject("costPerKilometer").getDouble("amount");

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
    String serviceZone = json.getString("serviceZone");

    WasteType wasteType = WasteType.valueOf(json.getString("wasteType"));
    Location location = parseLocation(json.getJSONObject("location"));

    double capacityLiters = json.getJSONObject("capacityLiters").getDouble("liters");
    double dailyDemandLitersPerDay =
        json.getJSONObject("dailyDemandLitersPerDay").getDouble("litersPerDay");

    return new Container(id, location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone);
  }

  private Location parseLocation(JSONObject json) {
    double latitude = json.getDouble("latitude");
    double longitude = json.getDouble("longitude");
    String postalAddress = json.getString("postalAddress");
    String gisReference = json.getString("gisReference");
    return new Location(latitude, longitude, postalAddress, gisReference);
  }
}
