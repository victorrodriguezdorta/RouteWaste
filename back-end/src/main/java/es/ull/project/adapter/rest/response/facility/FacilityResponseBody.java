package es.ull.project.adapter.rest.response.facility;

import java.util.UUID;

/**
 * Data Transfer Object representing a Facility response
 * This class is used to send Facility data in HTTP responses
 * It contains all the data of a facility in a structured format
 */
public class FacilityResponseBody {

    /**
     * Unique identifier of the facility
     */
    public UUID id;

    /**
     * Type of the facility (TRUCK_BASE, TRANSFER_STATION, TREATMENT_PLANT)
     */
    public String facilityType;

    /**
     * Physical location of the facility
     */
    public LocationData location;

    /**
     * Maximum capacity of the facility
     */
    public CapacityData capacity;

    /**
     * Fixed cost to open the facility
     */
    public OpeningFixedCostData openingFixedCost;

    /**
     * Current status of the facility (OPEN, CLOSED, PLANNED, DISCARDED)
     */
    public String status;

    /**
     * Accumulated waste demand assigned to the facility
     */
    public AssignedWasteDemandData assignedWasteDemand;

    /**
     * Data structure for location information
     */
    public static class LocationData {
        /**
         * Latitude in decimal degrees
         */
        public double latitude;

        /**
         * Longitude in decimal degrees
         */
        public double longitude;

        /**
         * Postal address
         */
        public String postalAddress;

        /**
         * GIS reference (Geographic Information System)
         */
        public String gisReference;
    }

    /**
     * Data structure for capacity information
     */
    public static class CapacityData {
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
     * Data structure for opening fixed cost information
     */
    public static class OpeningFixedCostData {
        /**
         * Amount of the fixed cost
         */
        public double amount;

        /**
         * Currency code (e.g., EUR, USD)
         * This field is optional
         */
        public String currency;
    }

    /**
     * Data structure for assigned waste demand information
     */
    public static class AssignedWasteDemandData {
        /**
         * Numeric value of the waste demand
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
}
