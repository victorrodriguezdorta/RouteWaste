package es.ull.project.adapter.rest.controller;

/**
 * ApiRoutes
 * 
 * Centralized API route configuration.
 * This class defines all base paths and prefixes used across REST controllers
 * to maintain consistency and facilitate URL changes.
 * 
 * Using constants allows modifying API versioning or base paths in a single
 * location without editing multiple controller files.
 */
public final class ApiRoutes {

    /**
     * Error message for utility class instantiation attempts.
     */
    private static final String UTILITY_CLASS_INSTANTIATION_ERROR = "This is a utility class and cannot be instantiated";

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static constants.
     */
    private ApiRoutes() {
        throw new UnsupportedOperationException(UTILITY_CLASS_INSTANTIATION_ERROR);
    }

    /**
     * Base API prefix used by all controllers.
     * Example: "/api/v1"
     */
    public static final String API_BASE = "/api/v1";

    /**
     * Vehicle resource endpoints.
     * Full path: /api/v1/vehicles
     */
    public static final String VEHICLES = API_BASE + "/vehicles";

    /**
     * Container resource endpoints.
     * Full path: /api/v1/containers
     */
    public static final String CONTAINERS = API_BASE + "/containers";

    /**
     * Facility resource endpoints.
     * Full path: /api/v1/facilities
     */
    public static final String FACILITIES = API_BASE + "/facilities";

    /**
     * Infrastructure plan resource endpoints.
     * Full path: /api/v1/infrastructure-plans
     */
    public static final String INFRASTRUCTURE_PLANS = API_BASE + "/infrastructure-plans";

    /**
     * Service assignment resource endpoints.
     * Full path: /api/v1/service-assignments
     */
    public static final String SERVICE_ASSIGNMENTS = API_BASE + "/service-assignments";

    /**
     * Algorithm resource endpoints.
     * Full path: /api/v1/algorithms
     */
    public static final String ALGORITHMS = API_BASE + "/algorithms";

    /**
     * Dashboard-style aggregate read (entity counts and recent infrastructure plans).
     * Full path: /api/v1/application-overview
     */
    public static final String APPLICATION_OVERVIEW = API_BASE + "/application-overview";
}
