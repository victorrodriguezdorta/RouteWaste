package es.ull.project.adapter.mongodb;

/**
 * MongoFields
 *
 * Defines the field names used in MongoDB documents.
 * This class centralizes all field names to avoid hardcoding strings
 * throughout the codebase and to ensure consistency.
 */
public class MongoFields {

    private static final String ERROR_UTILITY_CLASS = "This is a utility class and cannot be instantiated";

    /**
     * Common fields.
     */
    public static final String ID = "_id";
    public static final String INFRASTRUCTURE_PLAN_ID = "infrastructurePlanId";

    /**
     * Container fields.
     */
    public static final String LOCATION = "location";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String POSTAL_ADDRESS = "postalAddress";
    public static final String GIS_REFERENCE = "gisReference";
    public static final String WASTE_TYPE = "wasteType";
    public static final String CAPACITY_LITERS = "capacityLiters";
    public static final String DAILY_DEMAND_LITERS_PER_DAY = "dailyDemandLitersPerDay";
    public static final String SERVICE_ZONE = "serviceZone";
    public static final String CONTAINER_ID = "containerId";
    public static final String DAILY_FILLING_LITERS = "dailyFillingLiters";

    /**
     * ContainerDailyState ids stored in InfrastructurePlan documents.
     */
    public static final String CONTAINER_DAILY_STATE_IDS = "containerDailyStateIds";
    /**
     * WasteDemand fields (used by Facility and ServiceAssignment).
     */
    public static final String WASTE_DEMAND = "wasteDemand";
    public static final String WASTE_DEMAND_VALUE = "value";
    public static final String WASTE_DEMAND_QUANTITY_UNIT = "quantityUnit";
    public static final String WASTE_DEMAND_TIME_UNIT = "timeUnit";

    /**
     * Facility fields.
     */
    public static final String FACILITY_TYPE = "facilityType";
    public static final String STORAGE_CAPACITY = "storageCapacity";
    public static final String PROCESSING_CAPACITY = "processingCapacity";
    public static final String UNLOADING_TIME = "unloadingTime";
    public static final String CAPACITY = "capacity";
    public static final String CAPACITY_VALUE = "value";
    public static final String CAPACITY_QUANTITY_UNIT = "quantityUnit";
    public static final String CAPACITY_TIME_UNIT = "timeUnit";
    public static final String TIME_VALUE = "value";
    public static final String OPENING_FIXED_COST = "openingFixedCost";
    public static final String OPENING_FIXED_COST_AMOUNT = "amount";
    public static final String OPENING_FIXED_COST_CURRENCY = "currency";
    public static final String STATUS = "status";
    public static final String ASSIGNED_WASTE_DEMAND = "assignedWasteDemand";
    public static final String CURRENT_FILLING_LEVEL = "currentFillingLevel";

    /**
     * Vehicle fields.
     */
    public static final String VEHICLE_TYPE = "vehicleType";
    public static final String CAPACITY_Kilograms = "capacityKilograms";
    public static final String CAPACITY_Kilograms_VALUE = "Kilograms";
    public static final String CAPACITY_liters = "CapacityLiters";
    public static final String CAPACITY_liters_VALUE = "liters";
    public static final String COST_PER_KILOMETER = "costPerKilometer";
    public static final String COST_PER_KILOMETER_AMOUNT = "amount";
    public static final String COST_PER_KILOMETER_CURRENCY = "currency";

    /**
     * ServiceAssignment fields.
     */
    public static final String ASSIGNED_CONTAINERS = "assignedContainers";
    public static final String FACILITY_ID = "facilityId";

    /**
     * InfrastructurePlan fields.
     */
    public static final String PERIOD = "period";
    public static final String SELECTED_FACILITIES = "selectedFacilities";
    public static final String SERVICE_ASSIGNMENTS = "serviceAssignments";
    public static final String DAILY_PLANS = "dailyPlans";
    public static final String SERVICE_POLICIES = "servicePolicies";
    public static final String MAX_SERVICE_DISTANCE = "maxServiceDistance";
    public static final String MAX_SERVICE_TIME = "maxServiceTime";
    public static final String MAX_INFRASTRUCTURE_COUNT = "maxInfrastructureCount";
    public static final String MAX_EMISSIONS = "maxEmissions";
    public static final String MAX_BUDGET = "maxBudget";
    public static final String MAX_BUDGET_AMOUNT = "amount";
    public static final String MAX_BUDGET_CURRENCY = "currency";
    public static final String ESTIMATED_TOTAL_COST = "estimatedTotalCost";
    public static final String ESTIMATED_TOTAL_COST_AMOUNT = "amount";
    public static final String ESTIMATED_TOTAL_COST_CURRENCY = "currency";
    public static final String TOTAL_COLLECTED_KILOGRAMS = "totalCollectedKilograms";
    public static final String TOTAL_COLLECTED_LITERS = "totalCollectedLiters";
    public static final String TOTAL_DISTANCE_METERS = "totalDistanceMeters";
    public static final String NUMBER_OF_DAYS = "numberOfDays";
    public static final String AVERAGE_PICKUP_TIME_MINUTES = "averagePickupTimeMinutes";
    public static final String EXECUTED_AT = "executedAt";

    /**
     * DailyPlan fields.
     */
    public static final String SERVICE_DATE = "serviceDate";
    public static final String PLAN_DAY = "planDay";
    public static final String VEHICLE = "vehicle";
    public static final String STOPS = "stops";
    public static final String SEQUENCE = "sequence";
    public static final String STOP_TYPE = "type";
    public static final String COLLECTED_KILOGRAMS = "collectedKilograms";
    public static final String COLLECTED_LITERS = "collectedLiters";
    public static final String DISTANCE_FROM_PREVIOUS_METERS = "distanceFromPreviousMeters";
    public static final String CUMULATIVE_DISTANCE_METERS = "cumulativeDistanceMeters";

    /**
     * Private constructor to prevent instantiation.
     */
    private MongoFields() {
        throw new UnsupportedOperationException(ERROR_UTILITY_CLASS);
    }
}
