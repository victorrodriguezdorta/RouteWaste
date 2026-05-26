package com.ull.io;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.entity.Container;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.FacilityWithVehicles;
import com.ull.domain.entity.Vehicle;
import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.cost.MaximumBudget;
import com.ull.domain.valueobject.location.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;

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

  private static final String DEFAULT_CURRENCY = "EUR";
  private static final String ERROR_ID_ONLY_SHORTHAND =
      "Input JSON uses ID-only shorthand (selectedContainerIds or facilityId). "
          + "The algorithm requires fully expanded objects: 'selectedContainers' and "
          + "'facilitiesWithVehicles' with full 'facility' and 'selectedVehicles' objects.";
  private static final String ERROR_INVALID_INTEGER_FIELD = "Invalid integer field: ";
  private static final String ERROR_INVALID_NUMERIC_FIELD = "Invalid numeric field: ";
  private static final String FIELD_AMOUNT = "amount";
  private static final String FIELD_AVERAGE_PICKUP_TIME_MINUTES = "averagePickupTimeMinutes";
  private static final String FIELD_CAPACITY_KILOGRAMS = "capacityKilograms";
  private static final String FIELD_CAPACITY_LITERS = "capacityLiters";
  private static final String FIELD_COST_PER_KILOMETER = "costPerKilometer";
  private static final String FIELD_CURRENCY = "currency";
  private static final String FIELD_CURRENT_FILLING_LEVEL = "currentFillingLevel";
  private static final String FIELD_DAILY_DEMAND_LITERS_PER_DAY = "dailyDemandLitersPerDay";
  private static final String FIELD_FACILITIES_WITH_VEHICLES = "facilitiesWithVehicles";
  private static final String FIELD_FACILITY = "facility";
  private static final String FIELD_FACILITY_ID = "facilityId";
  private static final String FIELD_FACILITY_TYPE = "facilityType";
  private static final String FIELD_GIS_REFERENCE = "gisReference";
  private static final String FIELD_GIS_REFERENCE_LEGACY = "gisreference";
  private static final String FIELD_ID = "id";
  private static final String FIELD_KILOGRAMS_LOWERCASE = "kilograms";
  private static final String FIELD_KILOGRAMS = "Kilograms";
  private static final String FIELD_LATITUDE = "latitude";
  private static final String FIELD_LITERS = "liters";
  private static final String FIELD_LITERS_PER_DAY = "litersPerDay";
  private static final String FIELD_LOCATION = "location";
  private static final String FIELD_LONGITUDE = "longitude";
  private static final String FIELD_MAX_BUDGET = "maxBudget";
  private static final String FIELD_NUMBER_OF_DAYS = "numberOfDays";
  private static final String FIELD_OPENING_FIXED_COST = "openingFixedCost";
  private static final String FIELD_POSTAL_ADDRESS = "postalAddress";
  private static final String FIELD_PROCESSING_CAPACITY = "processingCapacity";
  private static final String FIELD_KILOGRAMS_PER_DAY = "kilogramsPerDay";
  private static final String FIELD_SELECTED_CONTAINER_IDS = "selectedContainerIds";
  private static final String FIELD_SELECTED_CONTAINERS = "selectedContainers";
  private static final String FIELD_SELECTED_VEHICLES = "selectedVehicles";
  private static final String FIELD_SERVICE_ZONE = "serviceZone";
  private static final String FIELD_STATUS = "status";
  private static final String FIELD_STORAGE_CAPACITY = "storageCapacity";
  private static final String FIELD_TIME_VALUE = "timeValue";
  private static final String FIELD_UNLOADING_TIME = "unloadingTime";
  private static final String FIELD_MINUTES = "minutes";
  private static final String FIELD_VALUE = "value";
  private static final String FIELD_VEHICLE_TYPE = "vehicleType";
  private static final String FIELD_WASTE_DEMAND_VALUE = "wasteDemandValue";
  private static final String FIELD_WASTE_TYPE = "wasteType";

  /**
   * Parses the JSON object and returns a stream containing the single
   * {@link DeliveryPlanningProblem} built from it.
   *
   * @param json the raw JSON received by the algorithm
   * @return a stream with the parsed problem
   */
  public Stream<DeliveryPlanningProblem> get(JSONObject json) {
    int averagePickupTimeMinutes = json.getInt(FIELD_AVERAGE_PICKUP_TIME_MINUTES);
    int numberOfDays = json.getInt(FIELD_NUMBER_OF_DAYS);
    MaximumBudget maxBudget = parseMaximumBudget(json.optJSONObject(FIELD_MAX_BUDGET));
    if (json.has(FIELD_SELECTED_CONTAINER_IDS) || containsFacilityIdOnly(json)) {
      throw new IllegalArgumentException(ERROR_ID_ONLY_SHORTHAND);
    }
    List<FacilityWithVehicles> facilitiesWithVehicles =
      parseFacilitiesWithVehicles(json.getJSONArray(FIELD_FACILITIES_WITH_VEHICLES));
    List<Container> containers =
      parseContainers(json.getJSONArray(FIELD_SELECTED_CONTAINERS));
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
   *
   * @param json JSON object to inspect
   * @return true when an ID-only facility entry is present
   */
  private boolean containsFacilityIdOnly(JSONObject json) {
    if (!json.has(FIELD_FACILITIES_WITH_VEHICLES)) {
      return false;
    }
    try {
      JSONArray array = json.getJSONArray(FIELD_FACILITIES_WITH_VEHICLES);
      for (int i = 0; i < array.length(); i++) {
        JSONObject entry = array.getJSONObject(i);
        if (entry.has(FIELD_FACILITY_ID) && !entry.has(FIELD_FACILITY)) {
          return true;
        }
      }
    } catch (Exception e) {
    }
    return false;
  }

  /**
   * Parses facility and vehicle assignment entries.
   *
   * @param array JSON array of facility assignment entries
   * @return parsed facility assignments
   */
  private List<FacilityWithVehicles> parseFacilitiesWithVehicles(JSONArray array) {
    List<FacilityWithVehicles> result = new ArrayList<>();
    for (int i = 0; i < array.length(); i++) {
      JSONObject entry = array.getJSONObject(i);
      Facility facility = parseFacility(entry.getJSONObject(FIELD_FACILITY));
      List<Vehicle> vehicles = parseVehicles(entry.getJSONArray(FIELD_SELECTED_VEHICLES));
      result.add(new FacilityWithVehicles(facility, vehicles));
    }
    return result;
  }

  /**
   * Parses a facility JSON object.
   *
   * @param json facility JSON object
   * @return parsed facility
   */
  private Facility parseFacility(JSONObject json) {
    String id = json.getString(FIELD_ID);
    String facilityType = json.getString(FIELD_FACILITY_TYPE);
    String status = json.getString(FIELD_STATUS);
    Location location = parseLocation(json.getJSONObject(FIELD_LOCATION));
    double storageCapacity = readDoubleFlexible(
        json, FIELD_STORAGE_CAPACITY, FIELD_VALUE, FIELD_KILOGRAMS_LOWERCASE, FIELD_KILOGRAMS);
    double processingCapacity = readDoubleFlexible(
        json, FIELD_PROCESSING_CAPACITY, FIELD_VALUE, FIELD_KILOGRAMS_PER_DAY);
    int unloadingTime = readIntFlexible(json, FIELD_UNLOADING_TIME, FIELD_TIME_VALUE, FIELD_MINUTES);
    double openingFixedCost = readDoubleFlexible(json, FIELD_OPENING_FIXED_COST, FIELD_AMOUNT);
    double currentFillingLevel = readDoubleFlexible(json, FIELD_CURRENT_FILLING_LEVEL, FIELD_WASTE_DEMAND_VALUE);
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

  /**
   * Parses a vehicle array.
   *
   * @param array JSON array of vehicles
   * @return parsed vehicles
   */
  private List<Vehicle> parseVehicles(JSONArray array) {
    List<Vehicle> result = new ArrayList<>();
    for (int i = 0; i < array.length(); i++) {
      result.add(parseVehicle(array.getJSONObject(i)));
    }
    return result;
  }

  /**
   * Parses a vehicle JSON object.
   *
   * @param json vehicle JSON object
   * @return parsed vehicle
   */
  private Vehicle parseVehicle(JSONObject json) {
    String id = json.getString(FIELD_ID);
    String vehicleType = json.getString(FIELD_VEHICLE_TYPE);
    double capacityKilograms =
      readDoubleFlexible(json, FIELD_CAPACITY_KILOGRAMS, FIELD_KILOGRAMS, FIELD_KILOGRAMS_LOWERCASE);
    double capacityLiters = readDoubleFlexible(json, FIELD_CAPACITY_LITERS, FIELD_LITERS);
    double costPerKilometer = readDoubleFlexible(json, FIELD_COST_PER_KILOMETER, FIELD_AMOUNT);
    return new Vehicle(id, vehicleType, capacityKilograms, capacityLiters, costPerKilometer);
  }

  /**
   * Parses a container array.
   *
   * @param array JSON array of containers
   * @return parsed containers
   */
  private List<Container> parseContainers(JSONArray array) {
    List<Container> result = new ArrayList<>();
    for (int i = 0; i < array.length(); i++) {
      result.add(parseContainer(array.getJSONObject(i)));
    }
    return result;
  }

  /**
   * Parses an optional maximum budget object.
   *
   * @param json maximum budget JSON object, or null
   * @return parsed maximum budget, or null when absent
   */
  private MaximumBudget parseMaximumBudget(JSONObject json) {
    if (json == null) {
      return null;
    }
    double amount = json.getDouble(FIELD_AMOUNT);
    String currency = json.optString(FIELD_CURRENCY, DEFAULT_CURRENCY);
    return new MaximumBudget(amount, currency);
  }

  /**
   * Parses a container JSON object.
   *
   * @param json container JSON object
   * @return parsed container
   */
  private Container parseContainer(JSONObject json) {
    String id = json.getString(FIELD_ID);
    String serviceZone = json.optString(FIELD_SERVICE_ZONE, null);
    if (serviceZone != null && serviceZone.isBlank()) {
      serviceZone = null;
    }
    WasteType wasteType = WasteType.valueOf(json.getString(FIELD_WASTE_TYPE));
    Location location = parseLocation(json.getJSONObject(FIELD_LOCATION));
    double capacityLiters = readDoubleFlexible(json, FIELD_CAPACITY_LITERS, FIELD_LITERS);
    double dailyDemandLitersPerDay =
      readDoubleFlexible(json, FIELD_DAILY_DEMAND_LITERS_PER_DAY, FIELD_LITERS_PER_DAY);
    return new Container(id, location, wasteType, capacityLiters, dailyDemandLitersPerDay, serviceZone);
  }

  /**
   * Parses a location JSON object.
   *
   * @param json location JSON object
   * @return parsed location
   */
  private Location parseLocation(JSONObject json) {
    double latitude = json.getDouble(FIELD_LATITUDE);
    double longitude = json.getDouble(FIELD_LONGITUDE);
    String postalAddress = json.getString(FIELD_POSTAL_ADDRESS);
    String gisReference = readStringFlexible(json, FIELD_GIS_REFERENCE, FIELD_GIS_REFERENCE_LEGACY);
    return new Location(latitude, longitude, postalAddress, gisReference);
  }

  /**
   * Reads a numeric field stored either directly or inside a nested object.
   *
   * @param json source JSON object
   * @param fieldName direct or object field name
   * @param nestedFields candidate nested numeric field names inside the object
   * @return parsed double value
   */
  private double readDoubleFlexible(JSONObject json, String fieldName, String... nestedFields) {
    Object value = json.get(fieldName);
    if (value instanceof JSONObject objectValue) {
      for (String nestedField : nestedFields) {
        if (objectValue.has(nestedField)) {
          return objectValue.getDouble(nestedField);
        }
      }
      throw new IllegalArgumentException(ERROR_INVALID_NUMERIC_FIELD + fieldName);
    }
    if (value instanceof Number numberValue) {
      return numberValue.doubleValue();
    }
    throw new IllegalArgumentException(ERROR_INVALID_NUMERIC_FIELD + fieldName);
  }

  /**
   * Reads an integer field stored either directly or inside a nested object.
   *
   * @param json source JSON object
   * @param fieldName direct or object field name
   * @param nestedFields candidate nested integer field names inside the object
   * @return parsed integer value
   */
  private int readIntFlexible(JSONObject json, String fieldName, String... nestedFields) {
    Object value = json.get(fieldName);
    if (value instanceof JSONObject objectValue) {
      for (String nestedField : nestedFields) {
        if (objectValue.has(nestedField)) {
          return objectValue.getInt(nestedField);
        }
      }
      throw new IllegalArgumentException(ERROR_INVALID_INTEGER_FIELD + fieldName);
    }
    if (value instanceof Number numberValue) {
      return numberValue.intValue();
    }
    throw new IllegalArgumentException(ERROR_INVALID_INTEGER_FIELD + fieldName);
  }

  /**
   * Reads a required string field accepting equivalent field names.
   *
   * @param json source JSON object
   * @param fieldNames supported field names
   * @return parsed string value
   */
  private String readStringFlexible(JSONObject json, String... fieldNames) {
    for (String fieldName : fieldNames) {
      if (json.has(fieldName)) {
        return json.getString(fieldName);
      }
    }
    return json.getString(fieldNames[0]);
  }
}
