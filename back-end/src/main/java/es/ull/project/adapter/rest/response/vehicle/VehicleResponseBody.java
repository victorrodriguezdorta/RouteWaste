package es.ull.project.adapter.rest.response.vehicle;

import java.util.UUID;

/**
 * Data Transfer Object representing a Vehicle response
 * This class is used to send Vehicle data in HTTP responses
 * It contains all the data of a vehicle in a structured format
 */
public class VehicleResponseBody {

    /**
     * Unique identifier of the vehicle
     */
    public UUID id;

    /**
     * Type of the vehicle (COLLECTION_TRUCK, TRANSFER_TRUCK, SUPPORT_VEHICLE)
     */
    public String vehicleType;

    /**
     * Transport capacity of the vehicle
     */
    public TransportCapacityData transportCapacity;

    /**
     * Variable cost per kilometer traveled
     */
    public CostPerKilometerData costPerKilometer;

    /**
     * Data structure for transport capacity information
     */
    public static class TransportCapacityData {
        /**
         * Numeric value of the capacity
         */
        public double value;

        /**
         * Unit of quantity (e.g., kg, tons, liters)
         */
        public String quantityUnit;

        /**
         * Time unit (e.g., DAYS, HOURS)
         */
        public String timeUnit;
    }

    /**
     * Data structure for cost per kilometer information
     */
    public static class CostPerKilometerData {
        /**
         * Amount of the cost
         */
        public double amount;

        /**
         * Currency code (e.g., EUR, USD)
         * This field is optional
         */
        public String currency;
    }
}
