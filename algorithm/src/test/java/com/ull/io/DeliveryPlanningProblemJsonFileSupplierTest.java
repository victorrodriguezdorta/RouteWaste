package com.ull.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ull.domain.DeliveryPlanningProblem;
import com.ull.domain.entity.Facility;
import com.ull.domain.entity.Vehicle;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class DeliveryPlanningProblemJsonFileSupplierTest {

  private static final double DELTA = 0.000001;

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

    assertEquals("20", facility.getLocation().getGisReference());
    assertEquals(300000.0, facility.getStorageCapacityKilograms(), DELTA);
    assertEquals(3000.0, facility.getProcessingCapacityKilogramsPerDay(), DELTA);
    assertEquals(15.0, facility.getUnloadingTimeMinutes(), DELTA);
    assertEquals(1000.0, vehicle.getCapacityKilograms(), DELTA);
    assertEquals(1, problem.getNumberOfContainers());
  }
}
