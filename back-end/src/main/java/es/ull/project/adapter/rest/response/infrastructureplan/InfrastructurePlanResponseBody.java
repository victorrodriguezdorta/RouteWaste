package es.ull.project.adapter.rest.response.infrastructureplan;

import java.util.UUID;

/**
 * Data Transfer Object representing an InfrastructurePlan response
 * This class is used to send InfrastructurePlan data in HTTP responses
 * It contains all the essential data of an infrastructure plan in a structured format
 */
public class InfrastructurePlanResponseBody {

    /**
     * Unique identifier of the infrastructure plan
     */
    public UUID id;

    /**
     * Planning period of the plan (e.g., "2026", "2026-Q1")
     */
    public String period;

    /**
     * Maximum budget allowed for the plan
     */
    public MaximumBudgetData maxBudget;

    /**
     * Estimated total cost of the plan
     */
    public TotalCostData estimatedTotalCost;

    /**
     * Service policies to comply with
     */
    public ServicePoliciesData servicePolicies;

    /**
     * Data structure for maximum budget information
     */
    public static class MaximumBudgetData {
        /**
         * Amount of the maximum budget
         */
        public double amount;

        /**
         * Currency code (e.g., EUR, USD)
         * This field is optional
         */
        public String currency;
    }

    /**
     * Data structure for total cost information
     */
    public static class TotalCostData {
        /**
         * Amount of the total cost
         */
        public double amount;

        /**
         * Currency code (e.g., EUR, USD)
         * This field is optional
         */
        public String currency;
    }

    /**
     * Data structure for service policies information
     */
    public static class ServicePoliciesData {
        /**
         * Maximum service distance allowed (meters)
         * This field is optional
         */
        public Double maxServiceDistance;

        /**
         * Maximum service time allowed (minutes)
         * This field is optional
         */
        public Integer maxServiceTime;

        /**
         * Maximum number of infrastructures allowed
         * This field is optional
         */
        public Integer maxInfrastructureCount;

        /**
         * Maximum emission limit (kg CO2)
         * This field is optional
         */
        public Double maxEmissions;
    }
}
