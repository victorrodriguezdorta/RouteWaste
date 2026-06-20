package com.ull.io;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.Vehicle;
import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class DeliveryPlanningProblemJsonFileSupplierTest {

  private static final double DELTA = 0.000001;
  private static final String EXPECTED_FACILITY_GIS_REFERENCE = "20";
  private static final double EXPECTED_STORAGE_CAPACITY_KILOGRAMS = 300000.0;
  private static final double EXPECTED_PROCESSING_CAPACITY_KILOGRAMS_PER_DAY = 3000.0;
  private static final double EXPECTED_UNLOADING_TIME_MINUTES = 15.0;
  private static final double EXPECTED_VEHICLE_CAPACITY_KILOGRAMS = 1000.0;
  private static final int EXPECTED_CONTAINER_COUNT = 1;

  /**
   * Verifies that a backend-serialized algorithm payload is parsed into a planning problem.
   */
  @Test
  void shouldParseBackendSerializedAlgorithmPayload() {
    JSONObject payload = new JSONObject("""
        {
          "facilitiesWithVehicles": [
            {
              "facility": {
                "id": "facility-1",
                "name": { "value": "Facility 1" },
                "facilityType": "OPERATIONAL_BASE",
                "location": {
                  "latitude": 28.4701,
                  "longitude": -16.276863,
                  "postalAddress": "la cuesta",
                  "point": { "longitude": -16.276863, "latitude": 28.4701 },
                  "gisreference": "20"
                },
                "storageCapacity": { "kilograms": 300000.0 },
                "processingCapacity": { "kilogramsPerDay": 3000.0 },
                "unloadingTime": { "minutes": 15, "seconds": 900 },
                "openingFixedCost": { "amount": 200.0, "currency": { "code": "EUR" } },
                "status": "CANDIDATE",
                "currentFillingLevel": 0.0
              },
              "selectedVehicles": [
                {
                  "id": "vehicle-1",
                  "name": { "value": "Vehicle 1" },
                  "vehicleType": "TRANSFER_TRUCK",
                  "capacityKilograms": 1000.0,
                  "capacityLiters": 1000.0,
                  "costPerKilometer": 10.0
                }
              ]
            }
          ],
          "selectedContainers": [
            {
              "id": "container-1",
              "name": "Container 1",
              "location": {
                "latitude": 28.459611,
                "longitude": -16.256356,
                "postalAddress": "santa cruz centro",
                "gisReference": "23"
              },
              "wasteType": "ORGANIC",
              "capacityLiters": { "liters": 100.0 },
              "dailyDemandLitersPerDay": { "litersPerDay": 60.0 },
              "serviceZone": "NEIGHBORHOOD"
            }
          ],
          "numberOfDays": 4,
          "averagePickupTimeMinutes": 15,
          "maxBudget": { "amount": 1000000.0, "currency": "EUR" }
        }
        """);
    DeliveryPlanningProblem problem = new DeliveryPlanningProblemJsonFileSupplier()
        .get(payload)
        .findFirst()
        .orElseThrow();
    Facility facility = problem.getFacilitiesWithVehicles().get(0).getFacility();
    Vehicle vehicle = problem.getFacilitiesWithVehicles().get(0).getVehicles().get(0);
    assertEquals(EXPECTED_FACILITY_GIS_REFERENCE, facility.getLocation().getGisReference());
    assertEquals(EXPECTED_STORAGE_CAPACITY_KILOGRAMS, facility.getStorageCapacityKilograms(), DELTA);
    assertEquals(EXPECTED_PROCESSING_CAPACITY_KILOGRAMS_PER_DAY, facility.getProcessingCapacityKilogramsPerDay(), DELTA);
    assertEquals(EXPECTED_UNLOADING_TIME_MINUTES, facility.getUnloadingTimeMinutes(), DELTA);
    assertEquals(EXPECTED_VEHICLE_CAPACITY_KILOGRAMS, vehicle.getCapacityKilograms(), DELTA);
    assertEquals(EXPECTED_CONTAINER_COUNT, problem.getNumberOfContainers());
  }
}
