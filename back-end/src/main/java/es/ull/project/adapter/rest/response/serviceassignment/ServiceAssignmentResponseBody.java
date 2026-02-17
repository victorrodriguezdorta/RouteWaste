package es.ull.project.adapter.rest.response.serviceassignment;

import java.util.UUID;

import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;

/**
 * Data Transfer Object representing a ServiceAssignment response
 * This class is used to send ServiceAssignment data in HTTP responses
 * It contains all the data of a service assignment in a structured format,
 * including complete information about the container and facility entities.
 */
public class ServiceAssignmentResponseBody {

    /**
     * Unique identifier of the service assignment
     */
    public UUID id;

    /**
     * Complete container entity with all its data
     */
    public ContainerResponseBody container;

    /**
     * Complete facility entity with all its data
     */
    public FacilityResponseBody facility;

    /**
     * Waste demand of the container
     */
    public WasteDemandData wasteDemand;

    /**
     * Distance between container and facility
     */
    public DistanceData distance;

    /**
     * Service time required for the assignment
     */
    public ServiceTimeData serviceTime;

    /**
     * Transportation cost applied for the assignment
     */
    public TransportCostData transportCost;

    /**
     * Data structure for waste demand information
     */
    public static class WasteDemandData {
        /**
         * Numeric value of the demand
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
     * Data structure for distance information
     */
    public static class DistanceData {
        /**
         * Distance in meters
         */
        public double meters;

        /**
         * Distance in kilometers
         */
        public double kilometers;
    }

    /**
     * Data structure for service time information
     */
    public static class ServiceTimeData {
        /**
         * Service time in minutes
         */
        public double minutes;
    }

    /**
     * Data structure for transportation cost information
     */
    public static class TransportCostData {
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
