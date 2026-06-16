package es.ull.project.adapter.rest.json;

/**
 * JsonFields
 *
 * Centralized constants for JSON field names used by REST adapters.
 * This class defines all field names that appear in JSON request and response bodies
 * to ensure consistency across serializers, deserializers and avoid typos.
 *
 * Using constants allows easy refactoring and provides a single source
 * of truth for field naming conventions.
 */
public final class JsonFields {

    /**
     * Error message for utility class instantiation.
     */
    private static final String UTILITY_CLASS_INSTANTIATION_ERROR = "This is a utility class and cannot be instantiated";

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static constants.
     */
    private JsonFields() {
        throw new UnsupportedOperationException(UTILITY_CLASS_INSTANTIATION_ERROR);
    }

    /**
     * Common fields
     */
    public static final String ID = "id";
    public static final String NAME = "name";

    /**
     * Bulk import wrapper fields
     */
    public static final String CONTAINERS = "containers";
    public static final String FACILITIES = "facilities";
    public static final String VEHICLES = "vehicles";

    /**
     * Vehicle fields
     */
    public static final String VEHICLE_TYPE = "vehicleType";
    public static final String CAPACITY_Kilograms = "capacityKilograms";
    public static final String CAPACITY_liters = "CapacityLiters";
    public static final String COST_PER_KILOMETER = "costPerKilometer";

    /**
     * Capacity fields
     */
    public static final String CAPACITY_VALUE = "value";
    public static final String QUANTITY_UNIT = "quantityUnit";
    public static final String TIME_UNIT = "timeUnit";

    /**
     * Cost fields
     */
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";

    /**
     * Container fields
     */
    public static final String LOCATION = "location";
    public static final String WASTE_TYPE = "wasteType";
    public static final String CAPACITY_LITERS = "capacityLiters";
    public static final String DAILY_DEMAND_LITERS_PER_DAY = "dailyDemandLitersPerDay";
    public static final String SERVICE_ZONE = "serviceZone";
    public static final String LITERS = "liters";
    public static final String LITERS_PER_DAY = "litersPerDay";

    /**
     * WasteDemand fields (used by Facility and ServiceAssignment)
     */
    public static final String WASTE_DEMAND = "wasteDemand";

    /**
     * Location fields
     */
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String POSTAL_ADDRESS = "postalAddress";
    public static final String GIS_REFERENCE = "gisReference";

    /**
     * Facility fields
     */
    public static final String FACILITY_TYPE = "facilityType";
    public static final String CAPACITY = "capacity";
    public static final String STORAGE_CAPACITY = "storageCapacity";
    public static final String PROCESSING_CAPACITY = "processingCapacity";
    public static final String UNLOADING_TIME = "unloadingTime";
    public static final String TIME_VALUE = "timeValue";
    public static final String CURRENT_FILLING_LEVEL = "currentFillingLevel";
    public static final String WASTE_DEMAND_VALUE = "wasteDemandValue";
    public static final String OPENING_FIXED_COST = "openingFixedCost";
    public static final String STATUS = "status";

    /**
     * Entity fields
     */
    public static final String FACILITY = "facility";
    public static final String CONTAINER = "container";

    /**
     * ServiceAssignment fields
     */
    public static final String DISTANCE = "distance";
    public static final String SERVICE_TIME = "serviceTime";
    public static final String TRANSPORT_COST = "transportCost";

    /**
     * InfrastructurePlan fields
     */
    public static final String PERIOD = "period";
    public static final String MAX_BUDGET = "maxBudget";
    public static final String SERVICE_POLICIES = "servicePolicies";
    public static final String SELECTED_FACILITY_IDS = "selectedFacilityIds";
    public static final String SERVICE_ASSIGNMENT_IDS = "serviceAssignmentIds";

    /**
     * ServicePolicies fields
     */
    public static final String MAX_SERVICE_DISTANCE = "maxServiceDistance";
    public static final String MAX_SERVICE_TIME = "maxServiceTime";
    public static final String MAX_INFRASTRUCTURE_COUNT = "maxInfrastructureCount";
    public static final String MAX_EMISSIONS = "maxEmissions";

    /**
     * ServiceAssignment fields
     */
    public static final String CONTAINER_ID = "containerId";
    public static final String FACILITY_ID = "facilityId";

    /**
     * Distance fields
     */
    public static final String METERS = "meters";
    public static final String KILOMETERS = "kilometers";
    public static final String MILES = "miles";

    /**
     * ServiceTime fields
     */
    public static final String MINUTES = "minutes";
    public static final String HOURS = "hours";
    public static final String SECONDS = "seconds";
}
